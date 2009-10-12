package org.apache.juddi.samples;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface HelloWorld {
	
	String sayHi(@WebParam(name="text") String text);

}
