package org.apache.juddi.api.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Enum to represent the queries within the publication API.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum PublicationQuery implements UDDIQuery {
    GET_REGISTEREDINFO("get_registeredInfo"),
    SAVE_BUSINESS("save_business"),
    SAVE_SERVICE("save_service"),
    SAVE_BINDING("save_binding"),
    SAVE_TMODEL("save_tmodel"),
    DELETE_BUSINESS("delete_business"),
    DELETE_SERVICE("delete_service"),
    DELETE_BINDING("delete_binding"),
    DELETE_TMODEL("delete_tmodel"),
    ADD_PUBLISHERASSERTIONS("add_publisherassertions"),
    SET_PUBLISHERASSERTIONS("set_publisherassertions"),
    GET_PUBLISHERASSERTIONS("get_publisherassertions"),
    DELETE_PUBLISHERASSERTIONS("delete_publisherassertions"),
    GET_ASSERTIONSTATUSREPORT("get_assertionstatusreport");
    
    private String _query;
    private static Hashtable<String, PublicationQuery> _publicationQueries = null;
    
    PublicationQuery(final String query) {
        _query = query;
    }
    
    public String getQuery() {
        return _query;
    }
    
    public synchronized static void initPublicationQueries () {
            if (_publicationQueries == null) {
                _publicationQueries = new Hashtable();

                _publicationQueries.put(PublicationQuery.GET_REGISTEREDINFO.getQuery(), PublicationQuery.GET_REGISTEREDINFO);
                _publicationQueries.put(PublicationQuery.SAVE_BUSINESS.getQuery(), PublicationQuery.SAVE_BUSINESS);
                _publicationQueries.put(PublicationQuery.SAVE_SERVICE.getQuery(), PublicationQuery.SAVE_SERVICE);
                _publicationQueries.put(PublicationQuery.SAVE_BINDING.getQuery(), PublicationQuery.SAVE_BINDING);
                _publicationQueries.put(PublicationQuery.SAVE_TMODEL.getQuery(), PublicationQuery.SAVE_TMODEL);
                _publicationQueries.put(PublicationQuery.DELETE_BUSINESS.getQuery(), PublicationQuery.DELETE_BUSINESS);
                _publicationQueries.put(PublicationQuery.DELETE_SERVICE.getQuery(), PublicationQuery.DELETE_SERVICE);
                _publicationQueries.put(PublicationQuery.DELETE_BINDING.getQuery(), PublicationQuery.DELETE_BINDING);
                _publicationQueries.put(PublicationQuery.DELETE_TMODEL.getQuery(), PublicationQuery.DELETE_TMODEL);
                _publicationQueries.put(PublicationQuery.ADD_PUBLISHERASSERTIONS.getQuery(), PublicationQuery.ADD_PUBLISHERASSERTIONS);
                _publicationQueries.put(PublicationQuery.SET_PUBLISHERASSERTIONS.getQuery(), PublicationQuery.SET_PUBLISHERASSERTIONS);
                _publicationQueries.put(PublicationQuery.GET_PUBLISHERASSERTIONS.getQuery(), PublicationQuery.GET_PUBLISHERASSERTIONS);
                _publicationQueries.put(PublicationQuery.DELETE_PUBLISHERASSERTIONS.getQuery(), PublicationQuery.DELETE_PUBLISHERASSERTIONS);
                _publicationQueries.put(PublicationQuery.GET_ASSERTIONSTATUSREPORT.getQuery(), PublicationQuery.GET_ASSERTIONSTATUSREPORT);       
            }
    }

    public static List<String> getQueries() {
        if (_publicationQueries == null) {
            initPublicationQueries();
        }
        
        List list = new ArrayList<String>(_publicationQueries.keySet());
        return list;
    }

    public static PublicationQuery fromQuery(final String query) {
        if (_publicationQueries == null) {
            initPublicationQueries();
        }
        
        if (_publicationQueries.contains(query)) {
            return _publicationQueries.get(query);
        } else {
            throw new IllegalArgumentException("Unrecognized query " + query);
        }        
    }    
}
