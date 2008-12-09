/*
 * Copyright 2001-2008 The Apache Software Foundation.
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

package org.apache.juddi.validation;

import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.model.UddiEntityPublisher;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public abstract class ValidateUDDIApi {

	protected UddiEntityPublisher publisher;
		
	public ValidateUDDIApi(UddiEntityPublisher publisher) {
		this.publisher = publisher;
	}

	public UddiEntityPublisher getPublisher() {
		return publisher;
	}

	public void setPublisher(UddiEntityPublisher publisher) {
		this.publisher = publisher;
	}
	
	public static void unsupportedAPICall() throws DispositionReportFaultMessage {
		throw new UnsupportedException(new ErrorMessage("errors.Unsupported"));
	}
}
