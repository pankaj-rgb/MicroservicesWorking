package com.infosys.infytel.customer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infosys.infytel.customer.dto.PlanDTO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class CustHystrixService {

	@Autowired
	RestTemplate template;
	
	@HystrixCommand
	public PlanDTO getSpecificPlans(int planId)
	{
		return template.getForObject("http://"+"PLANMS/plans/"+planId, PlanDTO.class);
	}
	
	@SuppressWarnings("unchecked")
	@HystrixCommand
	public List<Long> getFriends(long phoneNo)
	{
		return template.getForObject("http://FRIENDFAMILYMS"+"/customers/"+phoneNo+"/friends", List.class);
	}
}
