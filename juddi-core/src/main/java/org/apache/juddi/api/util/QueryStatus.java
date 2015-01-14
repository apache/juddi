/*
 * Copyright 2001-2015 The Apache Software Foundation.
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
 * Statuses of a query.    Successs represents a successful query, failed 
 * represents one that throws a DispositionFaultMessage.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum QueryStatus {
    SUCCESS("success"),
    FAILED ("failed");

    private String _status;
    private static Hashtable<String, QueryStatus> _statuses = null;
    
    QueryStatus(final String status) {
        _status = status;
    }
    
    public String getStatus() {
        return _status;
    }
    
    public synchronized static void initStatuses () {
        if (_statuses == null) {
            _statuses = new Hashtable();

            _statuses.put(QueryStatus.SUCCESS.getStatus(), QueryStatus.SUCCESS);
            _statuses.put(QueryStatus.FAILED.getStatus(), QueryStatus.FAILED);
        }
    }

    public static List<String> getQueries() {
        if (_statuses == null) {
            initStatuses();
        }
    
        List list = new ArrayList<String>(_statuses.keySet());
        return list;
    }

    public static QueryStatus fromStatus(final String status) {
        if (_statuses == null) {
            initStatuses();
        }
    
        if (_statuses.contains(status)) {
            return _statuses.get(status);
        } else {
            throw new IllegalArgumentException("Unrecognized status " + status);
        }       
    }
}