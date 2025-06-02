package com.newbusiness.one4all.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HelpSubmissionDTO {
    @NotBlank
    private String ofaMemberId;

    @NotBlank
    private String receiverMemberId;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String uplinerLevel; // Optional: can be String or Integer

    private String proof; // uploaded file URL (optional initially)
    @NotBlank
    private String receiverMobile;

}
