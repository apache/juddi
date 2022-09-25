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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.juddi.api.util.CustodyTransferQuery;
import org.apache.juddi.api.util.InquiryQuery;
import org.apache.juddi.api.util.JUDDIQuery;
import org.apache.juddi.api.util.PublicationQuery;
import org.apache.juddi.api.util.ReplicationQuery;
import org.apache.juddi.api.util.SecurityQuery;
import org.apache.juddi.api.util.SubscriptionListenerQuery;
import org.apache.juddi.api.util.SubscriptionQuery;
import org.apache.juddi.api.util.ValueSetCachingQuery;
import org.apache.juddi.api.util.ValueSetValidationQuery;

public class ServiceCounterLifecycleResource {
    static Map<String, UDDIServiceCounter> serviceCounters = 
            new HashMap<String, UDDIServiceCounter>();
    
    static Map<String, List<String>> implForQuery = new HashMap<String, List<String>>();
    
    public static String getObjectName(Class<?> klass) {
        StringBuffer objectName = new StringBuffer("portType=" + klass.toString());
        return objectName.toString();
    }
    
    public static synchronized void initQuery() {
        synchronized (implForQuery) {
            if (implForQuery.isEmpty()) {
                implForQuery = new HashMap<String, List<String>>();
                implForQuery.put(UDDICustodyTransferImpl.class.getName(), CustodyTransferQuery.getQueries());
                implForQuery.put(UDDIInquiryImpl.class.getName(), InquiryQuery.getQueries());
                implForQuery.put(UDDIPublicationImpl.class.getName(), PublicationQuery.getQueries());
                implForQuery.put(UDDIReplicationImpl.class.getName(), ReplicationQuery.getQueries());
                implForQuery.put(UDDISecurityImpl.class.getName(), SecurityQuery.getQueries());
                implForQuery.put(UDDISubscriptionImpl.class.getName(), SubscriptionQuery.getQueries());
                implForQuery.put(UDDISubscriptionListenerImpl.class.getName(), SubscriptionListenerQuery.getQueries());
                implForQuery.put(UDDIValueSetCachingImpl.class.getName(), ValueSetCachingQuery.getQueries());
                implForQuery.put(UDDIValueSetValidationImpl.class.getName(), ValueSetValidationQuery.getQueries());
                implForQuery.put(JUDDIApiImpl.class.getName(), JUDDIQuery.getQueries());
            }
        }
    }
    
    public static UDDIServiceCounter getServiceCounter(Class<?> klass) { 
        if (implForQuery.isEmpty()) {
            initQuery();
        }
        
        String objectName = getObjectName(klass);
        synchronized (serviceCounters) {
            UDDIServiceCounter serviceCounter = serviceCounters.get(objectName);
            if (serviceCounter == null) {
                UDDIServiceCounter uddiServiceCounter = new UDDIServiceCounter();
                uddiServiceCounter.initList(klass, (List<String>)implForQuery.get(klass.getName()));
                uddiServiceCounter.registerMBean();                
                serviceCounters.put(objectName, uddiServiceCounter);
                return uddiServiceCounter;
            } else {
                return serviceCounter;
            }
        }
    }
}
