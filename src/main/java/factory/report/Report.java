package com.vehicle.spareparts.factory.report;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Factory Pattern - Abstract Report Interface
 * Defines contract for different report types
 */
public interface Report {

    /**
     * Generate report data
     * @return Report data as map
     */
    Map<String, Object> generateReportData();

    /**
     * Get report title
     * @return Report title
     */
    String getReportTitle();

    /**
     * Get report type
     * @return Report type identifier
     */
    String getReportType();

    /**
     * Get report generation time
     * @return Generation timestamp
     */
    LocalDateTime getGeneratedAt();

    /**
     * Export report to string format
     * @return Formatted report string
     */
    String exportToString();
}

