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

import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.uddi.subr_v3.NotifySubscriptionListener;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:tcunning@apach.org">Tom Cunningham</a>
 */
public class ValidateSubscriptionListener extends ValidateUDDIApi {

	public ValidateSubscriptionListener() {
		super(null);
	}	
		
	public void validateNotification(NotifySubscriptionListener body) throws
		FatalErrorException {
		if (body == null) {
			throw new FatalErrorException(new ErrorMessage("Subscription Listener Body was null"));
		}
	}
}
