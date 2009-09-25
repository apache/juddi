package org.apache.juddi.v3.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UDDIServiceBinding {
	
	/** name which can be referenced by TModelInstanceRegistration, this key is not send
	 * down to the UDDI Registry. */
    public String serviceBindingKey() default "";
	/** Description of the ServiceBinding. */
    public String description();
    /** AccessPoint Type, which could be one of endPoint, wsdlDeployment, bindingTemplate, hostingDirector */
	public String accessPointType() default "wsdlDeployment";
	/** The URL of the accessPoint. */
	public String accessPoint();
	/** Language code i.e.: en, fr, nl. */
	public String lang() default "en";
}
