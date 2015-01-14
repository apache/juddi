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
 */package org.apache.juddi.api.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Enum to represent the queries within the ValueSetValidation API.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum ValueSetValidationQuery implements UDDIQuery {
    VALIDATE_VALUES("validate_values");
    
    private String _query;
    private static Hashtable<String, ValueSetValidationQuery> _valueSetValidationQueries = null;
        
    ValueSetValidationQuery(final String query) {
        _query = query;
    }
        
    public String getQuery() {
        return _query;
    }
        
    public synchronized static void initValueSetValidationQueries () {
            if (_valueSetValidationQueries == null) {
                _valueSetValidationQueries = new Hashtable<String, ValueSetValidationQuery>();

                _valueSetValidationQueries.put(ValueSetValidationQuery.VALIDATE_VALUES.getQuery(), ValueSetValidationQuery.VALIDATE_VALUES);
            }
    }

    public static List<String> getQueries() {
        if (_valueSetValidationQueries == null) {
            initValueSetValidationQueries();
        }
        
        List list = new ArrayList<String>(_valueSetValidationQueries.keySet());
        return list;
    }
    
    public static ValueSetValidationQuery fromQuery(final String query) {
        if (_valueSetValidationQueries == null) {
            initValueSetValidationQueries();
        }
        
        if (_valueSetValidationQueries.contains(query)) {
            return _valueSetValidationQueries.get(query);
        } else {
            throw new IllegalArgumentException("Unrecognized query " + query);
        }       
    }
}