package com.newbusiness.one4all.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.newbusiness.one4all.model.Member;

public interface UserRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByOfaMemberId(String ofaMemberId);
	List<Member> findALLByOfaMemberId(String ofaMemberId);
	boolean existsByOfaMemberId(String ofaMemberId);
	Optional<Member> findByOfaEmail(String email); 
	List<Member> findByOfaMemberIdIn(List<String> memberIds);
	@Query("SELECT m FROM Member m WHERE m.ofaMemberId NOT IN (SELECT r.memberId FROM ReferrerDetails r)")
	Page<Member> findUnassignedMembers(Pageable pageable);
}
