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

    /**
     * this doesn't appear to be used anywhere and will be removed in a future version
     * @param query
     * @return
     * @deprecated
     */
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