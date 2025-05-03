package com.newbusiness.one4all.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.newbusiness.one4all.dto.HelpDashboardProjection;
import com.newbusiness.one4all.dto.ReceivedHelpDetailDTO;
import com.newbusiness.one4all.dto.ReceivedHelpSummaryDTO;
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
    @Query(value = """
    	    SELECT 
    	        member_id AS memberId,
    	        full_name AS fullName,
    	        total_help_given AS totalHelpGiven,
    	        total_amount_given AS totalAmountGiven,
    	        total_amount_received_ack AS totalAmountReceivedAck,
    	        total_amount_pending AS totalAmountPending,
    	        available_balance AS availableBalance,
    	        last_received_date AS lastReceivedDate,
    	        pending_help_count AS pendingHelpCount,
    	        completed_help_count AS completedHelpCount, 
    	        this_month_total AS thisMonthTotal 
    	    FROM v_help_status_dashboard
    	    WHERE member_id = :memberId
    	""", nativeQuery = true)
    HelpDashboardProjection getHelpSummary(@Param("memberId") String memberId);

    Optional<HelpSubmission> findBySenderMemberIdAndReceiverMemberIdAndUplinerLevel(String senderId, String receiverId, int level);
    List<HelpSubmission> findAllBySenderMemberId(String senderMemberId);

    @Query(value = """
    SELECT 
      member_id AS memberId,
      received_from AS receivedFrom,
      transaction_id AS transactionId,
      request_received_at AS requestReceivedAt,
      request_modified_at AS requestModifiedAt,
      received_amount AS receivedAmount,
      status AS status,
      payment_id AS paymentID ,
      proof_doc as proofDoc
    FROM v_received_help_summary
    WHERE member_id = :memberId
  """, nativeQuery = true)
    List<ReceivedHelpDetailDTO> getReceivedHelpDetails(@Param("memberId") String memberId);

    @Query(value = """
    		  SELECT 
    		    member_id AS memberId,
    		    approved_request_count AS approvedRequestCount,
    		    rejected_request_count AS rejectedRequestCount,
    		    total_request_count AS totalRequestCount,
    		    total_received_amount AS totalReceivedAmount,
    		    this_month_received_amount AS thisMonthTotal
    		  FROM v_received_help_totals
    		  WHERE member_id = :memberId
    		""", nativeQuery = true)
    ReceivedHelpSummaryDTO getReceivedHelpSummary(@Param("memberId") String memberId);

}
