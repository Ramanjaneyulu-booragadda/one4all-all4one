package com.newbusiness.one4all.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.newbusiness.one4all.model.PaymentDetails;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetails, Long> {
	

}
