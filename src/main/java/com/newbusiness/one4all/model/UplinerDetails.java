package com.newbusiness.one4all.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Entity
@Table(name = "ofa_upliner_details", schema = "one4all")
public class UplinerDetails implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private String memberId; // References ofa_user_reg_details.ofa_member_id

    @Column(name = "upliner_id")
    private String uplinerId; // References ofa_user_reg_details.ofa_member_id, indicates the upliner for this member

    @Column(name = "upliner_level", nullable = false)
    private Integer uplinerLevel; // Indicates the level of this upliner relationship (1 to 10)
    @Transient
    private String consumerMobile;
	
   
    
}
