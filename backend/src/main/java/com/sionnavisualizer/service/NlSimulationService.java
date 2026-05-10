package com.sionnavisualizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sionnavisualizer.dto.ParsedParamsResponse;
import com.sionnavisualizer.model.NlParseRecord;
import com.sionnavisualizer.repository.NlParseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class NlSimulationService {

    @Autowired
    private NlParseRepository nlParseRepository;

    // Injected from application.properties — never hard-code API keys in source code
    @Value("${anthropic.api.key:NOT_CONFIGURED}")
    private String anthropicApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ─── SYSTEM PROMPT ────────────────────────────────────────────────────────
    //
    // This system prompt is the core instruction set for the Claude AI model.
    //
    // Why do we say "Return ONLY valid JSON"?
    // Claude is a conversational model — by default it adds preamble like
    // "Sure! Here are the parameters:" before the JSON. That breaks JSON parsing.
    // Saying ONLY JSON forces a machine-readable response with no decorative text.
    //
    // Why store it as a constant?
    // We use this same prompt for every call. Keeping it here makes it easy to
    // tune the prompt without touching logic methods.
    //
    private static final String SYSTEM_PROMPT = """
            You are a 6G wireless simulation parameter extractor.
            Extract simulation parameters from the user's natural language description
            and return ONLY a JSON object with no other text.

            Extract these fields if mentioned:
            - frequency_ghz: number (e.g. 28, 39, 60, 5, 2.4)
            - channel_model: string (AWGN, CDL-A, CDL-B, CDL-C, TDL-A, TDL-B, TDL-C)
            - modulation: string (BPSK, QPSK, 16QAM, 64QAM)
            - modulation_list: array of strings (if user wants comparison of multiple)
            - num_antennas_tx: integer
            - num_antennas_rx: integer
            - snr_min_db: number
            - snr_max_db: number
            - environment: string (urban, suburban, rural, indoor)
            - simulation_type: string (BER_SIMULATION, BEAM_PATTERN, CHANNEL_CAPACITY, CDL_TDL, COMPARISON)
            - run_comparison: boolean
            - natural_language_summary: string (one sentence explaining what you understood)
            - missing_params: array of strings (parameter names you could not extract)
            - confidence: string (HIGH if 6+ found, MEDIUM if 3-5, LOW if fewer than 3)

            Rules:
            - Return ONLY valid JSON, no explanation text before or after
            - If a parameter is not mentioned, set it to null
            - For MIMO NxN notation, extract N as both num_antennas_tx and num_antennas_rx
            - mmWave or millimeter wave implies frequency >= 28 GHz
            - If user says "compare" or "vs" or "versus", set run_comparison to true and use modulation_list
            - natural_language_summary must be one clear, readable English sentence
            """;

    // ─── KEYWORD GUARD ────────────────────────────────────────────────────────
    //
    // Why validate for 6G keywords before calling Claude?
    // Claude API costs money per call. If a user types random text like
    // "hello world" or "what is the weather", we'd waste an API call
    // with zero chance of extracting simulation parameters.
    // This keyword guard blocks obviously non-technical inputs cheaply,
    // before any network call is made.
    //
    private static final Set<String> VALID_6G_KEYWORDS = Set.of(
            "ghz", "mhz", "mimo", "ber", "awgn", "cdl", "tdl",
            "bpsk", "qpsk", "qam", "antenna", "beamforming", "channel",
            "simulation", "frequency", "snr", "modulation", "mmwave",
            "millimeter", "5g", "6g", "ofdm", "fading", "multipath",
            "throughput", "spectral", "latency"
    );

    public ParsedParamsResponse parseQuery(String query, Long userId) throws Exception {

        // Step 1: Basic validation — reject empty or over-length inputs
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query cannot be empty.");
        }
        if (query.length() > 500) {
            throw new IllegalArgumentException("Query exceeds 500 character limit.");
        }

        // Step 2: Keyword guard — check for at least one recognizable 6G term
        String queryLower = query.toLowerCase();
        boolean hasKeyword = VALID_6G_KEYWORDS.stream().anyMatch(queryLower::contains);
        if (!hasKeyword) {
            throw new IllegalArgumentException(
                "Query must contain at least one 6G simulation keyword (e.g. GHz, MIMO, CDL, BER, SNR).");
        }

        // Step 3: Call Claude AI API
        String claudeJson = callClaudeApi(query);

        // Step 4: Parse Claude's JSON response into our structured response object
        ParsedParamsResponse result = parseClaudeResponse(claudeJson);

        // Step 5: Save the parse record to database for history feature
        NlParseRecord record = new NlParseRecord();
        record.setUserId(userId);
        record.setQueryText(query);
        record.setExtractedParamsJson(claudeJson);
        record.setConfidence(result.getConfidence());
        record.setSimulationType(result.getSimulationType());
        nlParseRepository.save(record);

        return result;
    }

    // ─── CLAUDE API CALL ──────────────────────────────────────────────────────
    //
    // This method makes the actual HTTP POST to Anthropic's API.
    //
    // We use Java's built-in HttpClient (java.net.http) — no extra libraries needed.
    //
    // Key headers explained:
    //   x-api-key        — your Anthropic account key (set in application.properties)
    //   anthropic-version — tells Claude which API version to use (required)
    //   Content-Type      — we're sending JSON, so we must declare this
    //
    // The request body is a JSON string we build manually here.
    // In production you'd use Jackson ObjectMapper to build this, but
    // manual string building is clearer for understanding the structure.
    //
    private String callClaudeApi(String userQuery) throws Exception {

        String requestBody = """
                {
                  "model": "claude-sonnet-4-20250514",
                  "max_tokens": 512,
                  "system": %s,
                  "messages": [
                    {
                      "role": "user",
                      "content": %s
                    }
                  ]
                }
                """.formatted(
                        objectMapper.writeValueAsString(SYSTEM_PROMPT),
                        objectMapper.writeValueAsString(userQuery)
                );

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

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
            throw new RuntimeException("Claude API returned error: " + response.statusCode() + " — " + response.body());
        }

        // Claude wraps the actual content in: response.content[0].text
        // Parse the outer Anthropic envelope to get to the actual JSON string Claude wrote
        JsonNode envelope = objectMapper.readTree(response.body());
        return envelope.path("content").get(0).path("text").asText();
    }

    // ─── RESPONSE PARSER ──────────────────────────────────────────────────────
    //
    // Claude returns a JSON string. We parse it into our ParsedParamsResponse.
    //
    // Why not use a record/auto-mapper?
    // Because Claude's field names use snake_case (frequency_ghz) while our
    // Java DTO uses camelCase (frequency). Explicit field-by-field mapping
    // makes the conversion clear and easy to debug.
    //
    private ParsedParamsResponse parseClaudeResponse(String claudeText) throws Exception {
        // Sometimes Claude wraps JSON in markdown code fences like ```json ... ``` — strip them
        String cleanJson = claudeText.trim()
                .replaceAll("^```json\\s*", "")
                .replaceAll("^```\\s*", "")
                .replaceAll("\\s*```$", "");

        JsonNode node = objectMapper.readTree(cleanJson);

        ParsedParamsResponse r = new ParsedParamsResponse();
        List<String> aiFilled = new ArrayList<>();

        // Map each field, track which ones Claude actually populated (not null)
        if (!node.path("frequency_ghz").isNull() && node.has("frequency_ghz")) {
            r.setFrequency(node.path("frequency_ghz").asDouble());
            aiFilled.add("frequency");
        }
        if (!node.path("channel_model").isNull() && node.has("channel_model")) {
            r.setChannelModel(node.path("channel_model").asText(null));
            if (r.getChannelModel() != null) aiFilled.add("channelModel");
        }
        if (!node.path("modulation").isNull() && node.has("modulation")) {
            r.setModulation(node.path("modulation").asText(null));
            if (r.getModulation() != null) aiFilled.add("modulation");
        }
        if (node.has("modulation_list") && node.path("modulation_list").isArray()) {
            List<String> mods = new ArrayList<>();
            node.path("modulation_list").forEach(m -> mods.add(m.asText()));
            if (!mods.isEmpty()) { r.setModulationList(mods); aiFilled.add("modulationList"); }
        }
        if (!node.path("num_antennas_tx").isNull() && node.has("num_antennas_tx")) {
            r.setNumAntennasTx(node.path("num_antennas_tx").asInt());
            aiFilled.add("numAntennasTx");
        }
        if (!node.path("num_antennas_rx").isNull() && node.has("num_antennas_rx")) {
            r.setNumAntennasRx(node.path("num_antennas_rx").asInt());
            aiFilled.add("numAntennasRx");
        }
        if (!node.path("snr_min_db").isNull() && node.has("snr_min_db")) {
            r.setSnrMin(node.path("snr_min_db").asDouble());
            aiFilled.add("snrMin");
        }
        if (!node.path("snr_max_db").isNull() && node.has("snr_max_db")) {
            r.setSnrMax(node.path("snr_max_db").asDouble());
            aiFilled.add("snrMax");
        }
        if (!node.path("environment").isNull() && node.has("environment")) {
            r.setEnvironment(node.path("environment").asText(null));
            if (r.getEnvironment() != null) aiFilled.add("environment");
        }
        if (!node.path("simulation_type").isNull() && node.has("simulation_type")) {
            r.setSimulationType(node.path("simulation_type").asText(null));
            if (r.getSimulationType() != null) aiFilled.add("simulationType");
        }
        if (!node.path("run_comparison").isNull() && node.has("run_comparison")) {
            r.setRunComparison(node.path("run_comparison").asBoolean());
        }

        r.setNaturalLanguageSummary(node.path("natural_language_summary").asText("Unable to summarize."));

        // Missing params list from Claude
        List<String> missingList = new ArrayList<>();
        if (node.has("missing_params") && node.path("missing_params").isArray()) {
            node.path("missing_params").forEach(m -> missingList.add(m.asText()));
        }
        r.setMissingParams(missingList);

        // ─── CONFIDENCE SCORING ───────────────────────────────────────────────
        //
        // Why count filled params to determine confidence?
        // If Claude extracted 6+ parameters, the user's query was very specific
        // and we can be confident the simulation will be well-configured.
        // Fewer parameters usually means the query was vague (e.g. "run a simulation")
        // and the researcher should review defaults carefully before running.
        //
        String confidence = node.path("confidence").asText("MEDIUM");
        if (!List.of("HIGH", "MEDIUM", "LOW").contains(confidence)) {
            // If Claude returned something unexpected, re-calculate from filled count
            int filled = aiFilled.size();
            confidence = filled >= 6 ? "HIGH" : filled >= 3 ? "MEDIUM" : "LOW";
        }
        r.setConfidence(confidence);
        r.setAiFilled(aiFilled);

        // Apply sensible defaults for any required missing fields
        if (r.getSnrMin() == null) r.setSnrMin(-10.0);
        if (r.getSnrMax() == null) r.setSnrMax(30.0);
        if (r.getNumAntennasTx() == null) r.setNumAntennasTx(1);
        if (r.getNumAntennasRx() == null) r.setNumAntennasRx(1);
        if (r.getSimulationType() == null) r.setSimulationType("BER_SIMULATION");

        return r;
    }

    // Returns the last 10 parse records for display in the history panel
    public List<NlParseRecord> getHistory(Long userId) {
        return nlParseRepository.findTop10ByUserIdOrderByParsedAtDesc(userId);
    }
}
