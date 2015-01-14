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
 * Enum to represent the methods within the Subscription API.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum SubscriptionQuery implements UDDIQuery {
    DELETE_SUBSCRIPTION("delete_subscription"),
    GET_SUBSCRIPTIONRESULTS("get_subscriptionResults"),
    GET_SUBSCRIPTIONS("get_subscriptions"),
    SAVE_SUBSCRIPTION("save_subscription");
        
    private String _query;
    private static Hashtable<String, SubscriptionQuery> _subscriptionQueries = null;
        
    SubscriptionQuery(final String query) {
        _query = query;
    }
        
    public String getQuery() {
        return _query;
    }
        
    public synchronized static void initSubscriptionQueries () {
        if (_subscriptionQueries == null) {
                _subscriptionQueries = new Hashtable();

                _subscriptionQueries.put(SubscriptionQuery.DELETE_SUBSCRIPTION.getQuery(), SubscriptionQuery.DELETE_SUBSCRIPTION);
                _subscriptionQueries.put(SubscriptionQuery.GET_SUBSCRIPTIONRESULTS.getQuery(), SubscriptionQuery.GET_SUBSCRIPTIONRESULTS);
                _subscriptionQueries.put(SubscriptionQuery.GET_SUBSCRIPTIONS.getQuery(), SubscriptionQuery.GET_SUBSCRIPTIONS);
                _subscriptionQueries.put(SubscriptionQuery.SAVE_SUBSCRIPTION.getQuery(), SubscriptionQuery.SAVE_SUBSCRIPTION);
        }
    }
    
    public static List<String> getQueries() {
        if (_subscriptionQueries == null) {
            initSubscriptionQueries();
        }
        
        List list = new ArrayList<String>(_subscriptionQueries.keySet());
        return list;
    }

    public static SubscriptionQuery fromQuery(final String query) {
        if (_subscriptionQueries == null) {
            initSubscriptionQueries();
        }
        
        if (_subscriptionQueries.contains(query)) {
            return _subscriptionQueries.get(query);
        } else {
            throw new IllegalArgumentException("Unrecognized query " + query);
        }       
    }
}
