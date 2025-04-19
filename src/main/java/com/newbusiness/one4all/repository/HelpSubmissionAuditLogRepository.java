package com.newbusiness.one4all.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newbusiness.one4all.model.HelpSubmissionAuditLog;

@Repository
public interface HelpSubmissionAuditLogRepository extends JpaRepository<HelpSubmissionAuditLog, Long> {
	// ✅ Fix: property path uses helpSubmission.id (not paymentId directly)
	List<HelpSubmissionAuditLog> findByHelpSubmission_Id(Long paymentId);


}

