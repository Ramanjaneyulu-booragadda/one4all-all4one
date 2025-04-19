package com.newbusiness.one4all.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface HelpDashboardProjection {
    String getMemberId();
    String getFullName();
    Integer getTotalHelpGiven();
    BigDecimal getTotalAmountGiven();
    BigDecimal getTotalAmountPending();
    BigDecimal getTotalAmountReceivedAck();
    BigDecimal getAvailableBalance();
    Date getLastReceivedDate();
}

