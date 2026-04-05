package com.sionnavisualizer.service;

import com.sionnavisualizer.dto.LatexExportRequest;
import com.sionnavisualizer.dto.LatexExportResponse;
import com.sionnavisualizer.model.LatexExportRecord;
import com.sionnavisualizer.repository.LatexExportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LatexExportService {

    @Autowired
    private LatexExportRepository latexExportRepository;

    /**
     * Processes a request for a LaTeX table, generates the string representation, 
     * and logs the action securely in the database.
     */
    public LatexExportResponse processExport(LatexExportRequest request) {
        
        // Setup table identification hooks if passed by researcher
        String caption = request.getTableCaption() != null ? request.getTableCaption() : "Simulation Parameters";
        String label = request.getTableLabel() != null ? request.getTableLabel() : "tab:sim-params";

        // Begin constructing the booktabs formatted LaTeX string
        StringBuilder latex = new StringBuilder();
        latex.append("\\begin{table}[h]\n")
             .append("\\centering\n")
             .append("\\caption{").append(caption).append("}\n")
             .append("\\label{").append(label).append("}\n")
             .append("\\begin{tabular}{lll}\n")
             .append("\\toprule\n")
             .append("\\textbf{Parameter} & \\textbf{Value} & \\textbf{Unit} \\\\\n")
             .append("\\midrule\n");

        // Ideally here we inject a general Simulation repository 
        // to dynamically fetch by request.getSimulationId() and iterate dynamically.
        // For current requirement, populating the mock layout explicitly for backend mapping
        latex.append("Carrier Frequency & 28 & GHz \\\\\n");
        latex.append("Modulation & QPSK & -- \\\\\n");
        latex.append("Number of Antennas & 16 & -- \\\\\n");
        latex.append("Bandwidth & 100 & MHz \\\\\n");
        latex.append("SNR Range & -10 to 30 & dB \\\\\n");
        latex.append("Channel Model & AWGN & -- \\\\\n");
        latex.append("Monte Carlo Trials & 10000 & -- \\\\\n");
        latex.append("Simulation Duration & 4.2 & seconds \\\\\n");

        // Close cleanly
        latex.append("\\bottomrule\n")
             .append("\\end{tabular}\n")
             .append("\\end{table}");

        // Save export log record strictly mapped to this user's run
        LatexExportRecord record = new LatexExportRecord();
        record.setSimulationId(request.getSimulationId());
        record.setTableCaption(request.getTableCaption());
        record.setTableLabel(request.getTableLabel());
        
        LatexExportRecord savedRecord = latexExportRepository.save(record);

        // Bundle up cleanly for endpoint response
        return new LatexExportResponse(
            latex.toString(),
            savedRecord.getSimulationId(),
            savedRecord.getGeneratedAt()
        );
    }
}
