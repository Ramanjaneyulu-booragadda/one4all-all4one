package com.newbusiness.one4all.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class ReceiveHelpPageResponse {
	 private List<ReceivedHelpDetailDTO> records;
	    private ReceivedHelpSummaryDTO summary;

}
