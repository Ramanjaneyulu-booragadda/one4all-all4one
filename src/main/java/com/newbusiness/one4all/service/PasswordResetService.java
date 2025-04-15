package com.newbusiness.one4all.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.newbusiness.one4all.model.Member;
import com.newbusiness.one4all.model.PasswordResetToken;
import com.newbusiness.one4all.repository.PasswordResetTokenRepository;
import com.newbusiness.one4all.repository.UserRepository;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private UserRepository memberRepo;

    public String createResetToken(String email) {
        Optional<Member> userOpt = memberRepo.findByOfaEmail(email);
        if (userOpt.isPresent()) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setEmail(email);
            resetToken.setToken(token);
            resetToken.setExpiryTime(Instant.now().plus(15, ChronoUnit.MINUTES));
            tokenRepo.save(resetToken);
            return token;
        }
        return null;
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByToken(token);
        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken reset = tokenOpt.get();
        if (reset.isUsed() || reset.getExpiryTime().isBefore(Instant.now())) return false;

        Optional<Member> memberOpt = memberRepo.findByOfaEmail(reset.getEmail());
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setOfaPassword(new BCryptPasswordEncoder().encode(newPassword));
            reset.setUsed(true);
            memberRepo.save(member);
            tokenRepo.save(reset);
            return true;
        }
        return false;
    }
}
