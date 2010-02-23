/*
 * Copyright 2001-2010 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
	/** List of KeyedReferences */
	public String categoryBag() default "";
}
