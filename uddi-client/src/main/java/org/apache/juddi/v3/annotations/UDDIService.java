package org.apache.juddi.v3.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UDDIService {

	/** Name of the Service, this can be omitted if one is specified in a WebService annotation */
	public String serviceName() default "";
	/** Description of the Service */
    public String description();
	/** Unique key of this service */
	public String serviceKey();
	/** Unique key of the business to which this Service belongs. */
	public String businessKey() default "";
	/** Language code i.e.: en, fr, nl. */
	public String lang() default "en";
}
