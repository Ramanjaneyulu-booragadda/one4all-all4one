package com.newbusiness.one4all.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class ApiResponse {
	 @JsonProperty("corelationID")
    private String transactionID;
	  @JsonProperty("transactionDate")
    private String transactionDate;
	  @JsonProperty("message")
    private List<Map<String, Object>> messages;

    public ApiResponse(String transactionID, String transactionDate, List<Map<String, Object>> messages) {
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
        this.messages = messages;
    }

	public String getCorrelationID() {
		return transactionID;
	}

	public void setCorrelationID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public List<Map<String, Object>> getMessages() {
		return messages;
	}

	public void setMessages(List<Map<String, Object>> messages) {
		this.messages = messages;
	}

    
}

