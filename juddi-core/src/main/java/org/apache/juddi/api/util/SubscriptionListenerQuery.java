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

    public static List getQueries() {
        if (_subscriptionListenerQueries == null) {
            initSubscriptionListenerQueries();
        }
        
        List list = new ArrayList<String>(_subscriptionListenerQueries.keySet());
        return list;
    }
    
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
