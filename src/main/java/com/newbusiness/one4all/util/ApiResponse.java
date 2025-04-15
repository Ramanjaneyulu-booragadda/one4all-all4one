package com.newbusiness.one4all.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ApiResponse {

    @JsonProperty("corelationID")
    private String transactionID;

    @JsonProperty("transactionDate")
    private String transactionDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private List<Map<String, Object>> messages;

    @JsonProperty("errorDetails")
    private Map<String, Object> errorDetails;

    public ApiResponse(String transactionID, String transactionDate, String status,
                       List<Map<String, Object>> messages, Map<String, Object> errorDetails) {
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
        this.status = status;
        this.messages = messages;
        this.errorDetails = errorDetails;
    }

    public ApiResponse(String transactionID, String transactionDate, List<Map<String, Object>> messages) {
        this(transactionID, transactionDate, "Success", messages, null);
    }
}
