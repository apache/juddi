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

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.api.util.ValueSetCachingQuery;
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

        /**
         *
         * @param authInfo An optional element that contains an authentication
         * token. Authentication tokens are obtained using the get_authToken API
         * call or through some other means external to this specification.
         * Providers of get_allValidValues Web services that serve multiple
         * registries and providers that restrict who can use their service may
         * require authInfo for this API.
         * @param modelKey A required uddiKey value that identifies the specific
         * instance of the tModel which describes the value set or category
         * group system for which a Web service to get all valid values has been
         * provided. It uniquely identifies the category, identifier, or
         * category group system for which valid values are being requested.
         * @param chunkToken Optional element used to retrieve subsequent groups
         * of data when the first invocation of this API indicates more data is
         * available. This occurs when a chunkToken is returned whose value is
         * not "0" in the validValuesList structure described in the next
         * section. To retrieve the next chunk of data, the chunkToken returned
         * should be used as an argument to the next invocation of this API.
         * @param validValue A validValuesList structure is returned containing
         * the set of valid values for the external category or identifier
         * system. The list MUST contain a chunkToken if the Web service
         * provider wishes to provide the data in packets.
         * @throws DispositionReportFaultMessage
         */
        @Override
        public void getAllValidValues(String authInfo, String modelKey,
                Holder<String> chunkToken, Holder<List<ValidValue>> validValue)
                throws DispositionReportFaultMessage {

                long startTime = System.currentTimeMillis();
                List<String> validValues = UDDIValueSetValidationImpl.getValidValues(modelKey);

                Holder<List<ValidValue>> ret = new Holder<List<ValidValue>>(new ArrayList<ValidValue>());
                if (validValues != null) {
                        for (int i = 0; i < validValues.size(); i++) {
                                ValidValue x = new ValidValue(validValues.get(i));
                                ret.value.add(x);
                        }
                }
                chunkToken = new Holder<String>();
                chunkToken.value ="0";

                long procTime = System.currentTimeMillis() - startTime;
                serviceCounter.update(ValueSetCachingQuery.GET_ALLVALIDVALUES,
                        QueryStatus.SUCCESS, procTime);


        }
}
