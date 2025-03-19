package com.newbusiness.one4all.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.service.MemberService;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/assign-roles/{memberId}")
    public ResponseEntity<?> assignRolesToMember(@PathVariable Long memberId, @RequestBody Set<String> roles) {
        Optional<Member> memberOpt = memberService.getMemberById(memberId);

        if (!memberOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found.");
        }

        Member member = memberOpt.get();
        memberService.assignRoles(member, roles);

        return ResponseEntity.ok("Roles assigned successfully.");
    }
}
