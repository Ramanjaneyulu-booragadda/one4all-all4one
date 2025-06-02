package com.newbusiness.one4all.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newbusiness.one4all.model.UplinerDetails;

@Repository
public interface UplinerDetailsRepository extends JpaRepository<UplinerDetails, Long> {

	 // Find all upliners for a given member
    List<UplinerDetails> findByMemberId(String memberId);
   
}