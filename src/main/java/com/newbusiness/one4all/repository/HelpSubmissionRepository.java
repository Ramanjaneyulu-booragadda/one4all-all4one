package com.newbusiness.one4all.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.newbusiness.one4all.dto.HelpDashboardProjection;
import com.newbusiness.one4all.model.HelpSubmission;
import com.newbusiness.one4all.util.SubmissionStatus;



@Repository
public interface HelpSubmissionRepository extends JpaRepository<HelpSubmission, Long> {

    List<HelpSubmission> findBySenderMemberId(String senderMemberId);

    List<HelpSubmission> findByReceiverMemberId(String receiverMemberId);

    List<HelpSubmission> findBySubmissionStatus(SubmissionStatus submissionStatus);

    boolean existsBySenderMemberIdAndReceiverMemberIdAndUplinerLevel(
            String senderMemberId,
            String receiverMemberId,
            Integer uplinerLevel
    );
    Optional<HelpSubmission> findBySenderMemberIdAndReceiverMemberIdAndUplinerLevelAndSubmissionStatusNot(
    	    String senderMemberId,
    	    String receiverMemberId,
    	    Integer uplinerLevel,
    	    SubmissionStatus excludedStatus
    	);
    @Query(value = "SELECT * FROM v_help_status_dashboard WHERE member_id = :memberId", nativeQuery = true)
    HelpDashboardProjection getHelpSummary(@Param("memberId") String memberId);


}
