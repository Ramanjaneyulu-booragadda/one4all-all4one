package com.newbusiness.one4all.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.newbusiness.one4all.dto.LoginRequest;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.util.ResponseUtils;

@Service
public class MemberService {
    @Autowired
    private UserRepository userRepository;

    @Value("${id.prefix}")
    private String idPrefix;

    @Value("${id.number-length}")
    private int numberLength;

    public Member registerNewMember(Member member) {
        // Generate the custom ID
        member.setOfaMemberId(ResponseUtils.generateCustomId(idPrefix,numberLength));

        // Here you can implement other registration logic, like encrypting the password
        return userRepository.save(member);
    }
    public Optional<Member> validateLogin(LoginRequest loginRequest) {
        return userRepository.findByOfaMemberIdAndOfaPassword(loginRequest.getOfaMemberId(), loginRequest.getOfaPassword());
    }

 // READ - Get a single member by ID
    public Optional<Member> getMemberById(Long memberId) {
        return userRepository.findById(memberId);
    }

    // READ - Get all members
    public List<Member> getAllMembers() {
        return userRepository.findAll();
    }

    // UPDATE - Update an existing member
    public Optional<Member> updateMember(Long memberId, Member memberDetails) {
        Optional<Member> member = userRepository.findById(memberId);
        if (member.isPresent()) {
            Member existingMember = member.get();
            // Update the necessary fields from memberDetails
            existingMember.setOfaFullName(memberDetails.getOfaFullName());
            existingMember.setOfaMobileNo(memberDetails.getOfaMobileNo());
            // ... update other fields
            userRepository.save(existingMember);
            return Optional.of(existingMember);
        }
        return Optional.empty();
    }

    // DELETE - Delete a member
    public boolean deleteMember(Long memberId) {
        Optional<Member> member = userRepository.findById(memberId);
        if (member.isPresent()) {
            userRepository.delete(member.get());
            return true;
        }
        return false;
    }
}
