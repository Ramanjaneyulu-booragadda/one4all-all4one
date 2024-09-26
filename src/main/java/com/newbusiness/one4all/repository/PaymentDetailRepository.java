package com.newbusiness.one4all.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newbusiness.one4all.model.PaymentDetails;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetails, Long> {
	
	 // Custom query method to find by ofaConsumerNo
    Optional<PaymentDetails> findByOfaConsumerNo(String ofaConsumerNo);
}
