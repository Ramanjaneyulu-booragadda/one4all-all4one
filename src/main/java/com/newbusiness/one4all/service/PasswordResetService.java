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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private UserRepository memberRepo;

    public String createResetToken(String email) {
        log.info("Creating password reset token for email={}", email);
        Optional<Member> userOpt = memberRepo.findByOfaEmail(email);
        if (userOpt.isPresent()) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setEmail(email);
            resetToken.setToken(token);
            resetToken.setExpiryTime(Instant.now().plus(15, ChronoUnit.MINUTES));
            tokenRepo.save(resetToken);
            log.info("Password reset token created for email={}", email);
            return token;
        }
        log.warn("No user found for email={} when creating reset token", email);
        return null;
    }

    public boolean resetPassword(String token, String newPassword) {
        log.info("Resetting password using token={}", token);
        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByToken(token);
        if (tokenOpt.isEmpty()) {
            log.warn("Password reset failed. Invalid token: {}", token);
            return false;
        }

        PasswordResetToken reset = tokenOpt.get();
        if (reset.isUsed() || reset.getExpiryTime().isBefore(Instant.now())) {
            log.warn("Attempt to use expired or already used token={}", token);
            return false;
        }

        Optional<Member> memberOpt = memberRepo.findByOfaEmail(reset.getEmail());
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setOfaPassword(new BCryptPasswordEncoder().encode(newPassword));
            reset.setUsed(true);
            memberRepo.save(member);
            tokenRepo.save(reset);
            log.info("Password reset successful for email={}", reset.getEmail());
            return true;
        }
        log.warn("Password reset failed. Member not found for email: {}", reset.getEmail());
        return false;
    }

    // Get email by reset token
    public Optional<String> getEmailByToken(String token) {
        return tokenRepo.findByToken(token).map(PasswordResetToken::getEmail);
    }
}
