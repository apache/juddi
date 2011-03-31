package org.apache.juddi.api.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Enum to represent the methods within the ValueSetCaching API.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum ValueSetCachingQuery implements UDDIQuery {
    GET_ALLVALIDVALUES("get_allValidValues");
    
    private String _query;
    private static Hashtable<String, ValueSetCachingQuery> _valueSetCachingQueries = null;
        
    ValueSetCachingQuery(final String query) {
        _query = query;
    }
        
    public String getQuery() {
        return _query;
    }
        
    public synchronized static void initValueSetCachingQueries () {
            if (_valueSetCachingQueries == null) {
                _valueSetCachingQueries = new Hashtable();

                _valueSetCachingQueries.put(ValueSetCachingQuery.GET_ALLVALIDVALUES.getQuery(), ValueSetCachingQuery.GET_ALLVALIDVALUES);
            }
    }
    
    public static List<String> getQueries() {
        if (_valueSetCachingQueries == null) {
            initValueSetCachingQueries();
        }
        
        List list = new ArrayList<String>(_valueSetCachingQueries.keySet());
        return list;
    }

    public static ValueSetCachingQuery fromQuery(final String query) {
        if (_valueSetCachingQueries == null) {
            initValueSetCachingQueries();
        }
        
        if (_valueSetCachingQueries.contains(query)) {
            return _valueSetCachingQueries.get(query);
        } else {
            throw new IllegalArgumentException("Unrecognized query " + query);
        }       
    }
}