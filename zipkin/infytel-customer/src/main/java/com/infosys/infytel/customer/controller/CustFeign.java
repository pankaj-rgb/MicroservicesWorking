package com.infosys.infytel.customer.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.infosys.infytel.customer.dto.PlanDTO;

@FeignClient("PlanMS")
public interface CustFeign {


	@RequestMapping(value = "/plans/{planId}")
	PlanDTO getSpecificPlan(@PathVariable ("planId") int planId);
}
