package com.newbusiness.one4all.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newbusiness.one4all.model.Member;

public interface UserRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByOfaMemberId(String ofaMemberId);
	List<Member> findALLByOfaMemberId(String ofaMemberId);
	boolean existsByOfaMemberId(String ofaMemberId); 
}
