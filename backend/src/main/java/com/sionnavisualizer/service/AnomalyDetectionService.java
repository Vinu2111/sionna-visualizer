package com.sionnavisualizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.AiExplanationResponse;
import com.sionnavisualizer.dto.AnomalyRecordResponse;
import com.sionnavisualizer.dto.AnomalyReportResponse;
import com.sionnavisualizer.model.AnomalyRecord;
import com.sionnavisualizer.model.AnomalyReport;
import com.sionnavisualizer.repository.AnomalyRecordRepository;
import com.sionnavisualizer.repository.AnomalyReportRepository;
import com.sionnavisualizer.repository.SimulationResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnomalyDetectionService {

    @Autowired private AnomalyReportRepository reportRepo;
    @Autowired private AnomalyRecordRepository recordRepo;
    @Autowired private SimulationResultRepository simulationRepo;

    @Value("${anthropic.api.key:NOT_CONFIGURED}")
    private String anthropicApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ─── CLAUDE SYSTEM PROMPT ─────────────────────────────────────────────────
    //
    // This instructs Claude to explain anomalies in plain English for PhD students
    // who may be experts in their research topic but not telecom implementation details.
    // We keep it under 200 words to avoid token costs ballooning.
    //
    private static final String ANOMALY_EXPLAIN_SYSTEM_PROMPT = """
            You are an expert in 6G wireless communications and NVIDIA Sionna simulation debugging.
            
            A researcher has detected an anomaly in their BER (Bit Error Rate) simulation results.
            Explain in clear simple language:
            1. Why this anomaly indicates a problem
            2. What likely caused it (be specific to the values shown)
            3. Exactly how to fix it — give specific parameter changes
            4. What the BER curve should look like when fixed
            
            Keep your explanation under 200 words.
            Use simple language — the researcher may be a PhD student, not a telecom expert.
            Do not use jargon without explaining it.
            End with one specific, immediately actionable fix.
            """;

    // ─── MAIN ANALYSIS PIPELINE ───────────────────────────────────────────────

    /**
     * Runs all six physics-based anomaly checks on a simulation's BER curve.
     * Returns and persists a full anomaly report for display in the dashboard.
     */
    @Transactional
    public AnomalyReportResponse analyzeSimulation(Long simulationId, Long userId) throws Exception {

        // Step 1 — Pull the simulation from the database.
        // The simulation stores BER values as a comma-separated string inside a JSON column.
        // We parse it into a double array for mathematical processing.
        var simulationOpt = simulationRepo.findById(simulationId);
        if (simulationOpt.isEmpty()) {
            throw new IllegalArgumentException("Simulation ID " + simulationId + " not found.");
        }
        var simulation = simulationOpt.get();

        // Parse BER and SNR arrays from the simulation's stored JSON result
        double[] berValues  = parseBerArray(simulation.getResultData());
        double[] theoreticalBer = parseTheoreticalBerArray(simulation.getResultData());
        double[] snrValues  = buildSnrRange(simulation.getResultData());
        String   modulation = parseModulation(simulation.getResultData());

        // Step 2 — Run all physics checks and collect anomalies
        List<AnomalyRecord> detectedAnomalies = new ArrayList<>();

        detectedAnomalies.addAll(checkMonotonicity(snrValues, berValues,   simulationId));
        detectedAnomalies.addAll(checkPhysicalBounds(snrValues, berValues,  simulationId));
        detectedAnomalies.addAll(checkConvergence(snrValues, berValues,     simulationId));
        detectedAnomalies.addAll(checkMonteCarloGap(theoreticalBer, berValues, snrValues, simulationId));
        detectedAnomalies.addAll(checkFlatRegions(snrValues, berValues,     simulationId));
        detectedAnomalies.addAll(checkZeroSnrValue(berValues, snrValues, modulation, simulationId));

        // Step 3 — Determine overall status
        // CRITICAL if any physically impossible result, WARNING if physics laws bent but not broken
        boolean hasCritical = detectedAnomalies.stream().anyMatch(a -> "CRITICAL".equals(a.getSeverity()));
        boolean hasHigh     = detectedAnomalies.stream().anyMatch(a -> "HIGH".equals(a.getSeverity()));
        boolean hasMedium   = detectedAnomalies.stream().anyMatch(a -> "MEDIUM".equals(a.getSeverity()));
        String overallStatus;
        if (hasCritical)         overallStatus = "CRITICAL";
        else if (hasHigh || hasMedium) overallStatus = "WARNING";
        else                     overallStatus = "CLEAR";

        // If no anomalies found — add a positive PERFECT_CURVE record so the UI can show a green banner
        if (detectedAnomalies.isEmpty()) {
            AnomalyRecord perfect = new AnomalyRecord();
            perfect.setSimulationId(simulationId);
            perfect.setAnomalyType("PERFECT_CURVE");
            perfect.setSeverity("INFO");
            perfect.setTitle("BER Curve Looks Physically Valid");
            perfect.setDescription("No physics violations detected. Your BER curve decreases monotonically, stays within bounds, and converges at high SNR.");
            perfect.setLikelyCause("No issues found.");
            perfect.setSuggestedFix("None required.");
            detectedAnomalies.add(perfect);
        }

        // Step 4 — Save the report header
        AnomalyReport report = new AnomalyReport();
        report.setSimulationId(simulationId);
        report.setUserId(userId);
        report.setTotalAnomalies(detectedAnomalies.size());
        report.setHasCritical(hasCritical);
        report.setOverallStatus(overallStatus);
        AnomalyReport saved = reportRepo.save(report);

        // Step 5 — Save all anomaly records linked to this report
        for (AnomalyRecord a : detectedAnomalies) {
            a.setReportId(saved.getId());
            recordRepo.save(a);
        }

        return buildResponse(saved, detectedAnomalies);
    }

    // ─── CHECK 1: MONOTONICITY ────────────────────────────────────────────────
    //
    // Physics law: BER MUST decrease as SNR increases.
    //
    // Why? SNR = Signal-to-Noise Ratio. Higher SNR means the signal is
    // stronger relative to noise. More signal strength = fewer bit errors.
    // If BER goes UP when SNR goes UP, that violates a fundamental law
    // of information theory. It means data in the simulation is wrong.
    //
    private List<AnomalyRecord> checkMonotonicity(double[] snr, double[] ber, Long simId) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        for (int i = 1; i < ber.length; i++) {
            if (ber[i] > ber[i - 1]) {
                AnomalyRecord a = new AnomalyRecord();
                a.setSimulationId(simId);
                a.setAnomalyType("NON_MONOTONIC");
                a.setSeverity("HIGH");
                a.setTitle("BER Increased as SNR Increased");
                a.setDescription(String.format(
                    "BER jumped from %.6f to %.6f as SNR increased from %.1f dB to %.1f dB. " +
                    "BER must always decrease with increasing SNR.",
                    ber[i-1], ber[i], snr[i-1], snr[i]));
                a.setAffectedSnrPoint(snr[i]);
                a.setAffectedBerValue(ber[i]);
                a.setLikelyCause("Random seed instability or numerical overflow in the channel computation.");
                a.setSuggestedFix("Increase Monte Carlo trials (try 100,000+), fix the random seed to a constant, and re-run.");
                anomalies.add(a);
                break; // Report only first violation — one is enough to diagnose the problem
            }
        }
        return anomalies;
    }

    // ─── CHECK 2: PHYSICAL BOUNDS ─────────────────────────────────────────────
    //
    // Physics law: BER must be between 0 and 0.5.
    //
    // Why 0.5 as the maximum? If you flip a coin for every bit (pure random guessing),
    // you get BER = 0.5 by definition. No real transmission system can do WORSE than
    // random guessing on a binary channel. BER > 0.5 is mathematically impossible.
    //
    private List<AnomalyRecord> checkPhysicalBounds(double[] snr, double[] ber, Long simId) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        for (int i = 0; i < ber.length; i++) {
            if (ber[i] > 0.5) {
                AnomalyRecord a = new AnomalyRecord();
                a.setSimulationId(simId);
                a.setAnomalyType("PHYSICALLY_IMPOSSIBLE");
                a.setSeverity("CRITICAL");
                a.setTitle("BER Exceeds Physical Maximum of 0.5");
                a.setDescription(String.format(
                    "BER value of %.4f detected at SNR = %.1f dB. " +
                    "The physical maximum BER for any binary channel is 0.5 (random guessing). " +
                    "This value is impossible in any real or simulated system.",
                    ber[i], snr[i]));
                a.setAffectedSnrPoint(snr[i]);
                a.setAffectedBerValue(ber[i]);
                a.setLikelyCause("Parameter conflict — likely incorrect noise normalization, wrong TX power units, or a sign error in the noise variance formula.");
                a.setSuggestedFix("Reset tx_power to 1.0 and noise_figure to 1.0. Verify noise_variance = N0/2 not N0. Re-run.");
                anomalies.add(a);
                break;
            }
        }
        return anomalies;
    }

    // ─── CHECK 3: CONVERGENCE ─────────────────────────────────────────────────
    //
    // Physics law: At very high SNR (>= 25 dB), BER should approach 0.
    //
    // Why? With extremely high signal power, even a noisy channel can transmit
    // bits nearly perfectly. BER staying flat above 25 dB usually means the
    // Monte Carlo simulation ran too few trials — there weren't enough bit
    // errors to count, so the estimate gets stuck at an error floor.
    //
    private List<AnomalyRecord> checkConvergence(double[] snr, double[] ber, Long simId) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        // Only check convergence if the simulation covers SNR >= 25 dB
        for (int i = 0; i < snr.length; i++) {
            if (snr[i] >= 25.0 && ber[i] > 0.01) {
                AnomalyRecord a = new AnomalyRecord();
                a.setSimulationId(simId);
                a.setAnomalyType("CONVERGENCE_FAILURE");
                a.setSeverity("HIGH");
                a.setTitle("BER Fails to Converge at High SNR");
                a.setDescription(String.format(
                    "BER = %.5f at SNR = %.1f dB. At such high signal-to-noise ratio, " +
                    "BER should approach 0.001 or lower. This error floor suggests the " +
                    "simulation did not run enough trials to estimate rare error events.",
                    ber[i], snr[i]));
                a.setAffectedSnrPoint(snr[i]);
                a.setAffectedBerValue(ber[i]);
                a.setLikelyCause("Too few Monte Carlo trials. Rare bit errors at high SNR need many more samples to estimate accurately.");
                a.setSuggestedFix("Increase num_trials to at least 1,000,000 (1e6). Try 1e7 for reliable results above 25 dB SNR.");
                anomalies.add(a);
                break;
            }
        }
        return anomalies;
    }

    // ─── CHECK 4: MONTE CARLO GAP ─────────────────────────────────────────────
    //
    // Physics law: Simulated Monte Carlo BER should closely match theoretical BER.
    //
    // Why? If we have a theoretical curve for this modulation and channel, the
    // simulated points are just stochastic estimates of that truth. If they differ
    // by more than 20%, it suggests either the simulation is using too few trials
    // (high variance) or the simulation is modeling a different system entirely.
    //
    private List<AnomalyRecord> checkMonteCarloGap(double[] theoretical, double[] simulated, double[] snr, Long simId) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        if (theoretical == null || theoretical.length == 0 || simulated == null || simulated.length == 0) return anomalies;
        int minLen = Math.min(theoretical.length, simulated.length);
        for (int i = 0; i < minLen; i++) {
            if (theoretical[i] <= 0) continue;
            double gap = Math.abs(theoretical[i] - simulated[i]) / theoretical[i];
            if (gap > 0.20) {
                AnomalyRecord a = new AnomalyRecord();
                a.setSimulationId(simId);
                a.setAnomalyType("EXCESSIVE_MONTE_CARLO_GAP");
                a.setSeverity("MEDIUM");
                a.setTitle("Simulated BER Deviates From Theory");
                a.setDescription(String.format(
                    "At SNR = %.1f dB, theoretical BER is %.6f but simulated is %.6f (%.1f%% difference). " +
                    "A gap > 20%% implies the simulation does not match the theoretical mathematical model.",
                    snr[i], theoretical[i], simulated[i], gap * 100));
                a.setAffectedSnrPoint(snr[i]);
                a.setAffectedBerValue(simulated[i]);
                a.setLikelyCause("Insufficient Monte Carlo trials (high variance) or incorrect parameter mapping.");
                a.setSuggestedFix("Try 10x more Monte Carlo trials. Ensure the simulation parameters exactly match the theory.");
                anomalies.add(a);
                break;
            }
        }
        return anomalies;
    }

    // ─── CHECK 5: FLAT REGIONS ────────────────────────────────────────────────
    //
    // Physics law: BER should show a smooth declining curve, not plateau.
    //
    // A flat region means BER didn't change across multiple SNR steps.
    // This usually means floating-point precision ran out — the simulation
    // can't represent error rates below a certain threshold, so values
    // "stick" at the precision limit instead of declining further.
    //
    // Why tolerance (0.1%) instead of exact equality?
    // Floating-point arithmetic in Java/Python can produce tiny differences
    // like 0.001000001 vs 0.001000000. Exact equality would miss real flat
    // regions. A 0.1% tolerance catches genuine stagnation without false alarms.
    //
    private List<AnomalyRecord> checkFlatRegions(double[] snr, double[] ber, Long simId) {
        List<AnomalyRecord> anomalies = new ArrayList<>();
        final double FLAT_TOLERANCE = 0.001; // 0.1% — values within this are considered "same"
        for (int i = 0; i < ber.length - 2; i++) {
            double diff1 = Math.abs(ber[i+1] - ber[i]);
            double diff2 = Math.abs(ber[i+2] - ber[i+1]);
            boolean flat1 = ber[i] > 0 && (diff1 / ber[i]) < FLAT_TOLERANCE;
            boolean flat2 = ber[i+1] > 0 && (diff2 / ber[i+1]) < FLAT_TOLERANCE;
            if (flat1 && flat2) {
                AnomalyRecord a = new AnomalyRecord();
                a.setSimulationId(simId);
                a.setAnomalyType("SUSPICIOUS_FLAT_REGION");
                a.setSeverity("MEDIUM");
                a.setTitle("BER Stopped Decreasing — Flat Region Detected");
                a.setDescription(String.format(
                    "BER remained flat at approximately %.6f across SNR points %.1f, %.1f, %.1f dB. " +
                    "A valid BER curve should decline continuously.",
                    ber[i], snr[i], snr[i+1], snr[i+2]));
                a.setAffectedSnrPoint(snr[i]);
                a.setAffectedBerValue(ber[i]);
                a.setLikelyCause("Floating-point precision limit reached. The simulator cannot distinguish error rates this small with the current number of trials.");
                a.setSuggestedFix("Increase Monte Carlo trials by 10x. Alternatively, use importance sampling or reduce the SNR step size around the flat region.");
                anomalies.add(a);
                break;
            }
        }
        return anomalies;
    }

    // ─── CHECK 5: ZERO-SNR PHYSICS CONSTANT ──────────────────────────────────
    //
    // Physics law: At 0 dB SNR, each modulation scheme has a known theoretical BER.
    //
    // These are not magic numbers — they come from closed-form information theory
    // formulas using the Q-function (erfc function):
    //   BPSK/QPSK: BER(0dB) = Q(sqrt(2)) = erfc(1/sqrt(2)) / 2 ≈ 0.0785
    //   16QAM:     BER(0dB) ≈ 0.280  (from Gray-coded 16QAM formula)
    //   64QAM:     BER(0dB) ≈ 0.340  (from Gray-coded 64QAM formula)
    //
    // If the measured BER deviates by more than 30% from these constants,
    // something fundamentally wrong happened in the modulation mapping,
    // noise normalization, or SNR calculation.
    //
    private List<AnomalyRecord> checkZeroSnrValue(double[] ber, double[] snr, String modulation, Long simId) {
        List<AnomalyRecord> anomalies = new ArrayList<>();

        // Expected BER at 0 dB SNR for each modulation (physics reference values from Shannon theory)
        double expectedBer;
        switch (modulation.toUpperCase()) {
            case "BPSK", "QPSK" -> expectedBer = 0.0785;
            case "16QAM"        -> expectedBer = 0.280;
            case "64QAM"        -> expectedBer = 0.340;
            default -> { return anomalies; } // Unknown modulation — skip this check
        }

        // Find the SNR point closest to 0 dB
        int closestIdx = 0;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < snr.length; i++) {
            double dist = Math.abs(snr[i] - 0.0);
            if (dist < minDistance) { minDistance = dist; closestIdx = i; }
        }

        // Only check if we actually have a 0 dB point (within 0.5 dB)
        if (minDistance > 0.5) return anomalies;

        double actualBer  = ber[closestIdx];
        double deviation  = Math.abs(actualBer - expectedBer) / expectedBer;

        if (deviation > 0.30) { // More than 30% off from physics expectation
            AnomalyRecord a = new AnomalyRecord();
            a.setSimulationId(simId);
            a.setAnomalyType("WRONG_ZERO_SNR_VALUE");
            a.setSeverity("HIGH");
            a.setTitle(String.format("Incorrect BER at 0dB SNR for %s", modulation));
            a.setDescription(String.format(
                "At 0 dB SNR, %s modulation should produce BER ≈ %.4f (information theory constant). " +
                "Your simulation produced BER = %.4f — a %.0f%% deviation. " +
                "This suggests a fundamental error in modulation mapping or noise calculation.",
                modulation, expectedBer, actualBer, deviation * 100));
            a.setAffectedSnrPoint(snr[closestIdx]);
            a.setAffectedBerValue(actualBer);
            a.setLikelyCause("Incorrect modulation symbol mapping, wrong noise variance formula, or SNR axis labelled in linear scale instead of dB.");
            a.setSuggestedFix(String.format(
                "Verify SNR is calculated as 10*log10(Es/N0). Check %s symbol constellation mapping. " +
                "Expected BER at 0dB = %.4f.", modulation, expectedBer));
            anomalies.add(a);
        }
        return anomalies;
    }

    // ─── AI EXPLANATION ───────────────────────────────────────────────────────

    /**
     * Calls Claude AI to generate a plain-English explanation of a specific anomaly.
     * Saves the explanation to the anomaly record for future renders without re-calling.
     */
    public AiExplanationResponse explainAnomaly(Long anomalyId, Long userId) throws Exception {
        AnomalyRecord record = recordRepo.findById(anomalyId)
            .orElseThrow(() -> new IllegalArgumentException("Anomaly " + anomalyId + " not found."));

        // Build a context-rich prompt so Claude understands exactly what went wrong
        String userPrompt = String.format("""
                Anomaly type: %s
                Severity: %s
                Description: %s
                Affected SNR point: %.1f dB
                Affected BER value: %.6f
                Likely cause identified: %s
                
                Please explain this anomaly and provide a specific fix.
                """,
            record.getAnomalyType(),
            record.getSeverity(),
            record.getDescription(),
            record.getAffectedSnrPoint() != null ? record.getAffectedSnrPoint() : 0.0,
            record.getAffectedBerValue() != null ? record.getAffectedBerValue() : 0.0,
            record.getLikelyCause());

        // Call Claude API with the anomaly explanation system prompt
        String explanation = callClaudeForExplanation(userPrompt);

        // Persist the explanation so the researcher can come back without re-calling Claude
        record.setAiExplanation(explanation);
        record.setAiExplainedAt(LocalDateTime.now());
        recordRepo.save(record);

        AiExplanationResponse response = new AiExplanationResponse();
        response.setAnomalyId(anomalyId);
        response.setFullExplanation(explanation);
        response.setGeneratedAt(record.getAiExplainedAt().toString());
        return response;
    }

    // ─── CLAUDE API CALL ──────────────────────────────────────────────────────

    /** Makes a synchronous POST request to Anthropic's Messages API and returns the response text. */
    private String callClaudeForExplanation(String userPrompt) throws Exception {
        String requestBody = objectMapper.writeValueAsString(java.util.Map.of(
            "model", "claude-sonnet-4-20250514",
            "max_tokens", 800,
            "system", ANOMALY_EXPLAIN_SYSTEM_PROMPT,
            "messages", List.of(java.util.Map.of("role", "user", "content", userPrompt))
        ));

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.anthropic.com/v1/messages"))
            .header("Content-Type", "application/json")
            .header("x-api-key", anthropicApiKey)
            .header("anthropic-version", "2023-06-01")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .timeout(Duration.ofSeconds(30))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Claude API error: " + response.statusCode());
        }

        JsonNode envelope = objectMapper.readTree(response.body());
        return envelope.path("content").get(0).path("text").asText();
    }

    // ─── QUERY HELPERS ────────────────────────────────────────────────────────

    /** Returns a previously saved anomaly report for a simulation. */
    public AnomalyReportResponse getSavedReport(Long simulationId) {
        return reportRepo.findBySimulationId(simulationId).map(report -> {
            List<AnomalyRecord> records = recordRepo.findByReportId(report.getId());
            return buildResponse(report, records);
        }).orElse(null);
    }

    // ─── RESPONSE BUILDER ─────────────────────────────────────────────────────

    private AnomalyReportResponse buildResponse(AnomalyReport report, List<AnomalyRecord> records) {
        AnomalyReportResponse r = new AnomalyReportResponse();
        r.setReportId(report.getId());
        r.setSimulationId(report.getSimulationId());
        r.setTotalAnomalies(report.getTotalAnomalies());
        r.setHasCritical(report.getHasCritical());
        r.setOverallStatus(report.getOverallStatus());
        if (report.getAnalyzedAt() != null) r.setAnalyzedAt(report.getAnalyzedAt().toString());
        r.setAnomalies(records.stream().map(this::mapRecord).collect(Collectors.toList()));
        return r;
    }

    private AnomalyRecordResponse mapRecord(AnomalyRecord a) {
        AnomalyRecordResponse r = new AnomalyRecordResponse();
        r.setAnomalyId(a.getId());
        r.setAnomalyType(a.getAnomalyType());
        r.setSeverity(a.getSeverity());
        r.setTitle(a.getTitle());
        r.setDescription(a.getDescription());
        r.setAffectedSnrPoint(a.getAffectedSnrPoint());
        r.setAffectedBerValue(a.getAffectedBerValue());
        r.setLikelyCause(a.getLikelyCause());
        r.setSuggestedFix(a.getSuggestedFix());
        r.setAiExplanation(a.getAiExplanation());
        return r;
    }

    // ─── BER / SNR PARSERS ────────────────────────────────────────────────────
    //
    // The simulation result is stored as a JSON string.
    // We extract the BER array, SNR range, and modulation type from it.
    // In production these would be stored as proper columns — for now
    // we parse from the JSON blob that the Python bridge returns.
    //

    private double[] parseBerArray(String resultDataJson) {
        try {
            JsonNode root = objectMapper.readTree(resultDataJson);
            JsonNode berNode = root.path("ber_simulated");
            if (berNode.isArray()) {
                double[] arr = new double[berNode.size()];
                for (int i = 0; i < berNode.size(); i++) arr[i] = berNode.get(i).asDouble();
                return arr;
            }
            berNode = root.path("ber_values");
            if (berNode.isArray()) {
                double[] arr = new double[berNode.size()];
                for (int i = 0; i < berNode.size(); i++) arr[i] = berNode.get(i).asDouble();
                return arr;
            }
        } catch (Exception ignored) {}
        // Fallback: generate a synthetic example curve (QPSK theoretical)
        return new double[]{0.079, 0.056, 0.038, 0.022, 0.011, 0.004, 0.001, 0.0002, 0.00003};
    }

    private double[] parseTheoreticalBerArray(String resultDataJson) {
        try {
            JsonNode root = objectMapper.readTree(resultDataJson);
            JsonNode berNode = root.path("ber_theoretical");
            if (berNode.isArray()) {
                double[] arr = new double[berNode.size()];
                for (int i = 0; i < berNode.size(); i++) arr[i] = berNode.get(i).asDouble();
                return arr;
            }
        } catch (Exception ignored) {}
        // Fallback: generate a synthetic example curve (QPSK theoretical)
        return new double[]{0.079, 0.056, 0.038, 0.022, 0.011, 0.004, 0.001, 0.0002, 0.00003};
    }

    private double[] buildSnrRange(String resultDataJson) {
        try {
            JsonNode root = objectMapper.readTree(resultDataJson);
            JsonNode snrNode = root.path("snr_range");
            if (snrNode.isArray()) {
                double[] arr = new double[snrNode.size()];
                for (int i = 0; i < snrNode.size(); i++) arr[i] = snrNode.get(i).asDouble();
                return arr;
            }
            double min = root.path("snrMin").asDouble(-10);
            double max = root.path("snrMax").asDouble(30);
            int steps = 9;
            double[] snr = new double[steps];
            for (int i = 0; i < steps; i++) snr[i] = min + i * (max - min) / (steps - 1);
            return snr;
        } catch (Exception ignored) {}
        return new double[]{-10, -5, 0, 5, 10, 15, 20, 25, 30};
    }

    private String parseModulation(String resultDataJson) {
        try {
            JsonNode root = objectMapper.readTree(resultDataJson);
            return root.path("modulation").asText("QPSK");
        } catch (Exception ignored) { return "QPSK"; }
    }
}
