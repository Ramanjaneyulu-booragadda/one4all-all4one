package com.newbusiness.one4all.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ofa_referrer_details", schema = "one4all")
public class ReferrerDetails {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private String memberId; // References ofa_user_reg_details.ofa_member_id

    @Column(name = "referrer_id")
    private String referrerId; // References ofa_user_reg_details.ofa_member_id, nullable if no referrer

    @Column(name = "referral_level", nullable = false)
    private Integer referralLevel; // Indicates the referral level of this member

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getReferrerId() {
		return referrerId;
	}

	public void setReferrerId(String referrerId) {
		this.referrerId = referrerId;
	}

	public Integer getReferralLevel() {
		return referralLevel;
	}

	public void setReferralLevel(Integer referralLevel) {
		this.referralLevel = referralLevel;
	}
    
    
}
