package org.apache.juddi.api.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Enum to represent the queries within the Replication API.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum ReplicationQuery implements UDDIQuery {
    GET_CHANGERECORDS("get_changeRecords"),
    NOTIFY_CHANGERECORDSAVAILABLE("notify_changeRecordsAvailable"),
    DO_PING("do_ping"),
    GET_HIGHWATERMARKS("get_highWaterMarks"),
    TRANSFER_CUSTODY("transfer_custody");
    
    private String _query;
    private static Hashtable<String, ReplicationQuery> _replicationQueries = null;
    
    ReplicationQuery(final String query) {
        _query = query;
    }
    
    public String getQuery() {
        return _query;
    }
    
    public synchronized static void initReplicationQueries () {
            if (_replicationQueries == null) {
                _replicationQueries = new Hashtable();

                _replicationQueries.put(ReplicationQuery.GET_CHANGERECORDS.getQuery(), ReplicationQuery.GET_CHANGERECORDS);
                _replicationQueries.put(ReplicationQuery.NOTIFY_CHANGERECORDSAVAILABLE.getQuery(), ReplicationQuery.NOTIFY_CHANGERECORDSAVAILABLE);
                _replicationQueries.put(ReplicationQuery.DO_PING.getQuery(), ReplicationQuery.DO_PING);
                _replicationQueries.put(ReplicationQuery.GET_HIGHWATERMARKS.getQuery(), ReplicationQuery.GET_HIGHWATERMARKS);
                _replicationQueries.put(ReplicationQuery.TRANSFER_CUSTODY.getQuery(), ReplicationQuery.TRANSFER_CUSTODY);
            }
    }

    public static List<String> getQueries() {
        if (_replicationQueries == null) {
            initReplicationQueries();
        }
        
        List list = new ArrayList<String>(_replicationQueries.keySet());
        return list;
    }
    
    public static ReplicationQuery fromQuery(final String query) {
        if (_replicationQueries == null) {
            initReplicationQueries();
        }
        
        if (_replicationQueries.contains(query)) {
            return _replicationQueries.get(query);
        } else {
            throw new IllegalArgumentException("Unrecognized query " + query);
        }        

    }
}
