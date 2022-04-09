package com.infosys.infytel.customer.service;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infosys.infytel.customer.controller.CustFeign;
import com.infosys.infytel.customer.dto.PlanDTO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;

@Service
public class CustHystrixService {

	@Autowired
	RestTemplate template;
	
	@Autowired
	CustFeign planFeign;
	
	@HystrixCommand
	public Future<PlanDTO> getSpecificPlans(final int planId) {
		return new AsyncResult<PlanDTO>() {
			@Override
			public PlanDTO invoke() {
				System.out.println("plan id get form the values "+planId);
				//return template.getForObject("http://PLANMS"+"/plans/"+planId, PlanDTO.class);
				return planFeign.getSpecificPlan(planId);
			}
		};
		
	}
	
	@HystrixCommand
	public Future<List<Long>> getFriends(final long phoneNo)
	{
		return new AsyncResult<List<Long>>() {
		@SuppressWarnings("unchecked")
		@Override
		public List<Long> invoke(){
			return template.getForObject("http://FRIENDFAMILYMS"+"/customers/"+phoneNo+"/friends", List.class);
		}
		};
		
	}
}
