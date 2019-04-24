package com.amir.springsoapproducedemo;

import java.util.Properties;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;


@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

	//wsdl url will be "<ip:port>/<ServletRegistrationBean-urlmapping>/<DefaultWsdl11Definition-bean-name><.wsdl>"
	//example: localhost:8080/ws/services/students
	//please ,makesure setLocationUri  must contain wsdl-urlmapping as prefix(in this case-/ws/services/)
	//setLocationUri and soapaction we use client side to call webservice.
	//spring webservice xsd definition must be in request/resposne format else will not work
	//example- getCountryRequest and getCountryResponse.in this case  <operation-name>Request/Response
	//here operation name will be getCountry
	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/services/*");
	}

	@Bean(name = "students")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("StudentPort");
		wsdl11Definition.setLocationUri("/ws/services/mystudent");
		wsdl11Definition.setTargetNamespace("http://www.example.org/demo/");
		wsdl11Definition.setSchema(countriesSchema);
		Properties props = new Properties();
		props.put("getStudent", "http://www.example.org/demo/getStudentRequest");
		props.put("findCountry", "http://www.example.org/demo/findCountryRequest");
		wsdl11Definition.setSoapActions(props);
		
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("/com/xsd/demo.xsd"));
	}
}
