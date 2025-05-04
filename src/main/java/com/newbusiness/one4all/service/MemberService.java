package com.newbusiness.one4all.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.newbusiness.one4all.dto.LoginRequest;
import com.newbusiness.one4all.dto.MemberProfileResponse;
import com.newbusiness.one4all.dto.UnassignedMemberToReffererSystemDto;
import com.newbusiness.one4all.dto.UpdateProfileRequest;
import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.Role;
import com.newbusiness.one4all.model.UplinerDetails;
import com.newbusiness.one4all.repository.RoleRepository;
import com.newbusiness.one4all.repository.UplinerDetailsRepository;
import com.newbusiness.one4all.repository.UserRepository;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.ResponseUtils;
import com.newbusiness.one4all.util.SecurityUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;



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
@Autowired
	private RoleRepository roleRepository;
@Autowired
private UplinerDetailsRepository uplinerDetailsRepository;

    // Register a new member
    public Member registerNewMember(Member member, Set<String> roleNames) {
        // Generate the custom ID
        member.setOfaMemberId(ResponseUtils.generateCustomId(idPrefix, numberLength));
        member.setOfaPassword(passwordEncoder.encode(member.getOfaPassword()));
     // Retrieve roles from the database
        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByRoleName(roleName))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        member.setRoles(roles);
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
    
 // Bulk register members
    public List<Member> bulkRegisterMembers(List<Member> members) {
        return members.stream().map(member -> {
            member.setOfaMemberId(ResponseUtils.generateCustomId(idPrefix, numberLength));
            member.setOfaPassword(passwordEncoder.encode(member.getOfaPassword()));
            return userRepository.save(member);
        }).collect(Collectors.toList());
    }
    
    public void assignRoles(Member member, Set<String> roleNames) {
        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByRoleName(roleName))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        member.setRoles(roles);
        userRepository.save(member);
    }
    public MemberProfileResponse getMemberProfile(String memberId) {
        Member user = userRepository.findByOfaMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with memberId: " + memberId));

        MemberProfileResponse response = new MemberProfileResponse();

        if (user.getOfaFullName() != null) {
            
            response.setFullName(user.getOfaFullName() );
            
        }
        
        response.setEmail(user.getOfaEmail());
        response.setPhoneNumber(user.getOfaMobileNo());
        response.setAddress(user.getOfaAddress());
        response.setConsumerNumber(user.getOfaMemberId());
        response.setAccountCreatedDate(user.getOfaCreatedDt() != null ? user.getOfaCreatedDt().toString() : "");

        // Find Parent/Upliner
        List<UplinerDetails> upliners = uplinerDetailsRepository.findByMemberId(memberId);
        if (upliners != null && !upliners.isEmpty()) {
            response.setParentConsumerNumber(upliners.get(0).getUplinerId());
        } else {
            response.setParentConsumerNumber("");
        }
        response.setAccountStatus("Active"); // Hardcoding Active for now. (future: can check actual status)
return response;
       
    }
    
    @Transactional
    public MemberProfileResponse updateMemberProfile(String memberId, @Valid UpdateProfileRequest request) {

        // Fetch existing user
    	Member user = userRepository.findByOfaMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with memberId: " + memberId));

        // Update allowed fields
        if (request.getFullName() != null ) {
           
            user.setOfaFullName(request.getFullName() );
        }

        if (request.getEmail() != null) {
            user.setOfaEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null) {
            user.setOfaMobileNo(request.getPhoneNumber());
        }

        if (request.getAddress() != null) {
            user.setOfaAddress(request.getAddress());
        }

        // Save changes
        userRepository.save(user);

        // Fetch updated profile
        MemberProfileResponse response = buildMemberProfile(user);

        return response;
    }

    // Helper method to build profile response
    private MemberProfileResponse buildMemberProfile(Member user) {

        MemberProfileResponse response = new MemberProfileResponse();

        if (user.getOfaFullName() != null) {
            
            response.setFullName(user.getOfaFullName());
            
        }

        response.setEmail(user.getOfaEmail());
        response.setPhoneNumber(user.getOfaMobileNo());
        response.setAddress(user.getOfaAddress());
        response.setConsumerNumber(user.getOfaMemberId());
        response.setAccountCreatedDate(user.getOfaCreatedDt() != null ? user.getOfaCreatedDt().toString() : "");

        // Find Parent/Upliner
        List<UplinerDetails> upliners = uplinerDetailsRepository.findByMemberId(user.getOfaMemberId());
        if (upliners != null && !upliners.isEmpty()) {
            response.setParentConsumerNumber(upliners.get(0).getUplinerId());
        } else {
            response.setParentConsumerNumber("");
        }

        response.setAccountStatus("Active");

        return response;
    }
    
    public Page<UnassignedMemberToReffererSystemDto> getUnassignedMembers(Pageable pageable) {
    	Page<Member> members = userRepository.findUnassignedMembers(pageable);
        
       return  members.map( member -> {
            List<String> roleNames = member.getRoles()
                                           .stream()
                                           .map(Role::getRoleName)
                                           .collect(Collectors.toList());
            
            return new UnassignedMemberToReffererSystemDto(
                member.getOfaFullName(),
                member.getOfaMobileNo(),
                member.getOfaEmail(),
                member.getOfaCreatedDt().toString(), // you can format it nicely if you want
                member.getOfaCreatedBy(),
                roleNames
            );
            
        });
        
    }
}
