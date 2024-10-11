package com.newbusiness.one4all.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.PaymentDetails;
import com.newbusiness.one4all.repository.PaymentDetailRepository;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.util.PaymentStatus;
import com.newbusiness.one4all.util.ResponseUtils;
import org.springframework.core.env.Environment;
@Service
public class MLMService {
    
    @Autowired
    private PaymentDetailRepository paymentDetailRepository;
    
    @Autowired
    private PaymentDetailService paymentDetailService;

    @Autowired
    private Environment environment;
    
    @Autowired
    private UserRepository userRepository;

    // This method handles the distribution of payouts up the chain
    @Transactional
    public void distributeHelp(Optional<Member> member, PaymentDetails paymentDetails) {
        if (member.isPresent()) {
            Member currentMember = member.get();

            // Pay direct referral bonus if referredBy exists
            if (currentMember.getReferredBy() != null) {
                BigDecimal referralBonus = getReferralBonusFromProperties(); // Dynamic from properties
                payDirectReferralBonus(currentMember, referralBonus);

                // Subtract the referral bonus from the total help amount (leftover amount)
                BigDecimal remainingHelpAmount = paymentDetails.getOfaHelpAmount().subtract(referralBonus);

                // Update the payment record with the leftover amount
                paymentDetails.setOfaHelpAmount(remainingHelpAmount);
                paymentDetailService.updatePayment(paymentDetails.getOfaPaymentId(), ResponseUtils.convertToDTO(paymentDetails));
            }

            // Proceed to pay upliners with the leftover help amount
            payUpliners(currentMember, paymentDetails);
        }
    }

    private BigDecimal getReferralBonusFromProperties() {
		return new BigDecimal(environment.getProperty("mlm.bonus.direct.referral", "1000")); // Example dynamic value
    }

    // Pay direct referral bonus
    private void payDirectReferralBonus(Member member, BigDecimal referralBonus) {
        Member referrer = member.getReferredBy();
        if (referrer != null) {
            PaymentDetails paymentDetails = new PaymentDetails();
            paymentDetails.setOfaConsumerName(referrer.getOfaFullName());
            paymentDetails.setOfaHelpAmount(referralBonus);
            paymentDetails.setOfaPaymentStatus(PaymentStatus.PENDING);
            paymentDetails.setOfaUpdatedAt(new Date());

            paymentDetailRepository.save(paymentDetails);
        }
    }

    // Pay upliners up to 10 stages
    private void payUpliners(Member currentMember, PaymentDetails paymentDetails) {
        Member currentUpliner = currentMember.getReferredBy();
        int stage = 1;

        // Dynamic payout scheme from properties
        Map<Integer, BigDecimal> payoutScheme = getPayoutSchemeFromProperties();

        BigDecimal remainingHelpAmount = paymentDetails.getOfaHelpAmount(); // Start with full help amount

        while (currentUpliner != null && stage <= 10) {
            BigDecimal payout = calculateUplinePayout(payoutScheme, stage);

            // Check if the remaining help amount is sufficient for the payout
            if (remainingHelpAmount.compareTo(payout) >= 0) {
                // Distribute the payout to the upliner
                distributeUplinePayout(currentUpliner, payout, stage);

                // Subtract the payout from the remaining help amount
                remainingHelpAmount = remainingHelpAmount.subtract(payout);

                // Update the payment record with the remaining amount after each payout
                paymentDetails.setOfaHelpAmount(remainingHelpAmount);
                paymentDetailService.updatePayment(paymentDetails.getOfaPaymentId(), ResponseUtils.convertToDTO(paymentDetails));
            }

            // Move to the next upliner
            currentUpliner = currentUpliner.getReferredBy();
            stage++;
        }

        // In case the upliner hierarchy is not available, fallback to root admin case
        if (currentUpliner == null && stage <= 10) {
            distributeToRootAdmin(remainingHelpAmount, stage, paymentDetails);
        }
    }

    // Distribute remaining amount to root admin if no upliners are present
    private void distributeToRootAdmin(BigDecimal remainingHelpAmount, int stage, PaymentDetails paymentDetails) {
        Member rootAdmin = getRootAdmin(); // Fetch root admin details
        Map<Integer, BigDecimal> payoutScheme = getPayoutSchemeFromProperties();

        while (stage <= 10 && remainingHelpAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal payout = calculateUplinePayout(payoutScheme, stage);

            if (remainingHelpAmount.compareTo(payout) >= 0) {
                distributeUplinePayout(rootAdmin, payout, stage);
                remainingHelpAmount = remainingHelpAmount.subtract(payout);

                // Update payment details
                paymentDetails.setOfaHelpAmount(remainingHelpAmount);
                paymentDetailService.updatePayment(paymentDetails.getOfaPaymentId(), ResponseUtils.convertToDTO(paymentDetails));
            }

            stage++;
        }
    }

