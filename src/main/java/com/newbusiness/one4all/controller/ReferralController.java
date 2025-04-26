package com.newbusiness.one4all.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbusiness.one4all.dto.DownlinerHierarchyDTO;
import com.newbusiness.one4all.dto.DownlinerWithMemberDetailsDTO;
import com.newbusiness.one4all.dto.UplinerWithMemberDetailsDTO;
import com.newbusiness.one4all.model.ReferrerDetails;
import com.newbusiness.one4all.security.RoleCheck;
import com.newbusiness.one4all.service.MemberService;
import com.newbusiness.one4all.service.ReferralService;
import com.newbusiness.one4all.util.ApiResponse;
import com.newbusiness.one4all.util.GlobalConstants;
import com.newbusiness.one4all.util.ResponseUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ReferralController {

	private static final Logger logger = LoggerFactory.getLogger(ReferralController.class);
	
	@Autowired
	private ReferralService referralService; 
	@RoleCheck({GlobalConstants.ROLE_ADMIN_RW})
	@PostMapping("/addreferer")
	public ResponseEntity<?> addReferer(@Valid @RequestBody ReferrerDetails  referrerDetails, BindingResult result) {
	    logger.info("Add referer request received: {}", referrerDetails);
	    if (result.hasErrors()) {
	        List<Map<String, Object>> errorMessages = result.getAllErrors().stream().map(error -> {
	            Map<String, Object> errorMap = new HashMap<>();
	            errorMap.put("field", ((FieldError) error).getField());
	            errorMap.put("message", error.getDefaultMessage());
	            errorMap.put("rejectedValue", ((FieldError) error).getRejectedValue());
	            return errorMap;
	        }).collect(Collectors.toList());
	        ApiResponse apiResponse = ResponseUtils.buildValidationErrorResponse(errorMessages);
	        return ResponseEntity.badRequest().body(apiResponse);
	    }

	    try {
	        // Check if the referrer details already exist based on ofaConsumerNo
	        Optional<ReferrerDetails> existingreferrral = referralService.findByMemberId(referrerDetails.getMemberId());
	        
	        if (existingreferrral.isPresent()) {
	            // Return a message indicating the record already exists
	        	ApiResponse apiResponse = ResponseUtils.buildApiResponse(
		                Collections.singletonList(Map.of("status", GlobalConstants.DUPLICATE_REFFERAR_RECORD_FOUND + referrerDetails.getMemberId(), "errorCode",
		                        HttpStatus.CONFLICT, "message", Collections.singletonList(existingreferrral))));
	        	return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
	        } else {
	        	 ReferrerDetails details= referralService.addReferer(
	                    referrerDetails.getMemberId(), 
	                    referrerDetails.getReferrerId(), 
	                    referrerDetails.getReferralLevel()
	                );
	        	 ApiResponse apiResponse = ResponseUtils.buildApiResponse(
	 	                Collections.singletonList(Map.of("status", GlobalConstants.REFFERAR_CREATION_SUCCESS, "errorCode",
	 	                        HttpStatus.CREATED, "message", Collections.singletonList(details))));
	        	 return ResponseEntity.ok(apiResponse);    
	        
	        } 
	    } catch (Exception e) {
	        logger.error("Error while adding referer", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while adding referer");
	    }
	}
	
	 // Endpoint to get downliners
	@RoleCheck({GlobalConstants.ROLE_ADMIN_RW,GlobalConstants.ROLE_USER_RO})
    @GetMapping("/{memberId}/downliners")
    public ResponseEntity<?> getDownliners(@PathVariable String memberId) {
        List<DownlinerWithMemberDetailsDTO> downliners = referralService.getDownliners(memberId);
        return ResponseEntity.ok(downliners);
    }

    // Endpoint to get upliners
	@RoleCheck({GlobalConstants.ROLE_ADMIN_RW,GlobalConstants.ROLE_USER_RO})
    @GetMapping("/{memberId}/upliners")
    public ResponseEntity<?> getUpliners(@PathVariable String memberId) {
        List<UplinerWithMemberDetailsDTO> upliners = referralService.getUpliners(memberId);
        return ResponseEntity.ok(upliners);
    }
	@RoleCheck({GlobalConstants.ROLE_ADMIN_RW})
    @GetMapping("/{memberId}/downlinerHierarchy")
    public ResponseEntity<?> getDownlinerHierarchy(@PathVariable String memberId) {
        DownlinerHierarchyDTO hierarchy = referralService.getDownlinerHierarchy(memberId);
        return ResponseEntity.ok(hierarchy);
    }

}
