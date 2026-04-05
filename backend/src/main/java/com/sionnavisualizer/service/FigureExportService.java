package com.sionnavisualizer.service;

import com.sionnavisualizer.dto.FigureExportRequest;
import com.sionnavisualizer.dto.FigureExportResponse;
import com.sionnavisualizer.model.FigureExport;
import com.sionnavisualizer.repository.FigureExportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FigureExportService {

    @Autowired
    private FigureExportRepository figureExportRepository;

    // Handles the business logic of saving an export record
    public FigureExportResponse processExport(FigureExportRequest request) {
        // Create a new FigureExport entity
        FigureExport export = new FigureExport();
        export.setSimulationId(request.getSimulationId());
        export.setJournalStyle(request.getJournalStyle());
        export.setExportFormat(request.getExportFormat());
        export.setChartType(request.getChartType());
        export.setCreatedAt(LocalDateTime.now());
        
        // Save to PostgreSQL database
        FigureExport savedExport = figureExportRepository.save(export);
        
        // Return a response to the user
        return new FigureExportResponse(
            savedExport.getId(),
            "Successfully saved " + request.getJournalStyle() + " format export.",
            savedExport.getCreatedAt()
        );
    }
}
