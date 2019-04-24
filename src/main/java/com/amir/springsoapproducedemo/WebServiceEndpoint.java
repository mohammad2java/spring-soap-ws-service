package com.amir.springsoapproducedemo;

import org.example.demo.Country;
import org.example.demo.FindCountryRequest;
import org.example.demo.FindCountryResponse;
import org.example.demo.GetStudentRequest;
import org.example.demo.GetStudentResponse;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class WebServiceEndpoint {
	private static final String NAMESPACE_URI = "http://www.example.org/demo/";
	
	//localPart must be xsd request element name.
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getStudentRequest")
	@ResponsePayload
	public GetStudentResponse findStudent(@RequestPayload GetStudentRequest request,MessageContext messageContext) {
		GetStudentResponse ret = new GetStudentResponse();
		ret.setId(101);
		ret.setName("Amir");
		ret.setAge(32);
		return ret;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "findCountryRequest")
	@ResponsePayload
	public FindCountryResponse getCountry(@RequestPayload FindCountryRequest findCountryRequest) {
		FindCountryResponse ret =  new FindCountryResponse();
		Country value =  new Country();
		value.setCode("101");
		value.setName("India");
		value.setCurrency("INR");
		ret.setCountry(value);
		
		return ret;
		
	}
	
}
