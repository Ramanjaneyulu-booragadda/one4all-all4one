package com.newbusiness.one4all.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.newbusiness.one4all.model.PaymentDetails;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetails, Long> {
	
	 // Custom query method to find by ofaConsumerNo
    Optional<PaymentDetails> findByOfaConsumerNo(String ofaConsumerNo);
    List<PaymentDetails> findAllByOfaConsumerNo(String ofaConsumerNo);
//    List<PaymentDetails> findALLByOfaParentConsumerNo(String ofaConsumerNo);
//    Optional<PaymentDetails> findByOfaParentConsumerNo(String ofaConsumerNo);
    @Query("SELECT p FROM PaymentDetails p WHERE p.ofaConsumerNo = :ofaConsumerNo ORDER BY p.ofaUpdatedAt DESC")
    List<PaymentDetails> findAllByConsumerNoOrderByCreatedAtDesc(@Param("ofaConsumerNo") String ofaConsumerNo);
}