    // Get payout scheme dynamically from properties file
    private Map<Integer, BigDecimal> getPayoutSchemeFromProperties() {
        Map<Integer, BigDecimal> payoutScheme = new HashMap<>();
        
        // Loop through levels L1 to L10
        for (int level = 1; level <= 10; level++) {
            String propertyKey = "mlm.payout.L" + level;
            String defaultValue = "1000"; // Default value if not set in the properties file
            BigDecimal payout = new BigDecimal(environment.getProperty(propertyKey, defaultValue));
            
            payoutScheme.put(level, payout);
        }
        
        return payoutScheme;
    }

    private BigDecimal calculateUplinePayout(Map<Integer, BigDecimal> payoutScheme, int stage) {
        return payoutScheme.getOrDefault(stage, BigDecimal.ZERO);
    }

    // Distribute payout to upliners or root admin
    private void distributeUplinePayout(Member currentUpliner, BigDecimal payout, int stage) {
        if (currentUpliner != null) {
            // Pay to upliner
            PaymentDetails uplinerPayment = new PaymentDetails();
            uplinerPayment.setOfaConsumerName(currentUpliner.getOfaFullName());
            uplinerPayment.setOfaHelpAmount(payout);
            uplinerPayment.setOfaPaymentStatus(PaymentStatus.PAID);
            uplinerPayment.setOfaStageNo(stage);
            uplinerPayment.setOfaCreatedAt(new Date());
            uplinerPayment.setOfaUpdatedAt(new Date());

            paymentDetailRepository.save(uplinerPayment);
        }
    }

 
 // Method to get all upliner details along with the amount to be paid
    public List<Map<String, Object>> getUplinerDetailsWithPayout(String memberId) {
        Optional<Member> currentMemberOpt = userRepository.findByOfaMemberId(memberId);
        if (!currentMemberOpt.isPresent()) {
            throw new IllegalArgumentException("Member with ID " + memberId + " not found.");
        }

        Member currentMember = currentMemberOpt.get();
        Member currentUpliner = currentMember.getReferredBy();
        int stage = 1;

        // Dynamic payout scheme from properties
        Map<Integer, BigDecimal> payoutScheme = getPayoutSchemeFromProperties();

        List<Map<String, Object>> uplinerDetailsList = new ArrayList<>();

        // Traverse upliner chain up to 10 levels
        while (currentUpliner != null && stage <= 10) {
            BigDecimal payout = calculateUplinePayout(payoutScheme, stage);

            // Add upliner details to the list
            Map<String, Object> uplinerDetails = new HashMap<>();
            uplinerDetails.put("stage", stage);
            uplinerDetails.put("uplinerMemberId", currentUpliner.getOfaMemberId());
            uplinerDetails.put("uplinerName", currentUpliner.getOfaFullName());
            uplinerDetails.put("payoutAmount", payout);

            uplinerDetailsList.add(uplinerDetails);

            // Move to the next upliner
            currentUpliner = currentUpliner.getReferredBy();
            stage++;
        }

        // In case the upliner chain is shorter than 10 levels, fallback to root admin
        if (stage <= 10 && currentUpliner == null) {
            distributeToRootAdminForUplinerList(uplinerDetailsList, stage, payoutScheme);
        }

        return uplinerDetailsList;
    }
    private void distributeToRootAdminForUplinerList(List<Map<String, Object>> uplinerDetailsList, int startStage, Map<Integer, BigDecimal> payoutScheme) {
        // Fetch root admin details (implement getRootAdmin logic based on your system)
        Member rootAdmin = getRootAdmin();

        if (rootAdmin == null) {
            throw new IllegalStateException("Root admin not found in the system.");
        }

        // Continue distributing the remaining stages (up to 10) to the root admin
        while (startStage <= 10) {
            BigDecimal payout = calculateUplinePayout(payoutScheme, startStage);

            // Add root admin details to the list for this stage
            Map<String, Object> rootAdminDetails = new HashMap<>();
            rootAdminDetails.put("stage", startStage);
            rootAdminDetails.put("uplinerMemberId", rootAdmin.getOfaMemberId());
            rootAdminDetails.put("uplinerName", rootAdmin.getOfaFullName());
            rootAdminDetails.put("payoutAmount", payout);

            // Add to upliner details list
            uplinerDetailsList.add(rootAdminDetails);

            // Move to the next stage
            startStage++;
        }
    }
    private Member getRootAdmin() {
        // Example: Fetch the root admin based on a predefined ID or role
        String rootAdminId = environment.getProperty("mlm.root.admin.id", "1");  // Root admin ID from properties
        Optional<Member> rootAdminOpt = userRepository.findByOfaMemberId(rootAdminId);

        if (rootAdminOpt.isPresent()) {
            return rootAdminOpt.get();
        } else {
            throw new IllegalStateException("Root admin not found with ID: " + rootAdminId);
        }
    }

}
