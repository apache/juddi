package org.apache.juddi.api.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Enum to represent the methods within the Security API.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum SecurityQuery implements UDDIQuery {
    GET_AUTHTOKEN("get_authToken"),
    DISCARD_AUTHTOKEN("discard_authToken");
    
    private String _query;
    private static Hashtable<String, SecurityQuery> _securityQueries = null;
    
    SecurityQuery(final String query) {
        _query = query;
    }
    
    public String getQuery() {
        return _query;
    }
    
    public synchronized static void initSecurityQueries () {
            if (_securityQueries == null) {
                _securityQueries = new Hashtable();

                _securityQueries.put(SecurityQuery.GET_AUTHTOKEN.getQuery(), SecurityQuery.GET_AUTHTOKEN);
                _securityQueries.put(SecurityQuery.DISCARD_AUTHTOKEN.getQuery(), SecurityQuery.DISCARD_AUTHTOKEN);
            }
    }

    public static List<String> getQueries() {
        if (_securityQueries == null) {
            initSecurityQueries();
        }
        
        List list = new ArrayList<String>(_securityQueries.keySet());
        return list;
    }
    
    public static SecurityQuery fromQuery(final String query) {
        if (_securityQueries == null) {
            initSecurityQueries();
        }
        
        if (_securityQueries.contains(query)) {
            return _securityQueries.get(query);
        } else {
            throw new IllegalArgumentException("Unrecognized query " + query);
        }       
    }
}
