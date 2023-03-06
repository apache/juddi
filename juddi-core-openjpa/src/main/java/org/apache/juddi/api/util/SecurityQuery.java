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
    
    /**
     * this doesn't appear to be used anywhere and will be removed in a future version
     * @param query
     * @return
     * @deprecated
     */
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
