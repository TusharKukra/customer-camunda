package com.tk.customerOnboarding.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.task.CompleteTaskDto;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.tk.customerOnboarding.model.*;
import com.tk.customerOnboarding.ProcessConstants;


@RestController
@CrossOrigin
public class CustomerController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ProcessEngine processEngine;
	
	
	/* Validate Phone Number */
	@PostMapping("/validate/mobile-number")
	public ResponseEntity<HttpStatus> validateMobileNumber(@RequestBody Members members) {
		
		boolean mobileNumberVerified=true;
		boolean emailOTPVerified=true;
		boolean locationValid = true;
		boolean otpVerified=true;
		
//		if(members.getMemberPhone().substring(0, 3)=="971") {
//			mobileNumberVerified = true;
//		}
//		else {
//			mobileNumberVerified= false;
//		}
		
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		Map<String,Object> processVariable = new HashMap<>();
		processVariable.put("phoneNumber",members);
		processVariable.put("mobileNumberVerified", mobileNumberVerified);
		processVariable.put("emailOTPVerified", emailOTPVerified);
		processVariable.put("locationValid", locationValid);
		processVariable.put("otpVerified", otpVerified);
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(ProcessConstants.PROCESS_DEFINITION_KEY,processVariable);
		
		return ResponseEntity.ok(HttpStatus.OK);
	}
	
	
	/* Send/Receive OTP */
	@GetMapping("send-otp/{number}")
	public ResponseEntity<String> sendOtpRequest(@PathVariable String number){
		
		return new ResponseEntity<>("Message Sent to number: "+number, HttpStatus.OK);
		
	}
	
	
	/* Verify the OTP */
	@GetMapping("verify-otp/{otp}")
	public ResponseEntity<String> verifyOtpRequest(@PathVariable String otp){
		
		
		return new ResponseEntity<>("OTP Verified", HttpStatus.OK);
	
	}
	
	/* Register Email/Send/Resend OTP */
	@PostMapping("/register-email/")
	public ResponseEntity<HttpStatus> registerEmail(@RequestBody String email) {
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	/* Verify the Email OTP */
	@GetMapping("verify-email-otp/{emailOtp}")
	public ResponseEntity<String> verifyEmailOtp(@PathVariable String emailOtp){
		
		
		return new ResponseEntity<>("Verify Email OTP", HttpStatus.OK);
	
	}
	
	/* Setup the MPIN */
	@PostMapping("setup-mpin/")
	public ResponseEntity<String> setupMpin(@RequestBody String mpin){
		
		
		return new ResponseEntity<>("Setting up Mpin", HttpStatus.OK);
	
	}
	
	
	/* Validate the Location */
	@GetMapping("validate-location/{location}")
	public ResponseEntity<String> validateLocation(@PathVariable String location){
		
		
		return new ResponseEntity<>("Validate Location", HttpStatus.OK);
	
	}
	
	
	/* Capture FaceID */
	@PostMapping("capture-faceid/")
	public ResponseEntity<String> captureFaceId(@RequestBody String faceId){
		
		
		return new ResponseEntity<>("Capturing FaceID", HttpStatus.OK);
	
	}
	
	
	/* Get Task details */
	@GetMapping("/task-details")
	public TaskDetails getTaskDetails() {

		ResponseEntity<TaskDto[]> responseEntity = this.restTemplate.exchange(
				"http://localhost:8080/engine-rest/task", HttpMethod.GET, null,
				TaskDto[].class);
		if (!responseEntity.hasBody()) {
			throw new RuntimeException("Failed get Task");
		} else {
//			return responseEntity.getBody();
			List<TaskDto> taskList = Arrays.asList(responseEntity.getBody());
			taskList.get(0).getId();
			System.out.println(taskList.get(0).getId());
			
			return new TaskDetails(taskList.get(0).getAssignee(), taskList.get(0).getId(),
					processEngine.getFormService().getTaskFormData(taskList.get(0).getId()).getFormFields());
		}

	}
	
	
	/* Complete Task */
	@PostMapping("/task/{id}/complete")
	public String completeTask(@PathVariable String id, @RequestBody Map<String, Object> request) {
		Map<String, VariableValueDto> variableMap = new HashMap<>();
		request.forEach((key, value) -> {
			VariableValueDto variable = new VariableValueDto();
			variable.setValue(value);
			variableMap.put(key, variable);
		});
		CompleteTaskDto input = new CompleteTaskDto();
		input.setVariables(variableMap);
		HttpEntity<CompleteTaskDto> requestEntity = new HttpEntity<>(input);

		ResponseEntity<Void> responseEntity = this.restTemplate.exchange(
				"http://localhost:8080/engine-rest/task/" + id + "/complete", HttpMethod.POST, requestEntity,
				Void.class);
		if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
			throw new RuntimeException("failed.complete.task");
		} else {
			return "Task Completed";
		}
	}
		
	
	
	
	

}
