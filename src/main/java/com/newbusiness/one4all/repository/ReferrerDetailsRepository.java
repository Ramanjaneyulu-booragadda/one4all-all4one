package com.newbusiness.one4all.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.ReferrerDetails;

@Repository
public interface ReferrerDetailsRepository extends JpaRepository<ReferrerDetails, Long> {

    // Find referral details by member ID
    Optional<ReferrerDetails> findByMemberId(String memberId);

    // Find referral details by referrer ID
    Optional<ReferrerDetails> findByReferrerId(String referrerId);
    List<ReferrerDetails> findAllByReferrerId(String referrerId);
    @Query("SELECT r.memberId FROM ReferrerDetails r WHERE r.referrerId = :parentMemberId")
    List<String> findDirectReferralMemberIds(@Param("parentMemberId") String parentMemberId);

}