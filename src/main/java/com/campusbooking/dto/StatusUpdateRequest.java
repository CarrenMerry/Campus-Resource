package com.campusbooking.dto;

import com.campusbooking.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private BookingStatus status;

    private String remark;

    public StatusUpdateRequest() {}

    public StatusUpdateRequest(BookingStatus status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
