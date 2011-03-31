package org.apache.juddi.api.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.juddi.api.util.CustodyTransferQuery;
import org.apache.juddi.api.util.InquiryQuery;
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
    
    static Map implForQuery = new HashMap<String, List<String>>();
    
    public static String getObjectName(Class klass) {
        StringBuffer objectName = new StringBuffer("portType=" + klass.toString());
        return objectName.toString();
    }
    
    public static synchronized void initQuery() {
        synchronized (implForQuery) {
            if (implForQuery.isEmpty()) {
                implForQuery = new HashMap<String, List<String>>();
                implForQuery.put(UDDICustodyTransferImpl.class, CustodyTransferQuery.getQueries());
                implForQuery.put(UDDIInquiryImpl.class, InquiryQuery.getQueries());
                implForQuery.put(UDDIPublicationImpl.class, PublicationQuery.getQueries());
                implForQuery.put(UDDIReplicationImpl.class, ReplicationQuery.getQueries());
                implForQuery.put(UDDISecurityImpl.class, SecurityQuery.getQueries());
                implForQuery.put(UDDISubscriptionImpl.class, SubscriptionQuery.getQueries());
                implForQuery.put(UDDISubscriptionListenerImpl.class, SubscriptionListenerQuery.getQueries());
                implForQuery.put(UDDIValueSetCachingImpl.class, ValueSetCachingQuery.getQueries());
                implForQuery.put(UDDIValueSetValidationImpl.class, ValueSetValidationQuery.getQueries());
            }
        }
    }
    
    public static UDDIServiceCounter getServiceCounter(Class klass) { 
        if (implForQuery.isEmpty()) {
            initQuery();
        }
        
        String objectName = getObjectName(klass);
        synchronized (serviceCounters) {
            UDDIServiceCounter serviceCounter = serviceCounters.get(objectName);
            if (serviceCounter == null) {
                UDDIServiceCounter uddiServiceCounter = new UDDIServiceCounter();
                uddiServiceCounter.initList(klass, (List<String>)implForQuery.get(klass));
                uddiServiceCounter.registerMBean();                
                serviceCounters.put(objectName, uddiServiceCounter);
                return uddiServiceCounter;
            } else {
                return serviceCounter;
            }
        }
    }
}
