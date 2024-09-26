package com.newbusiness.one4all.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

import com.newbusiness.one4all.dto.LoginRequest;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.util.ResponseUtils;

@Service
public class MemberService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Value("${id.prefix}")
    private String idPrefix;

    @Value("${id.number-length}")
    private int numberLength;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder; // Autowire the password encoder

    // Register a new member
    public Member registerNewMember(Member member) {
        // Generate the custom ID
        member.setOfaMemberId(ResponseUtils.generateCustomId(idPrefix, numberLength));
        member.setOfaPassword(passwordEncoder.encode(member.getOfaPassword()));
        return userRepository.save(member);
    }

    // Validate login by comparing passwords
    public Optional<Member> validateLogin(LoginRequest loginRequest) {
        Optional<Member> memberOpt = userRepository.findByOfaMemberId(loginRequest.getOfaMemberId());
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            if (passwordEncoder.matches(loginRequest.getOfaPassword(), member.getOfaPassword())) {
                return Optional.of(member);
            }
        }
        return Optional.empty();
    }

    // Implement UserDetailsService's method
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load member by email or username (in this case, member ID or email)
        Optional<Member> memberOpt = userRepository.findByOfaMemberId(username);
        
        if (!memberOpt.isPresent()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        
        Member member = memberOpt.get();
        // Return Spring Security's UserDetails object with roles and permissions
        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getOfaMemberId()) // Use email as username
                .password(member.getOfaPassword()) // Encoded password
                .roles("USER") // For now, assign a simple role, can be customized
                .build();
    }

    // CRUD operations and other member-related methods
    public Optional<Member> getMemberById(Long memberId) {
        return userRepository.findById(memberId);
    }

    public List<Member> getAllMembers() {
        return userRepository.findAll();
    }

    public Optional<Member> updateMember(Long memberId, Member memberDetails) {
        Optional<Member> member = userRepository.findById(memberId);
        if (member.isPresent()) {
            Member existingMember = member.get();
            existingMember.setOfaFullName(memberDetails.getOfaFullName());
            existingMember.setOfaMobileNo(memberDetails.getOfaMobileNo());
            // Update other fields
            userRepository.save(existingMember);
            return Optional.of(existingMember);
        }
        return Optional.empty();
    }

    public boolean deleteMember(Long memberId) {
        Optional<Member> member = userRepository.findById(memberId);
        if (member.isPresent()) {
            userRepository.delete(member.get());
            return true;
        }
        return false;
    }
}
