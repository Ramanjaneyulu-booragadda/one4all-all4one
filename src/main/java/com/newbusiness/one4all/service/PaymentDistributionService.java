package com.newbusiness.one4all.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.strategy.PaymentDistributionStrategy;

import jakarta.transaction.Transactional;

@Service
public class PaymentDistributionService {

    @Autowired
    private PaymentDistributionStrategy paymentDistributionStrategy;

    @Autowired
    private ReferralService referralService;

    @Transactional
    public void distributePayment(PaymentDetails paymentDetails) {
        List<UplinerWithMemberDetailsDTO> upliners = referralService.getUpliners(paymentDetails.getOfaConsumerNo());
        paymentDistributionStrategy.distributePayment(paymentDetails, upliners);
    }
}
