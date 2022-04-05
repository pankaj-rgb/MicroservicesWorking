package com.infosys.infytel.customer.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.infosys.infytel.customer.dto.CustomerDTO;
import com.infosys.infytel.customer.dto.LoginDTO;
import com.infosys.infytel.customer.dto.PlanDTO;
import com.infosys.infytel.customer.service.CustHystrixService;
import com.infosys.infytel.customer.service.CustomerService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@CrossOrigin
public class CustomerController {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CustomerService custService;
	
//	@Autowired
//	RestTemplate template;
	
	@Autowired
	CustHystrixService hystService;
	
	// Create a new customer
	@PostMapping(value = "/customers",  consumes = MediaType.APPLICATION_JSON_VALUE)
	public void createCustomer(@RequestBody CustomerDTO custDTO) {
		logger.info("Creation request for customer {}", custDTO);
		custService.createCustomer(custDTO);
	}

	// Login
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean login(@RequestBody LoginDTO loginDTO) {
		logger.info("Login request for customer {} with password {}", loginDTO.getPhoneNo(),loginDTO.getPassword());
		return custService.login(loginDTO);
	}

	// Fetches full profile of a specific customer
	@HystrixCommand(fallbackMethod = "getCustomerProfileFallback")
	@GetMapping(value = "/customers/{phoneNo}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo) {
		System.out.println("===================in profile=========");
		long overallstart=System.currentTimeMillis();
		logger.info("Profile request for customer {}", phoneNo);
		
		
		//the below one are using with the ribbon 
		
		CustomerDTO custDTO=custService.getCustomerProfile(phoneNo);
		long planstart=System.currentTimeMillis();
		//PlanDTO planDTO=template.getForObject("http://"+"PLANMS/plans/"+custDTO.getCurrentPlan().getPlanId(), PlanDTO.class);
		PlanDTO planDTO=hystService.getSpecificPlans(custDTO.getCurrentPlan().getPlanId());
		
		long planstop=System.currentTimeMillis();
		custDTO.setCurrentPlan(planDTO);
		
		@SuppressWarnings("unchecked")
		//List<Long> friends=template.getForObject("http://FRIENDFAMILYMS"+"/customers/"+phoneNo+"/friends", List.class);
		//above one is used with the hystrix class
		
		long friendstart=System.currentTimeMillis();
		List<Long> friends=hystService.getFriends(phoneNo);
		long friendStop=System.currentTimeMillis();
		custDTO.setFriendAndFamily(friends);
		
		long overAllStop=System.currentTimeMillis();
		
		//below add for the timing part
		System.out.println("total time for plan "+(planstop-planstart));
		System.out.println("total time for friend "+ (friendStop-friendstart));
		System.out.println("total overall time "+(overAllStop-overallstart));
		
		
		return custDTO;
	}

	public CustomerDTO getCustomerProfileFallback(@PathVariable Long phoneNo) {
		System.out.println("===================in fallback=========");
		return new CustomerDTO();
	}
}
