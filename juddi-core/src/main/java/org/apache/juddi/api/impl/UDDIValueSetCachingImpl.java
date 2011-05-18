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

import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.api.util.SubscriptionQuery;
import org.apache.juddi.api.util.ValueSetCachingQuery;
import org.apache.juddi.validation.ValidateValueSetCaching;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIValueSetCachingPortType;
import org.uddi.vscache_v3.ValidValue;

//@WebService(serviceName="UDDIValueSetCachingService", 
//			endpointInterface="org.uddi.v3_service.UDDIValueSetCachingPortType",
//			targetNamespace = "urn:uddi-org:v3_service")
public class UDDIValueSetCachingImpl extends AuthenticatedService implements UDDIValueSetCachingPortType {

    private static Log logger = LogFactory.getLog(UDDIValueSetCachingImpl.class);

    private UDDIServiceCounter serviceCounter;

    public UDDIValueSetCachingImpl() {
        super();
        serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(this.getClass());
    }
    
    public void getAllValidValues(String authInfo, String modelKey,
			Holder<String> chunkToken, Holder<List<ValidValue>> validValue)
			throws DispositionReportFaultMessage {
            long startTime = System.nanoTime();
            long procTime = System.nanoTime() - startTime;
            serviceCounter.update(ValueSetCachingQuery.GET_ALLVALIDVALUES, 
                    QueryStatus.SUCCESS, procTime);

            ValidateValueSetCaching.unsupportedAPICall();
    }
}
