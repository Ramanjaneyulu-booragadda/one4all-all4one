package com.newbusiness.one4all.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
/*
 * @Getter
 * 
 * @Setter
 */
@Table(name = "ofa_user_reg_details",schema = "datalayer")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ofa_id")
    
    private Long ofaId;
	/* @NotBlank(message = "{member.id.notblank}") */
    @Column(name = "ofa_member_id", nullable = false)
    private String ofaMemberId;

    @Column(name = "ofa_full_name", nullable = false)
    @NotBlank(message = "{member.fullname.notblank}")
    @Size(max = 255, message = "{member.fullname.size}")
    private String ofaFullName;

    @Column(name = "ofa_gender", nullable = false)
    @NotBlank(message = "{member.gender.notblank}")
    @Size(max = 10, message = "{member.gender.size}")
    private String ofaGender;
    
    @NotNull(message = "{member.dob.notnull}")
    @Column(name = "ofa_dob", nullable = false)
    private LocalDate ofaDob;

    @Column(name = "ofa_address", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "{member.address.notblank}")
    private String ofaAddress;

    @Column(name = "ofa_pincode", nullable = false)
    @NotBlank(message = "{member.address.notblank}")
    @Size(min = 6,max=6, message = "{member.pincode.size}")
    private String ofaPincode;

    @Column(name = "ofa_mobile_no", nullable = false)
    @NotBlank(message = "{member.mobile.notblank}")
    @Size(min = 10,max = 12, message = "{member.mobile.size}")
    private String ofaMobileNo;

    @Column(name = "ofa_email", nullable = false)
    @NotBlank(message = "{member.email.notblank}")
    @Size(max = 255, message = "{member.email.invalid}")
    private String ofaEmail;

    @Column(name = "ofa_nationality", nullable = false)
    @NotBlank(message = "{member.nationality.notblank}")
    @Size(max = 50, message = "{member.nationality.size}")
    private String ofaNationallity;

    @Column(name = "ofa_password", nullable = false)
    @NotBlank(message = "{member.password.notblank}")
    private String ofaPassword;

    @Column(name = "ofa_created_by", nullable = false)
    @NotBlank(message = "{member.createdby.notblank}")
    private String ofaCreatedBy;

    @Column(name = "ofa_created_dt", nullable = false)
    @NotNull(message = "{member.createddt.notnull}")
    private LocalDate ofaCreatedDt;

    @Column(name = "ofa_updated_by", nullable = false)
    @NotBlank(message = "{member.updatedby.notblank}")
    private String ofaUpdatedBy;

    @Column(name = "ofa_updated_dt", nullable = false)
    @NotNull(message = "{member.updateddt.notnull}")
    private LocalDate ofaUpdatedDt;

    @Column(name = "ofa_is_deleted", nullable = false)
    @NotNull(message = "{member.deleted.notnull}")
    private Integer ofaIsDeleted;

	public Long getOfaId() {
		return ofaId;
	}

	public void setOfaId(Long ofaId) {
		this.ofaId = ofaId;
	}

	public String getOfaMemberId() {
		return ofaMemberId;
	}

	public void setOfaMemberId(String ofaMemberId) {
		this.ofaMemberId = ofaMemberId;
	}

	public String getOfaFullName() {
		return ofaFullName;
	}

	public void setOfaFullName(String ofaFullName) {
		this.ofaFullName = ofaFullName;
	}

	public String getOfaGender() {
		return ofaGender;
	}

	public void setOfaGender(String ofaGender) {
		this.ofaGender = ofaGender;
	}

	public LocalDate getOfaDob() {
		return ofaDob;
	}

	public void setOfaDob(LocalDate ofaDob) {
		this.ofaDob = ofaDob;
	}

	public String getOfaAddress() {
		return ofaAddress;
	}

	public void setOfaAddress(String ofaAddress) {
		this.ofaAddress = ofaAddress;
	}

	public String getOfaPincode() {
		return ofaPincode;
	}

	public void setOfaPincode(String ofaPincode) {
		this.ofaPincode = ofaPincode;
	}

	public String getOfaMobileNo() {
		return ofaMobileNo;
	}

	public void setOfaMobileNo(String ofaMobileNo) {
		this.ofaMobileNo = ofaMobileNo;
	}

	public String getOfaEmail() {
		return ofaEmail;
	}

	public void setOfaEmail(String ofaEmail) {
		this.ofaEmail = ofaEmail;
	}

	public String getOfaNationallity() {
		return ofaNationallity;
	}

	public void setOfaNationallity(String ofaNationallity) {
		this.ofaNationallity = ofaNationallity;
	}

	public String getOfaPassword() {
		return ofaPassword;
	}

	public void setOfaPassword(String ofaPassword) {
		this.ofaPassword = ofaPassword;
	}

	public String getOfaCreatedBy() {
		return ofaCreatedBy;
	}

	public void setOfaCreatedBy(String ofaCreatedBy) {
		this.ofaCreatedBy = ofaCreatedBy;
	}

	public LocalDate getOfaCreatedDt() {
		return ofaCreatedDt;
	}

	public void setOfaCreatedDt(LocalDate ofaCreatedDt) {
		this.ofaCreatedDt = ofaCreatedDt;
	}

	public String getOfaUpdatedBy() {
		return ofaUpdatedBy;
	}

	public void setOfaUpdatedBy(String ofaUpdatedBy) {
		this.ofaUpdatedBy = ofaUpdatedBy;
	}

	public LocalDate getOfaUpdatedDt() {
		return ofaUpdatedDt;
	}

	public void setOfaUpdatedDt(LocalDate ofaUpdatedDt) {
		this.ofaUpdatedDt = ofaUpdatedDt;
	}

	public Integer getOfaIsDeleted() {
		return ofaIsDeleted;
	}

	public void setOfaIsDeleted(Integer ofaIsDeleted) {
		this.ofaIsDeleted = ofaIsDeleted;
	}
    
}
