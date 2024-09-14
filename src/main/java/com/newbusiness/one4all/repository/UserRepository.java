package com.newbusiness.one4all.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newbusiness.one4all.model.Member;

public interface UserRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByOfaMemberIdAndOfaPassword(String ofaMemberId, String ofaPassword);

}
