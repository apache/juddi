package org.apache.juddi.api.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Enum to represent the queries within the Custody Transfer API.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum CustodyTransferQuery implements UDDIQuery {
    DISCARD_TRANSFERTOKEN("discard_transferToken"),
    GET_TRANSFERTOKEN("get_transferToken"),
    TRANSFER_ENTITIES("transfer_entities");
    
    private String _query;
    private static Hashtable<String, CustodyTransferQuery> _custodyTransferQueries = null;
    
    CustodyTransferQuery(final String query) {
        _query = query;
    }
    
    public String getQuery() {
        return _query;
    }
    
    public synchronized static void initCustodyTransferQueries () {
        if (_custodyTransferQueries == null) {
                _custodyTransferQueries = new Hashtable();
                _custodyTransferQueries.put("discard_transferToken", CustodyTransferQuery.DISCARD_TRANSFERTOKEN);
                _custodyTransferQueries.put("get_transferToken", CustodyTransferQuery.GET_TRANSFERTOKEN);
                _custodyTransferQueries.put("transfer_entities", CustodyTransferQuery.TRANSFER_ENTITIES);
        }
    }
    
    public static List<String> getQueries() {
        if (_custodyTransferQueries == null) {
            initCustodyTransferQueries();
        }
        
        List list = new ArrayList<String>(_custodyTransferQueries.keySet());
        return list;
    }

    public static CustodyTransferQuery fromQuery(final String query) {
        if (_custodyTransferQueries == null) {
            initCustodyTransferQueries();
        }
        
        if (_custodyTransferQueries.contains(query)) {
            return _custodyTransferQueries.get(query);
        } else {
            throw new IllegalArgumentException("Unrecognized query " + query);
        }
    }
}
