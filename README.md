How to make spring soap web-service(JAX-WS)
-------------------------------------------

step-1: 
--------
create xml schema please make sure Request/Response element must be named in following format.<br>
{operation-name}Request  and {operation-name}Response <br>
Example: getCountryRequest and getCountryResponse  ...findCountryRequest and findCountryResponse.

<element name="findCountryRequest">
    <complexType>
   		 <sequence>
    		<element name="name" type="string"/>
    	</sequence>
    </complexType>
    </element>
    
 <element name="findCountryResponse">
    <complexType>
    	<sequence>
   			 <element name="country" type="tns:Country"/>
    	</sequence>
    </complexType>
    </element>
   
you can use eclipse design view to create xsd file.

Step-2
-------
configured maven-plugin to generate jaxb classes using maven.

			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>jaxb2-maven-plugin</artifactId>
				<version>1.6</version>
				<executions>
					<execution>
						<id>xjc</id>
						<goals>
							<goal>xjc</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<schemaDirectory>${project.basedir}/src/main/resources/com/xsd</schemaDirectory>
					<outputDirectory>${project.basedir}/src/main/java</outputDirectory>
					<clearOutputDir>false</clearOutputDir>
				</configuration>
			</plugin>


step-3
------
create configuration class in spring boot application and added following 3 beans into this class.

1-ServletRegistrationBean -- This is used to provide url mapping for your soap webservice<br>
2-DefaultWsdl11Definition --This is used to generated runtime WSDL with help of bean called XsdSchema <br>
3-XsdSchema --This bean is an input for DefaultWsdl11Definition<br>

Example:
--------
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
		//way to add soapAction uri
		props.put("getStudent", "http://www.example.org/demo/getStudentRequest");
		props.put("findCountry", "http://www.example.org/demo/findCountryRequest");
		wsdl11Definition.setSoapActions(props);
		
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("/com/xsd/demo.xsd"));
	}

notes:
-----
	wsdl url will be "<ip:port>/<ServletRegistrationBean-urlmapping>/<DefaultWsdl11Definition-bean-name><.wsdl>"
	example: localhost:8080/ws/services/students.wsdl
	please ,makesure setLocationUri  must contain wsdl-urlmapping as prefix(in this case-/ws/services/)
	setLocationUri and soapaction we use client side to call webservice.
	spring webservice xsd definition must be in request/resposne format else will not work
	example- getCountryRequest and getCountryResponse.in this case  <operation-name>Request/Response
	here operation name will be getCountry
	
	
step-4
------
create endpoint class to make responsive service.<br>

class must be annotated with @Endpoint and method must be annotated with <br>
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getStudentRequest") <br>
@ResponsePayload <br>
and method-argument(request) must be annotated with @RequestPayload <br>

NAMESPACE_URI is a target namespace value which is used in xsd file.<br>
localPart is a request element name of xsd file.<br>
soapAction={namespace}/{localpart}


Example:
------

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
	}