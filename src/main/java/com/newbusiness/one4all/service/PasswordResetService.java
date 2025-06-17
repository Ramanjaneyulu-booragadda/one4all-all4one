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

    @Autowired
    private MemberService memberService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MemberService userService;

    /**
     * Creates a password reset token for either email or mobile (only one should be provided).
     * @param email The user's email (nullable)
     * @param mobile The user's mobile (nullable)
     * @return The generated token, or null if user not found or input invalid
     */
    public String createResetToken(String email, String mobile) {
        log.info("Creating password reset token for email={} mobile={}", email, mobile);
        if ((email == null || email.isBlank()) && (mobile == null || mobile.isBlank())) {
            log.warn("No email or mobile provided for reset token creation");
            return null;
        }
        if (email != null && !email.isBlank() && mobile != null && !mobile.isBlank()) {
            log.warn("Both email and mobile provided for reset token creation; only one allowed");
            return null;
        }
        Optional<Member> userOpt = Optional.empty();
        if (email != null && !email.isBlank()) {
            userOpt = userService.findByOfaEmail(email);
        } else if (mobile != null && !mobile.isBlank()) {
            userOpt = userService.findByOfaMobileNo(mobile);
        }
        if (userOpt.isPresent()) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            if (email != null && !email.isBlank()) {
                resetToken.setEmail(email);
                resetToken.setMobile(null);
            } else {
                resetToken.setEmail(null);
                resetToken.setMobile(mobile);
            }
            resetToken.setToken(token);
            resetToken.setExpiryTime(Instant.now().plus(15, ChronoUnit.MINUTES));
            tokenRepo.save(resetToken);
            log.info("Password reset token created for {}={}", email != null ? "email" : "mobile", email != null ? email : mobile);
            return token;
        }
        log.warn("No user found for email={} or mobile={} when creating reset token", email, mobile);
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

        Optional<Member> memberOpt;
        if (reset.getEmail() != null && !reset.getEmail().isBlank()) {
            memberOpt = memberRepo.findByOfaEmail(reset.getEmail());
        } else if (reset.getMobile() != null && !reset.getMobile().isBlank()) {
            memberOpt = memberRepo.findByOfaMobileNo(reset.getMobile());
        } else {
            log.warn("Password reset token does not have a valid email or mobile");
            return false;
        }
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setOfaPassword(new BCryptPasswordEncoder().encode(newPassword));
            reset.setUsed(true);
            memberRepo.save(member);
            tokenRepo.save(reset);
            log.info("Password reset successful for {}={}", reset.getEmail() != null ? "email" : "mobile", reset.getEmail() != null ? reset.getEmail() : reset.getMobile());
            return true;
        }
        log.warn("Password reset failed. Member not found for email: {} or mobile: {}", reset.getEmail(), reset.getMobile());
        return false;
    }

    // Get email by reset token
    public Optional<String> getEmailByToken(String token) {
        return tokenRepo.findByToken(token).map(PasswordResetToken::getEmail);
    }

    /**
     * Request a password reset by either email or mobile (not both).
     * 
     * @param email            The user's email (nullable)
     * @param mobile           The user's mobile (nullable)
     * @param resetLinkBaseUrl The base URL for the reset link
     * @return A message describing the result
     */
    public String requestPasswordReset(String email, String mobile, String resetLinkBaseUrl) {
        if ((email == null || email.isBlank()) && (mobile == null || mobile.isBlank())) {
            return "Either email or mobile must be provided.";
        }
        if (email != null && !email.isBlank() && mobile != null && !mobile.isBlank()) {
            return "Provide only one of email or mobile, not both.";
        }
        if (email != null && !email.isBlank()) {
            Optional<Member> memberOpt = memberService.findByOfaEmail(email);
            if (memberOpt.isEmpty()) {
                return "No user found with the provided email.";
            }
            String token = createResetToken(email, null);
            if (token == null) {
                return "Failed to create reset token.";
            }
            String resetLink = resetLinkBaseUrl + token;
            emailService.sendPasswordResetEmail(email, resetLink);
            return "Password reset link sent to your registered email.";
        } else {
            Optional<Member> memberOpt = memberService.findByOfaMobileNo(mobile);
            if (memberOpt.isEmpty()) {
                return "No user found with the provided mobile number.";
            }
            String token = createResetToken(null, mobile);
            if (token == null) {
                return "Failed to create reset token.";
            }
            String resetLink = resetLinkBaseUrl + token;
            emailService.sendPasswordResetSms(mobile, resetLink);
            return "Password reset link sent to your registered mobile number.";
        }
    }

    public static class PasswordResetResult {
        private final boolean success;
        private final String message;
        public PasswordResetResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    /**
     * Handles password reset request by email, mobile, or memberId (not more than one). All business logic is here.
     * Enhanced: Validates association between memberId and email/mobile, and provides meaningful error messages.
     */
    public PasswordResetResult handlePasswordResetRequest(String email, String mobile, String memberId, String resetLinkBaseUrl) {
        int provided = 0;
        if (email != null && !email.isBlank()) provided++;
        if (mobile != null && !mobile.isBlank()) provided++;
        if (memberId != null && !memberId.isBlank()) provided++;
        if (provided == 0) {
            return new PasswordResetResult(false, "Provide either email, mobile, or memberId.");
        }
        if (provided > 1) {
            // Combination logic: validate association
            if (memberId != null && !memberId.isBlank()) {
                if (email != null && !email.isBlank()) {
                    // Check if email is associated with memberId
                    if (userService.findByEmailAndMemberId(email, memberId).isEmpty()) {
                        boolean emailExists = !userService.findByOfaEmail(email).isEmpty();
                        boolean memberExists = !userService.getMemberByMemberId(memberId).isEmpty();
                        if (!memberExists) {
                            return new PasswordResetResult(false, "The Member ID provided does not exist in our records.");
                        } else if (!emailExists) {
                            return new PasswordResetResult(false, "The email ID provided does not exist in our records.");
                        } else {
                            return new PasswordResetResult(false, "The email ID provided is not associated with the Member ID you have provided. Please recheck and submit details properly.");
                        }
                    }
                }
                if (mobile != null && !mobile.isBlank()) {
                    // Check if mobile is associated with memberId
                    if (userService.findByMobileAndMemberId(mobile, memberId).isEmpty()) {
                        boolean mobileExists = !userService.findByOfaMobileNo(mobile).isEmpty();
                        boolean memberExists = !userService.getMemberByMemberId(memberId).isEmpty();
                        if (!memberExists) {
                            return new PasswordResetResult(false, "The Member ID provided does not exist in our records.");
                        } else if (!mobileExists) {
                            return new PasswordResetResult(false, "The mobile number provided does not exist in our records.");
                        } else {
                            return new PasswordResetResult(false, "The mobile number provided is not associated with the Member ID you have provided. Please recheck and submit details properly.");
                        }
                    }
                }
            }
            // If both email and mobile are provided but no memberId, error
            if (memberId == null || memberId.isBlank()) {
                return new PasswordResetResult(false, "Provide only one of email, mobile, or memberId, not multiple.");
            }
        }
        // Single input or valid association
        Optional<Member> memberOpt = Optional.empty();
        if (memberId != null && !memberId.isBlank()) {
            // If memberId + email
            if (email != null && !email.isBlank()) {
                // Check if email is associated with memberId (DB lookup)
                memberOpt = userService.findByEmailAndMemberId(email, memberId);
                if (memberOpt.isEmpty()) {
                    boolean emailExists = !userService.findByOfaEmail(email).isEmpty();
                    boolean memberExists = !userService.getMemberByMemberId(memberId).isEmpty();
                    if (!memberExists) {
                        return new PasswordResetResult(false, "The Member ID provided does not exist in our records.");
                    } else if (!emailExists) {
                        return new PasswordResetResult(false, "The email ID provided does not exist in our records.");
                    } else {
                        return new PasswordResetResult(false, "The email ID provided is not associated with the Member ID you have provided. Please recheck and submit details properly.");
                    }
                }
            } else if (mobile != null && !mobile.isBlank()) {
                // Check if mobile is associated with memberId (DB lookup)
                memberOpt = userService.findByMobileAndMemberId(mobile, memberId);
                if (memberOpt.isEmpty()) {
                    boolean mobileExists = !userService.findByOfaMobileNo(mobile).isEmpty();
                    boolean memberExists = !userService.getMemberByMemberId(memberId).isEmpty();
                    if (!memberExists) {
                        return new PasswordResetResult(false, "The Member ID provided does not exist in our records.");
                    } else if (!mobileExists) {
                        return new PasswordResetResult(false, "The mobile number provided does not exist in our records.");
                    } else {
                        return new PasswordResetResult(false, "The mobile number provided is not associated with the Member ID you have provided. Please recheck and submit details properly.");
                    }
                }
            } else {
                memberOpt = userService.getMemberByMemberId(memberId);
            }
        } else if (email != null && !email.isBlank()) {
            // Check if email exists (DB lookup)
            memberOpt = userService.findByOfaEmail(email);
            if (memberOpt.isEmpty()) {
                return new PasswordResetResult(false, "The email ID provided does not exist in our records.");
            }
        } else if (mobile != null && !mobile.isBlank()) {
            // Check if mobile exists (DB lookup)
            memberOpt = userService.findByOfaMobileNo(mobile);
            if (memberOpt.isEmpty()) {
                return new PasswordResetResult(false, "The mobile number provided does not exist in our records.");
            }
        }
        if (memberOpt.isEmpty()) {
            return new PasswordResetResult(false, "No user found with the provided details.");
        }
        Member member = memberOpt.get();
        String token;
        if (email != null && !email.isBlank()) {
            token = createResetToken(email, null);
        } else if (mobile != null && !mobile.isBlank()) {
            token = createResetToken(null, mobile);
        } else {
            // fallback: try to use email if present, else mobile
            if (member.getOfaEmail() != null && !member.getOfaEmail().isBlank()) {
                token = createResetToken(member.getOfaEmail(), null);
            } else if (member.getOfaMobileNo() != null && !member.getOfaMobileNo().isBlank()) {
                token = createResetToken(null, member.getOfaMobileNo());
            } else {
                return new PasswordResetResult(false, "No contact information found for this member.");
            }
        }
        if (token == null) {
            return new PasswordResetResult(false, "Failed to create reset token.");
        }
        String resetLink = resetLinkBaseUrl + token;
        if (email != null && !email.isBlank()) {
            emailService.sendPasswordResetEmail(member.getOfaEmail(), resetLink);
            return new PasswordResetResult(true, "Password reset link sent to your registered email.");
        } else if (mobile != null && !mobile.isBlank()) {
            emailService.sendPasswordResetSms(member.getOfaMobileNo(), resetLink);
            return new PasswordResetResult(true, "Password reset link sent to your registered mobile number.");
        } else {
            boolean sent = false;
            if (member.getOfaEmail() != null && !member.getOfaEmail().isBlank()) {
                emailService.sendPasswordResetEmail(member.getOfaEmail(), resetLink);
                sent = true;
            }
            if (member.getOfaMobileNo() != null && !member.getOfaMobileNo().isBlank()) {
                emailService.sendPasswordResetSms(member.getOfaMobileNo(), resetLink);
                sent = true;
            }
            if (sent) {
                return new PasswordResetResult(true, "Password reset link sent to your registered contact(s).");
            } else {
                return new PasswordResetResult(false, "No contact information found for this member.");
            }
        }
    }

    /**
     * Get the PasswordResetToken object by token string.
     */
    public Optional<PasswordResetToken> getTokenObject(String token) {
        return tokenRepo.findByToken(token);
    }

    public PasswordResetResult resetPasswordWithResult(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepo.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return new PasswordResetResult(false, "Invalid or expired token");
        }
        PasswordResetToken reset = tokenOpt.get();
        if (reset.isUsed() || reset.getExpiryTime().isBefore(Instant.now())) {
            return new PasswordResetResult(false, "The password reset link has expired or has already been used. Please request a new reset link.");
        }
        Optional<Member> memberOpt;
        if (reset.getEmail() != null && !reset.getEmail().isBlank()) {
            memberOpt = memberRepo.findByOfaEmail(reset.getEmail());
        } else if (reset.getMobile() != null && !reset.getMobile().isBlank()) {
            memberOpt = memberRepo.findByOfaMobileNo(reset.getMobile());
        } else {
            return new PasswordResetResult(false, "Password reset token does not have a valid email or mobile");
        }
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setOfaPassword(new BCryptPasswordEncoder().encode(newPassword));
            reset.setUsed(true);
            memberRepo.save(member);
            tokenRepo.save(reset);
            return new PasswordResetResult(true, "Password reset successful.");
        }
        return new PasswordResetResult(false, "Password reset failed. Member not found for email or mobile.");
    }
}
