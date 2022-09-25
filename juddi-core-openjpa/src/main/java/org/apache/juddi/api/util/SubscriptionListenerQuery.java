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
package org.apache.juddi.api.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Enum to represent the methods within the SubscriptionListener API.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum SubscriptionListenerQuery implements UDDIQuery {
    NOTIFY_SUBSCRIPTIONLISTENER("notify_subscriptionListener");
        
    private String _query;
    private static Hashtable<String, SubscriptionListenerQuery> _subscriptionListenerQueries = null;
        
    SubscriptionListenerQuery(final String query) {
        _query = query;
    }
        
    public String getQuery() {
        return _query;
    }
        
    public synchronized static void initSubscriptionListenerQueries () {
            if (_subscriptionListenerQueries == null) {
                _subscriptionListenerQueries = new Hashtable();

                _subscriptionListenerQueries.put(SubscriptionListenerQuery.NOTIFY_SUBSCRIPTIONLISTENER.getQuery(), SubscriptionListenerQuery.NOTIFY_SUBSCRIPTIONLISTENER);
            }
    }

    public static List<String> getQueries() {
        if (_subscriptionListenerQueries == null) {
            initSubscriptionListenerQueries();
        }
        
        List<String> list = new ArrayList<String>(_subscriptionListenerQueries.keySet());
        return list;
    }
    
    /**
     * this doesn't appear to be used anywhere and will be removed in a future version
     * @param query
     * @return
     * @deprecated
     */
    
    public static SubscriptionListenerQuery fromQuery(final String query) {
        if (_subscriptionListenerQueries == null) {
            initSubscriptionListenerQueries();
        }
        
        if (_subscriptionListenerQueries.contains(query)) {
            return _subscriptionListenerQueries.get(query);
        } else {
            throw new IllegalArgumentException("Unrecognized query " + query);
        }       
    }
}
