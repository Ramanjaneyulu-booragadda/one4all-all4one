package com.newbusiness.one4all.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbusiness.one4all.dto.DownlinerHelpInfoDto;
import com.newbusiness.one4all.dto.DownlinerHierarchyDTO;
import com.newbusiness.one4all.dto.DownlinerWithMemberDetailsDTO;
import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.HelpSubmission;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.ReferrerDetails;
import com.newbusiness.one4all.model.UplinerDetails;
import com.newbusiness.one4all.repository.HelpSubmissionRepository;
import com.newbusiness.one4all.repository.ReferrerDetailsRepository;
import com.newbusiness.one4all.repository.UplinerDetailsRepository;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.util.SubmissionStatus;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ReferralService {

    @Autowired
    private UserRepository memberRepository;

    @Autowired
    private ReferrerDetailsRepository referrerDetailsRepository;

    @Autowired
    private UplinerDetailsRepository uplinerDetailsRepository;
    @Autowired
	private HelpSubmissionRepository helpSubmissionRepository;

    public ReferrerDetails addReferer(String memberId, String referrerId, int referralLevel) {
        log.info("Adding referer: memberId={}, referrerId={}, referralLevel={}", memberId, referrerId, referralLevel);
        Optional<Member> memberOpt = memberRepository.findByOfaMemberId(memberId);
        if (memberOpt.isEmpty()) {
            log.warn("Member does not exist with ID: {}", memberId);
            throw new IllegalArgumentException("Member does not exist with ID: " + memberId);
        }

        // ✅ 2. Check if the referrer exists in ofa_user_reg_details
        Optional<Member> referrerOpt = memberRepository.findByOfaMemberId(referrerId);
        if (referrerOpt.isEmpty()) {
            throw new IllegalArgumentException("Referrer does not exist with ID: " + referrerId);
        }

        // ✅ 3. Fetch existing children of the referrer
        List<ReferrerDetails> immediateMembers = referrerDetailsRepository.findAllByReferrerId(referrerId);

        // ✅ 4. Validation for SPLN-prefixed IDs
        if (referrerId.startsWith("SPLN")) {
            if (immediateMembers.size() >= 1) {
                throw new IllegalStateException("SPLN-prefixed referrer already has one child.");
            }
        } 
        // ✅ 5. Validation for Non-SPLN IDs (Binary Tree Rule)
        else {
            if (immediateMembers.size() >= 2) {
                throw new IllegalStateException("Referrer already has 2 direct members and cannot add more.");
            }
        }

        // ✅ 6. Create or update the entry in ofa_referrer_details
        ReferrerDetails referrerDetails = new ReferrerDetails();
        referrerDetails.setMemberId(memberId);
        referrerDetails.setReferrerId(referrerId);
        referrerDetails.setReferralLevel(referralLevel);
        referrerDetailsRepository.save(referrerDetails);

        // ✅ 7. Establish upliner relationships up to 10 levels
        List<UplinerDetails> uplinerDetailsList = new ArrayList<>();
        Optional<ReferrerDetails> currentReferrer = referrerDetailsRepository.findByMemberId(memberId);
        int currentLevel = 1;

        while (currentReferrer.isPresent() && currentLevel <= 10) {
            UplinerDetails uplinerDetails = new UplinerDetails();
            uplinerDetails.setMemberId(memberId);
            uplinerDetails.setUplinerId(currentReferrer.get().getReferrerId());
            uplinerDetails.setUplinerLevel(currentLevel);

            uplinerDetailsList.add(uplinerDetails);
            currentReferrer = referrerDetailsRepository.findByMemberId(currentReferrer.get().getReferrerId());
            currentLevel++;
        }

        // ✅ 8. Save upliner relationships in bulk
        uplinerDetailsRepository.saveAll(uplinerDetailsList);

        log.info("Referer added successfully for memberId={}", memberId);
        return referrerDetails;
    }

    
    
    
    
    public Optional<ReferrerDetails> findByMemberId(String ofaConsumerNo) {
        return referrerDetailsRepository.findByMemberId(ofaConsumerNo);
    }
    
    public List<DownlinerWithMemberDetailsDTO> getDownliners(String memberId) {
        log.info("Fetching downliners for memberId={}", memberId);
        // Fetch all downliner details for the given memberId
        List<ReferrerDetails> downlinerDetailsList = referrerDetailsRepository.findAllByReferrerId(memberId);

        // Map each ReferrerDetails to DownlinerWithMemberDetailsDTO
        List<DownlinerWithMemberDetailsDTO> downliners = downlinerDetailsList.stream().map(downlinerDetails -> {
            // Fetch the full member details for the downlinerId
            Optional<Member> downlinerMemberOpt = memberRepository.findByOfaMemberId(downlinerDetails.getMemberId());

            // Build the DTO
            return new DownlinerWithMemberDetailsDTO(
                downlinerDetails.getId(),
                downlinerDetails.getMemberId(),
                downlinerDetails.getReferralLevel(),
                downlinerMemberOpt.orElse(null) // Add downliner member details, or null if not found
            );
        }).collect(Collectors.toList());
        log.info("Fetched {} downliners for memberId={}", downliners.size(), memberId);
        return downliners;
    }
    public List<UplinerWithMemberDetailsDTO> getUpliners(String memberId) {
        log.info("Fetching upliners for memberId={}", memberId);
        // 1. Fetch all upliner details for the given member ID
    List<UplinerDetails> uplinerDetailsList = uplinerDetailsRepository.findByMemberId(memberId);

    // 2. Bulk fetch help submissions where this member is the sender
    List<HelpSubmission> helpSubmissions = helpSubmissionRepository.findAllBySenderMemberId(memberId);

    // 3. Convert help submissions into a lookup map with key = "receiverId_level"
    Map<String, HelpSubmission> helpMap = helpSubmissions.stream()
        .collect(Collectors.toMap(
            hs -> hs.getReceiverMemberId() + "_" + hs.getUplinerLevel(),
            hs -> hs
        ));

    // 4. Loop through upliner details and enrich DTOs
    List<UplinerWithMemberDetailsDTO> upliners = uplinerDetailsList.stream().map(uplinerDetails -> {
        Optional<Member> uplinerMemberOpt = memberRepository.findByOfaMemberId(uplinerDetails.getUplinerId());
        Optional<Member> currentMemberOpt = memberRepository.findByOfaMemberId(uplinerDetails.getMemberId());

        UplinerWithMemberDetailsDTO detailsDTO = new UplinerWithMemberDetailsDTO();
        detailsDTO.setId(uplinerDetails.getId());
        detailsDTO.setMemberId(uplinerDetails.getMemberId());
        detailsDTO.setUplinerLevel(uplinerDetails.getUplinerLevel());
        detailsDTO.setConsumerMobile(currentMemberOpt.map(Member::getOfaMobileNo).orElse(null));
        detailsDTO.setLevelAmount(getPayoutForPhase(uplinerDetails.getUplinerLevel()));
        detailsDTO.setUplinerName(uplinerMemberOpt.map(Member::getOfaFullName).orElse(null));
        detailsDTO.setUplinerMobileNo(uplinerMemberOpt.map(Member::getOfaMobileNo).orElse(null));
        detailsDTO.setUplinerMemberId(uplinerMemberOpt.map(Member::getOfaMemberId).orElse(null));

        // Check in the pre-fetched help map
        String key = uplinerDetails.getUplinerId() + "_" + uplinerDetails.getUplinerLevel();
        HelpSubmission matchedSubmission = helpMap.get(key);

        if (matchedSubmission != null) {
            detailsDTO.setStatus(matchedSubmission.getSubmissionStatus());
            detailsDTO.setProof(matchedSubmission.getProof());
            detailsDTO.setTransactionReferenceId(matchedSubmission.getSubmissionReferenceId());
        } else {
            detailsDTO.setStatus(SubmissionStatus.UNPAID);
        }

        return detailsDTO;
    }).collect(Collectors.toList());
    log.info("Fetched {} upliners for memberId={}", upliners.size(), memberId);
    return upliners;
}
    
    public DownlinerHierarchyDTO getDownlinerHierarchy(String memberId) {
        log.info("Fetching downliner hierarchy for memberId={}", memberId);
        // Fetch member details for the current member
        Optional<Member> memberOpt = memberRepository.findByOfaMemberId(memberId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Member does not exist with ID: " + memberId);
        }
        Member member = memberOpt.get();

        // Fetch all immediate downliners for this member
        List<ReferrerDetails> immediateDownliners = referrerDetailsRepository.findAllByReferrerId(memberId);

        // Calculate positions left (binary tree: max 2 immediate downliners)
        int positionsLeft = 2 - immediateDownliners.size();

     // Recursively fetch downliner hierarchies for each immediate downliner
        List<DownlinerHierarchyDTO> children = immediateDownliners.stream()
                .map(downliner -> getDownlinerHierarchy(downliner.getMemberId())) // Recursive call
                .collect(Collectors.toList());
        
        // Gather all children member details recursively
        List<Member> allChildrenMembers = new ArrayList<>();
        for (DownlinerHierarchyDTO child : children) {
            // Add the current child member details
            Optional<Member> childMemberOpt = memberRepository.findByOfaMemberId(child.getMemberId());
            childMemberOpt.ifPresent(allChildrenMembers::add);

            // Add all recursive children member details
            //allChildrenMembers.addAll(child.getAllChildrenMembers());
        }

        // Build and return the hierarchy DTO
        log.info("Fetched downliner hierarchy for memberId={}", memberId);
        return new DownlinerHierarchyDTO(
                member.getOfaMemberId(),
                member.getOfaFullName(),
                positionsLeft,
                children
                //,                allChildrenMembers
        );
        
    }
    public boolean isDirectChild(String memberId, String referrerId) {
        // Fetch all direct children of the referrer
        List<ReferrerDetails> immediateMembers = referrerDetailsRepository.findAllByReferrerId(referrerId);

        // Check if the memberId exists in the direct children list
        return immediateMembers.stream()
            .anyMatch(referral -> referral.getMemberId().equals(memberId));
    }
//    public boolean isDirectChildFromUplinerDetails(String consumerNo) {
//        // Fetch the upliner details for the given consumerNo
//        List<UplinerPaymentDetails> uplinerDetails = uplinerPaymentDetailsRepository
//            .findByUplinerId(consumerNo);
//
//        // Check if the upliner details exist and the level is 1 (Direct Child)
//        return uplinerDetails.stream()
//            .anyMatch(upliner -> upliner.getUplinerLevel() == 1);
//    }

    private BigDecimal getPayoutForPhase(int level) {
		switch (level) {
		case 1:
			return new BigDecimal(2000);
		case 2:
			return new BigDecimal(1000);
		case 3:
			return new BigDecimal(1000);
		case 4:
			return new BigDecimal(2500);
		case 5:
			return new BigDecimal(5000);
		case 6:
			return new BigDecimal(10000);
		case 7:
			return new BigDecimal(20000);
		case 8:
			return new BigDecimal(30000);
		case 9:
			return new BigDecimal(40000);
		case 10:
			return new BigDecimal(50000);
		default:
			return BigDecimal.ZERO;
		}
	}
    
    public List<DownlinerHelpInfoDto> getDownlinerListWithLevelAndAmount(String memberId) {
        log.info("Fetching downliner list with level and amount for memberId={}", memberId);
        List<DownlinerHelpInfoDto> downlinerList = new ArrayList<>();
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member ID cannot be null or blank");
        }
        buildDownlinerListRecursive(memberId, 1, downlinerList);
        log.info("Fetched downliner list with level and amount for memberId={}", memberId);
        return downlinerList;
    }

    private void buildDownlinerListRecursive(String parentMemberId, int currentLevel, List<DownlinerHelpInfoDto> downlinerList) {
        if (currentLevel > 20) {
            return; // Maximum 20 levels as per your business rule
        }

        List<String> childMemberIds = referrerDetailsRepository.findDirectReferralMemberIds(parentMemberId);
        if (childMemberIds == null || childMemberIds.isEmpty()) {
            log.info("No direct referrals found for memberId: {}", parentMemberId);
            return;
        }

        List<Member> children = memberRepository.findByOfaMemberIdIn(childMemberIds);
        if (children == null || children.isEmpty()) {
            log.warn("Member IDs found in referrer table but corresponding user records missing for memberId: {}", parentMemberId);
            return;
        }

        for (Member child : children) {
            DownlinerHelpInfoDto dto = new DownlinerHelpInfoDto(
                    child.getOfaMemberId(),
                    child.getOfaFullName(),
                    currentLevel,
                    getPayoutForPhase(currentLevel)
            );
            downlinerList.add(dto);

            // Recursive call
            buildDownlinerListRecursive(child.getOfaMemberId(), currentLevel + 1, downlinerList);
        }
    }
}

