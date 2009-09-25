package org.apache.juddi.v3.annotations;

import java.util.ArrayList;
import java.util.Collection;

import javax.jws.WebService;

//import org.apache.log4j.Logger;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Name;

public class AnnotationProcessor {
	
	//private Logger log = Logger.getLogger(AnnotationProcessor.class);
	
	public Collection<BusinessService> readServiceAnnotations(String[] classesWithAnnotations) {
		Collection<BusinessService> services = new ArrayList<BusinessService>();
		try {
			for (String className : classesWithAnnotations) {
				Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
				UDDIService uddiService= (UDDIService) clazz.getAnnotation(UDDIService.class);
				if (uddiService!=null) {
					BusinessService service = new BusinessService();
					WebService webServiceAnnotation = (WebService) clazz.getAnnotation(WebService.class);
					if (webServiceAnnotation!=null) {
						Name name = new Name();
						name.setLang("en"); //default to english
			            name.setValue(webServiceAnnotation.name());
						service.getName().add(name);
						
						
						String kurt1= webServiceAnnotation.wsdlLocation();
						System.out.println(kurt1);
						String kurt2=webServiceAnnotation.endpointInterface();
						System.out.println(kurt2);
						String kurt3=webServiceAnnotation.portName();
						System.out.println(kurt3);
						System.out.println(webServiceAnnotation.serviceName());
						String description = uddiService.description();
						System.out.println(description);
					}
					services.add(service);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return services;
	}
}
