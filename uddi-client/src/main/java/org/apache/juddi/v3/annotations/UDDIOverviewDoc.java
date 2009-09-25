package org.apache.juddi.v3.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * The overviewDoc is a mandatory repeating element, used to house references to remote 
 * descriptive information or instructions related to the use of a particular tModel and its 
 * instanceParms. Multiple overviewDoc elements are useful, for example, to handle alternative 
 * representations of the documentation. 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UDDIOverviewDoc {

	/** The tModelInstance Key */
	public String tModelInstanceKey();
	/** The description is a mandatory repeating element. Each description, optionally qualified by 
      * an lang attribute, holds a short descriptive overview of how a particular tModel is to be  used. */
	public String description();
	/** Language code i.e.: en, fr, nl. */
	public String lang() default "en";
	/** The optional overviewURL is to be used to hold a URL that refers to a long form of an 
	  * overview document that covers the way a particular tModel is used as a component of an 
	  * overall Web service description. */
	public String overviewURL();
	
}
