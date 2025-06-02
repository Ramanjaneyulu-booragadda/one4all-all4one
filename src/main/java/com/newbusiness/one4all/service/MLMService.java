package com.newbusiness.one4all.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@Transactional
public class MLMService {

	@Autowired
	private Environment environment;


	/**
	 * Retrieve the referral bonus amount from properties.
	 */
	public BigDecimal getReferralBonusFromProperties() {
		BigDecimal bonus = new BigDecimal(environment.getProperty("mlm.bonus.direct.referral", "1000"));
		log.info("Fetched referral bonus from properties: {}", bonus);
		return bonus;
	}

	/**
	 * Retrieve the payout scheme for upliners dynamically from properties.
	 */
	public Map<Integer, BigDecimal> getPayoutSchemeFromProperties() {
		Map<Integer, BigDecimal> payoutScheme = new HashMap<>();
		for (int level = 1; level <= 10; level++) {
			String propertyKey = "mlm.payout.L" + level;
			String defaultValue = "1000";
			BigDecimal payout = new BigDecimal(environment.getProperty(propertyKey, defaultValue));
			payoutScheme.put(level, payout);
		}
		log.info("Fetched payout scheme from properties: {}", payoutScheme);
		return payoutScheme;
	}



	private Map<Integer, BigDecimal> getPhaseBasedPayoutScheme() {
		Map<Integer, BigDecimal> phaseScheme = new LinkedHashMap<>();

		phaseScheme.put(1, new BigDecimal(0)); // L1 -> 2000
		phaseScheme.put(2, new BigDecimal(0)); // L2 -> 1000
		phaseScheme.put(3, new BigDecimal(4000)); // L3 -> 1000
		phaseScheme.put(4, new BigDecimal(6500)); // L4 -> 2500
		phaseScheme.put(5, new BigDecimal(11500)); // L5 -> 5000
		phaseScheme.put(6, new BigDecimal(21500)); // L6 -> 10000
		phaseScheme.put(7, new BigDecimal(41500)); // L7 -> 20000
		phaseScheme.put(8, new BigDecimal(71500)); // L8 -> 30000
		phaseScheme.put(9, new BigDecimal(111500)); // L9 -> 40000
		phaseScheme.put(10, new BigDecimal(161500)); // L10 -> 50000
		return phaseScheme;
	}

	public BigDecimal getPayoutForPhase(int level) {
		switch (level) {
		case 1:
			return new BigDecimal(2000);
		case 2:
			return new BigDecimal(1000);
		case 3:
			return new BigDecimal(1000);
		case 4:
			return new BigDecimal(2500);
		case 5:
			return new BigDecimal(5000);
		case 6:
			return new BigDecimal(10000);
		case 7:
			return new BigDecimal(20000);
		case 8:
			return new BigDecimal(30000);
		case 9:
			return new BigDecimal(40000);
		case 10:
			return new BigDecimal(50000);
		default:
			return BigDecimal.ZERO;
		}
	}

	
}
