package org.apache.juddi.v3.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UDDITModelInstance {

	/** The tModelInstance Key */
	public String tModelInstanceKey();
	/** Reference to ServiceBinding Key to which this tModelInstance belongs. */
	public String serviceBindingKey();
	/** Description of the tModelInstanceInfo */
    public String description() default "";
    /** tModelKey reference to a specification with which the Service represented by the containing bindingTemplate complies */
	public String tModelKey();
	/** Language code i.e.: en, fr, nl. */
	public String lang() default "en";
	/** The instanceParms is an optional element of type string, used to locally contain settings or 
        parameters related to the proper use of a tModelInstanceInfo. The suggested format is a 
        namespace-qualified XML document so that the settings or parameters can be found in the 
        XML documents elements and attributes. */
	public String instanceParams() default "";
}
