package com.newbusiness.one4all.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.newbusiness.one4all.model.UplinerPaymentDetails;

import java.util.List;

@Repository
public interface UplinerPaymentDetailsRepository extends JpaRepository<UplinerPaymentDetails, Long> {

    /**
     * Find all upliner payments associated with a specific transaction reference ID.
     *
     * @param transactionRefId the unique transaction reference ID
     * @return List of UplinerPaymentDetails
     */
    List<UplinerPaymentDetails> findByTransactionRefId(String transactionRefId);

    /**
     * Find all upliner payments for a specific upliner.
     *
     * @param uplinerId the unique ID of the upliner
     * @return List of UplinerPaymentDetails
     */
    List<UplinerPaymentDetails> findByUplinerId(String uplinerId);

    /**
     * Find all upliner payments for a specific upliner and transaction reference ID.
     *
     * @param transactionRefId the unique transaction reference ID
     * @param uplinerId        the unique ID of the upliner
     * @return List of UplinerPaymentDetails
     */
    List<UplinerPaymentDetails> findByTransactionRefIdAndUplinerId(String transactionRefId, String uplinerId);
}
