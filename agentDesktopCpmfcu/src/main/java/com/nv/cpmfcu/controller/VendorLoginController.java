package com.nv.cpmfcu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nv.cpmfcu.service.VendorLoginService;

@RestController
@RequestMapping("/cpmfcu/")
public class VendorLoginController {
	
	private VendorLoginService vendorLoginService;
	
	public VendorLoginController(VendorLoginService vendorLoginService) {
		this.vendorLoginService = vendorLoginService;
	}

	@GetMapping("/vendorLogin")
	public ResponseEntity<String> sendVendorLoginRequest(){
		
		String sendVendorLogin = vendorLoginService.getSessionData().getSessionId();
		
		return ResponseEntity.status(HttpStatus.OK).body(sendVendorLogin);
	}

}
