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

import org.apache.juddi.validation.ValidateValueSetValidation;
import org.uddi.api_v3.DispositionReport;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIValueSetValidationPortType;
import org.uddi.vs_v3.ValidateValues;

@WebService(serviceName="UDDIValueSetValidationService", 
			endpointInterface="org.uddi.v3_service.UDDIValueSetValidationPortType",
			targetNamespace = "urn:uddi-org:vs_v3_portType")
public class UDDIValueSetValidationImpl extends AuthenticatedService implements
		UDDIValueSetValidationPortType {
	public DispositionReport validateValues(ValidateValues body)
			throws DispositionReportFaultMessage {
		ValidateValueSetValidation.unsupportedAPICall();
		return null;
	}
}
