package com.newbusiness.one4all.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HelpVerificationDTO {

    @NotBlank
    private String paymentId;

    @NotNull
    private String status; // should be enum-validated on service layer

    @NotBlank
    private String comments;
}
