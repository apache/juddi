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

package org.apache.juddi.api.impl;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.api.util.ValueSetCachingQuery;
import org.apache.juddi.api.util.ValueSetValidationQuery;
import org.apache.juddi.validation.ValidateValueSetValidation;
import org.uddi.api_v3.DispositionReport;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIValueSetValidationPortType;
import org.uddi.vs_v3.ValidateValues;

//@WebService(serviceName="UDDIValueSetValidationService", 
//			endpointInterface="org.uddi.v3_service.UDDIValueSetValidationPortType",
//			targetNamespace = "urn:uddi-org:v3_service")
public class UDDIValueSetValidationImpl extends AuthenticatedService implements
		UDDIValueSetValidationPortType {
        private static Log logger = LogFactory.getLog(UDDIValueSetValidationImpl.class);

        private UDDIServiceCounter serviceCounter;

        public UDDIValueSetValidationImpl() {
            super();
            serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(this.getClass());
        }
    
	public DispositionReport validateValues(ValidateValues body)
			throws DispositionReportFaultMessage {
	        long startTime = System.nanoTime();
                long procTime = System.nanoTime() - startTime;
                serviceCounter.update(ValueSetValidationQuery.VALIDATE_VALUES, 
                        QueryStatus.SUCCESS, procTime);

		ValidateValueSetValidation.unsupportedAPICall();
		return null;
	}
}
