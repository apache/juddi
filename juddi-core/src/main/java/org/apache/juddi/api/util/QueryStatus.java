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