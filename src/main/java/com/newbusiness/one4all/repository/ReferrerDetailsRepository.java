package com.newbusiness.one4all.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newbusiness.one4all.model.ReferrerDetails;

@Repository
public interface ReferrerDetailsRepository extends JpaRepository<ReferrerDetails, Long> {

    // Find referral details by member ID
    Optional<ReferrerDetails> findByMemberId(String memberId);

    // Find referral details by referrer ID
    Optional<ReferrerDetails> findByReferrerId(String referrerId);
    List<ReferrerDetails> findAllByReferrerId(String referrerId);
}