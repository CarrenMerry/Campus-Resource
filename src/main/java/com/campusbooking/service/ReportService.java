package com.campusbooking.service;

import com.campusbooking.dto.BookingResponseDto;
import com.campusbooking.dto.ReportSummaryDto;
import com.campusbooking.enums.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    ReportSummaryDto getDashboardSummary();

    List<BookingResponseDto> getFilteredReport(LocalDate fromDate, LocalDate toDate,
                                               Long resourceId, Long categoryId,
                                               BookingStatus status);

    List<ReportSummaryDto.ResourceUsageItem> getResourceUsageReport(LocalDate fromDate, LocalDate toDate);
}
