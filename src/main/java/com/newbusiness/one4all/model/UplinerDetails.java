package com.newbusiness.one4all.model;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ofa_upliner_details", schema = "one4all")
public class UplinerDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ofa_upliner_id")  // Primary key
    private Long ofaUplinerId;

    @Column(name = "ofa_stage_no")
    private int ofaStageNo;

    // Refers to the current member
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ofa_member_id")
    private Member member;

    // Refers to the upliner (another member)
    // Rename the column to avoid conflict with the primary key
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ofa_upliner_member_id")  // Renamed to avoid conflict with primary key
    private Member uplinerMember;

    // Getters and setters
    public Long getOfaUplinerId() {
        return ofaUplinerId;
    }

    public void setOfaUplinerId(Long ofaUplinerId) {
        this.ofaUplinerId = ofaUplinerId;
    }

    public int getOfaStageNo() {
        return ofaStageNo;
    }

    public void setOfaStageNo(int ofaStageNo) {
        this.ofaStageNo = ofaStageNo;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Member getUplinerMember() {
        return uplinerMember;
    }

    public void setUplinerMember(Member uplinerMember) {
        this.uplinerMember = uplinerMember;
    }
}
