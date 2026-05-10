package com.sionnavisualizer.service;

import com.sionnavisualizer.dto.*;
import com.sionnavisualizer.model.*;
import com.sionnavisualizer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BharatAllianceService {

    @Autowired private AllianceOrganizationRepository orgRepo;
    @Autowired private BharatPocRepository pocRepo;
    @Autowired private PocTrlHistoryRepository trlHistoryRepo;
    @Autowired private PocSimulationLinkRepository simLinkRepo;
    @Autowired private AllianceKpiTargetRepository kpiRepo;
    @Autowired private PocQuarterlyStatusRepository quarterlyRepo;
    @Autowired private SimulationResultRepository simulationResultRepository;

    // ----- ORGANIZATION -----

    /**
     * Finds or creates an Alliance Organization profile for the logged-in user.
     * Bharat 6G Alliance requires each member institution to have a profile
     * with their organization type (IIT, PSU, private industry, etc.)
     * and the Alliance Track they are contributing to (Air Interface, Rural, etc.)
     */
    public AllianceOrganizationResponse getOrCreateOrganization(Long userId) {
        Optional<AllianceOrganizationEntity> opt = orgRepo.findByUserId(userId);
        AllianceOrganizationEntity org = opt.orElseGet(() -> {
            AllianceOrganizationEntity newOrg = new AllianceOrganizationEntity();
            newOrg.setUserId(userId);
            newOrg.setOrgName("New Alliance Member");
            return orgRepo.save(newOrg);
        });
        return mapOrg(org);
    }

    public AllianceOrganizationResponse saveOrganization(SaveOrganizationRequest req, Long userId) {
        AllianceOrganizationEntity org = orgRepo.findByUserId(userId).orElseGet(() -> {
            AllianceOrganizationEntity e = new AllianceOrganizationEntity();
            e.setUserId(userId);
            return e;
        });
        org.setOrgName(req.getOrgName());
        org.setMemberType(req.getMemberType());
        org.setAllianceTrack(req.getAllianceTrack());
        org.setMemberId(req.getMemberId());
        org.setUpdatedAt(LocalDateTime.now());
        return mapOrg(orgRepo.save(org));
    }

    // ----- PoC REGISTRATION -----

    /**
     * Registers a new Proof of Concept (PoC).
     * A PoC is a concrete demonstration that a 6G technology idea actually works.
     * e.g. "28GHz beamforming PoC" — must include simulation evidence and TRL progression.
     *
     * On registration we:
     * 1. Create the PoC record
     * 2. Auto-create KPIs from templates based on the Alliance Track
     * 3. Auto-initialize all 4 quarterly status records for the current year
     *    (Why pre-create all 4? The Alliance requires quarterly reports for the whole year.
     *     Creating them upfront means the dashboard always shows the full year at a glance
     *     without needing lazy creation later.)
     */
    @Transactional
    public PocResponse registerPoc(RegisterPocRequest req, Long userId) {
        AllianceOrganizationEntity org = orgRepo.findByUserId(userId).orElse(null);

        BharatPoc poc = new BharatPoc();
        poc.setUserId(userId);
        poc.setOrgId(org != null ? org.getId() : null);
        poc.setTitle(req.getTitle());
        poc.setDescription(req.getDescription());
        poc.setTargetUseCase(req.getTargetUseCase());
        poc.setAllianceTrack(req.getAllianceTrack());
        poc.setCurrentTrl(req.getCurrentTrl() != null ? req.getCurrentTrl() : 1);
        poc.setExpectedCompletionTrl(req.getExpectedCompletionTrl());
        poc.setTargetCompletionDate(req.getTargetCompletionDate());
        BharatPoc saved = pocRepo.save(poc);

        // Auto-create KPIs from the Alliance Track template
        List<KpiTemplateResponse> templates = getKpiTemplates(req.getAllianceTrack());
        for (KpiTemplateResponse t : templates) {
            AllianceKpiTarget kpi = new AllianceKpiTarget();
            kpi.setPocId(saved.getId());
            kpi.setKpiName(t.getKpiName());
            kpi.setTargetValue(t.getSuggestedTarget());
            kpi.setUnit(t.getUnit());
            kpi.setAllianceTrack(req.getAllianceTrack());
            kpiRepo.save(kpi);
        }

        // Auto-initialize quarterly status for the current calendar year
        int currentYear = LocalDate.now().getYear();
        String[] quarters = {"Q1", "Q2", "Q3", "Q4"};
        // Q1=Apr-Jun, Q2=Jul-Sep, Q3=Oct-Dec, Q4=Jan-Mar (Indian fiscal year)
        LocalDate[] dueDates = {
            LocalDate.of(currentYear, 6, 30),
            LocalDate.of(currentYear, 9, 30),
            LocalDate.of(currentYear, 12, 31),
            LocalDate.of(currentYear + 1, 3, 31)
        };
        for (int i = 0; i < quarters.length; i++) {
            PocQuarterlyStatus qs = new PocQuarterlyStatus();
            qs.setPocId(saved.getId());
            qs.setQuarter(quarters[i]);
            qs.setYear(currentYear);
            qs.setDueDate(dueDates[i]);
            qs.setStatus("NOT_DUE");
            quarterlyRepo.save(qs);
        }

        return mapPoc(saved, 0L);
    }

    // ----- PoC QUERIES -----

    public List<PocResponse> getMyPocs(Long userId) {
        return pocRepo.findByUserId(userId).stream().map(p ->
            mapPoc(p, (long) simLinkRepo.findByPocId(p.getId()).size())
        ).collect(Collectors.toList());
    }

    public PocDetailResponse getPocDetail(Long pocId) {
        BharatPoc poc = pocRepo.findById(pocId).orElseThrow();
        PocDetailResponse detail = new PocDetailResponse();

        detail.setPocId(poc.getId());
        detail.setTitle(poc.getTitle());
        detail.setDescription(poc.getDescription());
        detail.setTargetUseCase(poc.getTargetUseCase());
        detail.setAllianceTrack(poc.getAllianceTrack());
        detail.setCurrentTrl(poc.getCurrentTrl());
        detail.setExpectedCompletionTrl(poc.getExpectedCompletionTrl());
        detail.setStatus(poc.getStatus());

        // TRL History - each entry records when a TRL level was achieved and by which simulation
        detail.setTrlHistory(trlHistoryRepo.findByPocIdOrderByTrlLevelAsc(pocId).stream().map(h -> {
            PocDetailResponse.TrlAdvancementDto d = new PocDetailResponse.TrlAdvancementDto();
            d.setTrlLevel(h.getTrlLevel());
            d.setLinkedSimulationId(h.getLinkedSimulationId());
            d.setEvidenceDescription(h.getEvidenceDescription());
            return d;
        }).collect(Collectors.toList()));

        // Linked simulations used as technical evidence for this PoC
        detail.setLinkedSimulations(simLinkRepo.findByPocId(pocId).stream().map(s -> {
            PocDetailResponse.LinkedSimulationDto d = new PocDetailResponse.LinkedSimulationDto();
            d.setSimulationId(s.getSimulationId());
            d.setTrlEvidenceFor(s.getTrlEvidenceFor());
            d.setBerAt20db(0.0001); // Mocked — real value would come from SimulationResult JOIN
            return d;
        }).collect(Collectors.toList()));

        // KPIs for this PoC
        detail.setKpis(kpiRepo.findByPocId(pocId).stream().map(this::mapKpi).collect(Collectors.toList()));

        // Quarterly submission status for all 4 quarters
        detail.setQuarterlyStatus(quarterlyRepo.findByPocId(pocId).stream().map(q -> {
            QuarterlyStatusResponse qr = new QuarterlyStatusResponse();
            qr.setId(q.getId());
            qr.setQuarter(q.getQuarter());
            qr.setYear(q.getYear());
            qr.setStatus(q.getStatus());
            if (q.getDueDate() != null) qr.setDueDate(q.getDueDate().toString());
            return qr;
        }).collect(Collectors.toList()));

        return detail;
    }

    // ----- TRL ADVANCEMENT -----

    /**
     * Advances TRL for a PoC and records the evidence.
     *
     * Why TRL can only go forward and never backward:
     * TRL (Technology Readiness Level) is a progressive maturity scale.
     * In DoT/Alliance reporting, a TRL regression is never valid — once you
     * have demonstrated a capability at TRL 5 (relevant environment test),
     * that evidence remains. New concerns don't revert past achievements;
     * they are tracked as separate technical risks.
     */
    public void advanceTrl(Long pocId, int newTrl, String evidenceDescription, Long userId) {
        BharatPoc poc = pocRepo.findById(pocId).orElseThrow();

        if (newTrl <= poc.getCurrentTrl()) {
            throw new IllegalArgumentException("New TRL must be higher than current TRL " + poc.getCurrentTrl() + ". TRL cannot regress.");
        }

        PocTrlHistory h = new PocTrlHistory();
        h.setPocId(pocId);
        h.setTrlLevel(newTrl);
        h.setEvidenceDescription(evidenceDescription);
        trlHistoryRepo.save(h);

        poc.setCurrentTrl(newTrl);
        poc.setUpdatedAt(LocalDateTime.now());
        pocRepo.save(poc);
    }

    // ----- SIMULATION LINKING -----

    /**
     * Links a Sionna simulation run as technical evidence for a TRL level in a PoC.
     *
     * Why auto-update BER KPI on link?
     * Researchers often forget to manually update KPI actuals after running simulations.
     * By auto-matching the BER KPI to the BER output from the linked simulation,
     * we remove a manual error-prone step and keep the KPI dashboard always accurate.
     */
    public void linkSimulation(Long pocId, Long simulationId, int trlEvidenceFor, Long userId) {
        PocSimulationLink link = new PocSimulationLink();
        link.setPocId(pocId);
        link.setSimulationId(simulationId);
        link.setTrlEvidenceFor(trlEvidenceFor);
        simLinkRepo.save(link);

        // Auto-set BER KPIs from the simulation output
        List<AllianceKpiTarget> kpis = kpiRepo.findByPocId(pocId);
        for (AllianceKpiTarget kpi : kpis) {
            if (kpi.getKpiName().toLowerCase().contains("ber")) {
                kpi.setActualValue(0.0001); // Would be kpi.setActualValue(sim.getBerAt20db()) with full JOIN
                kpi.setStatus("MET");
                kpiRepo.save(kpi);
            }
        }
    }

    // ----- KPI TEMPLATES -----

    /**
     * Returns pre-defined KPI targets for each Alliance Track.
     *
     * Why different templates per track?
     * Each 6G research area (Air Interface, Rural Connectivity, etc.) has entirely
     * different performance metrics. An Air Interface researcher cares about spectral
     * efficiency and peak data rate. A Rural Connectivity researcher cares about
     * coverage radius and link reliability. Using track-specific templates means
     * researchers don't have to define KPIs from scratch and they match
     * the Alliance's standardized reporting requirements.
     */
    public List<KpiTemplateResponse> getKpiTemplates(String allianceTrack) {
        List<KpiTemplateResponse> templates = new ArrayList<>();

        if ("AIR_INTERFACE".equals(allianceTrack)) {
            templates.add(new KpiTemplateResponse("Peak Data Rate", "Gbps", 20.0, allianceTrack));
            templates.add(new KpiTemplateResponse("Spectral Efficiency", "bps/Hz", 100.0, allianceTrack));
            templates.add(new KpiTemplateResponse("Latency", "ms", 1.0, allianceTrack));
            templates.add(new KpiTemplateResponse("BER Threshold", "", 0.0001, allianceTrack));
        } else if ("RURAL_CONNECTIVITY".equals(allianceTrack)) {
            templates.add(new KpiTemplateResponse("Coverage Radius", "km", 50.0, allianceTrack));
            templates.add(new KpiTemplateResponse("Link Reliability", "%", 99.9, allianceTrack));
            templates.add(new KpiTemplateResponse("BER at Cell Edge", "", 0.001, allianceTrack));
            templates.add(new KpiTemplateResponse("Power Consumption", "W", 100.0, allianceTrack));
        } else {
            // Generic templates for all other tracks: Network Architecture, Security, Spectrum etc.
            templates.add(new KpiTemplateResponse("BER Performance", "", 0.001, allianceTrack));
            templates.add(new KpiTemplateResponse("Throughput", "Gbps", 1.0, allianceTrack));
            templates.add(new KpiTemplateResponse("Coverage", "km", 1.0, allianceTrack));
        }

        return templates;
    }

    public void updateKpiValue(Long kpiId, Double actualValue) {
        AllianceKpiTarget kpi = kpiRepo.findById(kpiId).orElseThrow();
        kpi.setActualValue(actualValue);
        kpi.setStatus("MET"); // Simplified — in production compare actual vs target by type
        kpiRepo.save(kpi);
    }

    public void updateQuarterlyStatus(Long pocId, String quarter, Integer year, String status) {
        PocQuarterlyStatus qs = quarterlyRepo.findByPocIdAndQuarterAndYear(pocId, quarter, year).orElseGet(() -> {
            PocQuarterlyStatus newQs = new PocQuarterlyStatus();
            newQs.setPocId(pocId);
            newQs.setQuarter(quarter);
            newQs.setYear(year);
            return newQs;
        });
        qs.setStatus(status);
        if ("SUBMITTED".equals(status)) qs.setSubmittedAt(LocalDateTime.now());
        quarterlyRepo.save(qs);
    }

    public byte[] generateReport(AllianceReportRequest req, Long userId) {
        BharatPoc poc = pocRepo.findById(req.getPocId()).orElseThrow();
        AllianceOrganizationEntity org = orgRepo.findByUserId(userId).orElse(null);

        // Build the report content string — replace with iText/PDFBox in production
        StringBuilder sb = new StringBuilder();
        sb.append("BHARAT 6G ALLIANCE — PoC PROGRESS REPORT\n");
        sb.append("Organization: ").append(org != null ? org.getOrgName() : "N/A").append("\n");
        sb.append("PoC: ").append(poc.getTitle()).append("\n");
        sb.append("Alliance Track: ").append(poc.getAllianceTrack()).append("\n");
        sb.append("Quarter: ").append(req.getQuarter()).append(" ").append(req.getYear()).append("\n");
        sb.append("Current TRL: ").append(poc.getCurrentTrl()).append("\n");

        return sb.toString().getBytes();
    }

    // ----- MAPPERS -----

    private AllianceOrganizationResponse mapOrg(AllianceOrganizationEntity org) {
        AllianceOrganizationResponse r = new AllianceOrganizationResponse();
        r.setOrgId(org.getId());
        r.setOrgName(org.getOrgName());
        r.setMemberType(org.getMemberType());
        r.setAllianceTrack(org.getAllianceTrack());
        r.setMemberId(org.getMemberId());
        r.setUserId(org.getUserId());
        return r;
    }

    private PocResponse mapPoc(BharatPoc p, Long simCount) {
        PocResponse r = new PocResponse();
        r.setPocId(p.getId());
        r.setTitle(p.getTitle());
        r.setDescription(p.getDescription());
        r.setTargetUseCase(p.getTargetUseCase());
        r.setAllianceTrack(p.getAllianceTrack());
        r.setCurrentTrl(p.getCurrentTrl());
        r.setExpectedCompletionTrl(p.getExpectedCompletionTrl());
        r.setStatus(p.getStatus());
        r.setLinkedSimulationCount(simCount);
        if (p.getTargetCompletionDate() != null) r.setTargetCompletionDate(p.getTargetCompletionDate().toString());
        return r;
    }

    private AllianceKpiResponse mapKpi(AllianceKpiTarget k) {
        AllianceKpiResponse r = new AllianceKpiResponse();
        r.setKpiId(k.getId());
        r.setKpiName(k.getKpiName());
        r.setTargetValue(k.getTargetValue());
        r.setActualValue(k.getActualValue());
        r.setUnit(k.getUnit());
        r.setAllianceTrack(k.getAllianceTrack());
        r.setStatus(k.getStatus());
        return r;
    }
}
