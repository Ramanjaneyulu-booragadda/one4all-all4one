package com.newbusiness.one4all.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbusiness.one4all.dto.DownlinerHierarchyDTO;
import com.newbusiness.one4all.dto.DownlinerWithMemberDetailsDTO;
import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.ReferrerDetails;
import com.newbusiness.one4all.model.UplinerDetails;
import com.newbusiness.one4all.repository.ReferrerDetailsRepository;
import com.newbusiness.one4all.repository.UplinerDetailsRepository;
import com.newbusiness.one4all.repository.UserRepository;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReferralService {

    @Autowired
    private UserRepository memberRepository;

    @Autowired
    private ReferrerDetailsRepository referrerDetailsRepository;

    @Autowired
    private UplinerDetailsRepository uplinerDetailsRepository;

    public ReferrerDetails addReferer(String memberId, String referrerId, int referralLevel) {
        // Check if the member exists in ofa_user_reg_details
        Optional<Member> memberOpt = memberRepository.findByOfaMemberId(memberId);
        if (memberOpt.isEmpty()) {
            throw new IllegalArgumentException("Member does not exist with ID: " + memberId);
        }

        // Check if the referrer exists in ofa_user_reg_details
        Optional<Member> referrerOpt = memberRepository.findByOfaMemberId(referrerId);
        if (referrerOpt.isEmpty()) {
            throw new IllegalArgumentException("Referrer does not exist with ID: " + referrerId);
        }
        // Check if the referrer already has 2 immediate members
        List<ReferrerDetails> immediateMembers = referrerDetailsRepository.findAllByReferrerId(referrerId);
        if (immediateMembers.size() >= 2) {
            throw new IllegalStateException("Referrer already has 2 immediate members and cannot add more.");
        }

        // Create or update the entry in ofa_referrer_details
        ReferrerDetails referrerDetails = new ReferrerDetails();
        referrerDetails.setMemberId(memberId);
        referrerDetails.setReferrerId(referrerId);
        referrerDetails.setReferralLevel(referralLevel);
        referrerDetailsRepository.save(referrerDetails);

        // Establish upliner relationships up to 10 levels
        List<UplinerDetails> uplinerDetailsList = new ArrayList<>();
        Optional<ReferrerDetails> currentReferrer = referrerDetailsRepository.findByMemberId(memberId);
        int currentLevel = 1;

        // Traverse up the hierarchy, adding upliner relationships up to 10 levels
        while (currentReferrer.isPresent() && currentLevel <= 10) {
            UplinerDetails uplinerDetails = new UplinerDetails();
            uplinerDetails.setMemberId(memberId);
            uplinerDetails.setUplinerId(currentReferrer.get().getReferrerId());
            uplinerDetails.setUplinerLevel(currentLevel);

            uplinerDetailsList.add(uplinerDetails);
            currentReferrer = referrerDetailsRepository.findByMemberId(currentReferrer.get().getReferrerId());
            currentLevel++;
        }

        // Save upliner relationships in bulk
        uplinerDetailsRepository.saveAll(uplinerDetailsList);

        return referrerDetails;
    }
    
    public Optional<ReferrerDetails> findByMemberId(String ofaConsumerNo) {
        return referrerDetailsRepository.findByMemberId(ofaConsumerNo);
    }
    
    public List<DownlinerWithMemberDetailsDTO> getDownliners(String referrerId) {
    	// Fetch all downliner details for the given memberId
        List<ReferrerDetails> downlinerDetailsList = referrerDetailsRepository.findAllByReferrerId(referrerId);

        // Map each ReferrerDetails to DownlinerWithMemberDetailsDTO
        return downlinerDetailsList.stream().map(downlinerDetails -> {
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
    }
    public List<UplinerWithMemberDetailsDTO> getUpliners(String memberId) {
    	// Fetch all upliner details for the given member ID
        List<UplinerDetails> uplinerDetailsList = uplinerDetailsRepository.findByMemberId(memberId);

        // Map each UplinerDetails to UplinerWithMemberDetailsDTO
        return uplinerDetailsList.stream().map(uplinerDetails -> {
            // Fetch the full member details for the uplinerId
            Optional<Member> uplinerMemberOpt = memberRepository.findByOfaMemberId(uplinerDetails.getUplinerId());
            // Fetch the full member details for the current member
            Optional<Member> currentMemberOpt = memberRepository.findByOfaMemberId(uplinerDetails.getMemberId());

            // Build the DTO
            return new UplinerWithMemberDetailsDTO(
                uplinerDetails.getId(),
                uplinerDetails.getMemberId(),
                uplinerDetails.getUplinerLevel(),
                currentMemberOpt.get().getOfaMobileNo(),
                uplinerMemberOpt.orElse(null) // Add upliner member details, or null if not found
            );
        }).collect(Collectors.toList());
    }

    public DownlinerHierarchyDTO getDownlinerHierarchy(String memberId) {
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
        return new DownlinerHierarchyDTO(
                member.getOfaMemberId(),
                member.getOfaFullName(),
                positionsLeft,
                children
                //,                allChildrenMembers
        );
        
    }

}

