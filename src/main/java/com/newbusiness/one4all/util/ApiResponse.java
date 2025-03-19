package com.newbusiness.one4all.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ApiResponse {
	@JsonProperty("corelationID")
	private String transactionID;
	@JsonProperty("transactionDate")
	private String transactionDate;
	@JsonProperty("message")
	private List<Map<String, Object>> messages;
	@JsonProperty("errorDetails")
	private List<Map<String, Object>> errorDetails;
	@JsonProperty("status")
	private String status;

	// Constructor for error responses
    public ApiResponse(String transactionID, String transactionDate, String status, List<Map<String, Object>> errorDetails, List<Map<String, Object>> messages) {
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
        this.status = status;
        this.errorDetails = errorDetails;
        this.messages = messages;
    }

    // Constructor for messages-only responses
    public ApiResponse(String transactionID, String transactionDate, List<Map<String, Object>> messages) {
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
        this.status = "Success";
        this.errorDetails = null;
        this.messages = messages;
    }

	

}
