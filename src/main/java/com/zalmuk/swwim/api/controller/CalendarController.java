package com.zalmuk.swwim.api.controller;

import com.zalmuk.swwim.api.dto.calendar.CalendarEventRequest;
import com.zalmuk.swwim.api.dto.calendar.CalendarEventResponse;
import com.zalmuk.swwim.api.dto.common.ApiResponse;
import com.zalmuk.swwim.api.entity.training.CalendarEvent;
import com.zalmuk.swwim.api.service.training.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 캘린더 컨트롤러
 */
@Tag(name = "Calendar", description = "캘린더 API")
@RestController
@RequestMapping("/api/v1/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @Operation(summary = "월간 이벤트 조회", description = "특정 월의 모든 캘린더 이벤트를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CalendarEventResponse>>> getMonthlyEvents(
            @AuthenticationPrincipal String userId,
            @Parameter(description = "조회할 년월 (yyyy-MM)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<CalendarEvent> events = calendarService.getEventsByDateRange(userId, startDate, endDate);
        List<CalendarEventResponse> responses = events.stream()
                .map(CalendarEventResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @Operation(summary = "특정 날짜 이벤트 조회", description = "특정 날짜의 캘린더 이벤트를 조회합니다.")
    @GetMapping("/{date}")
    public ResponseEntity<ApiResponse<CalendarEventResponse>> getEventByDate(
            @AuthenticationPrincipal String userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return calendarService.findByUserAndDate(userId, date)
                .map(event -> ResponseEntity.ok(ApiResponse.success(CalendarEventResponse.from(event))))
                .orElse(ResponseEntity.ok(ApiResponse.success(null)));
    }

    @Operation(summary = "이벤트 생성/수정", description = "특정 날짜의 캘린더 이벤트를 생성하거나 수정합니다.")
    @PostMapping("/{date}")
    public ResponseEntity<ApiResponse<CalendarEventResponse>> saveEvent(
            @AuthenticationPrincipal String userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Valid @RequestBody CalendarEventRequest request) {
        CalendarEvent event = calendarService.createOrUpdateEvent(userId, date, request.getTrainings());
        return ResponseEntity.ok(ApiResponse.success(CalendarEventResponse.from(event), "이벤트가 저장되었습니다."));
    }

    @Operation(summary = "이벤트 삭제", description = "특정 날짜의 캘린더 이벤트를 삭제합니다.")
    @DeleteMapping("/{date}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @AuthenticationPrincipal String userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        calendarService.deleteEventByDate(userId, date);
        return ResponseEntity.ok(ApiResponse.success(null, "이벤트가 삭제되었습니다."));
    }

    @Operation(summary = "훈련 있는 날짜 목록", description = "특정 월에서 훈련이 있는 날짜 목록을 조회합니다.")
    @GetMapping("/dates")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getDatesWithEvents(
            @AuthenticationPrincipal String userId,
            @Parameter(description = "조회할 년월 (yyyy-MM)") @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<LocalDate> dates = calendarService.getDatesWithEvents(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(dates));
    }
}
