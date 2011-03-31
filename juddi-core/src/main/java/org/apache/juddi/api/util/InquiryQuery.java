package org.apache.juddi.api.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Enum to represent the queries within the Inquiry API.
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public enum InquiryQuery implements UDDIQuery {
    FIND_BUSINESS ("find_business"),
    FIND_SERVICE ("find_service"),
    FIND_BINDING ("find_binding"), 
    FIND_TMODEL ("find_tModel"),
    FIND_RELATEDBUSINESSES ("find_relatedBusinesses"),
    GET_BUSINESSDETAIL ("get_businessDetail"),
    GET_SERVICEDETAIL ("get_serviceDetail"),
    GET_BINDINGDETAIL ("get_bindingDetail"),
    GET_TMODELDETAIL ("get_tModelDetail"),
    GET_OPERATIONALINFO ("get_operationalInfo");
    
    private String _query;
    private static Hashtable<String,InquiryQuery> _inquiryQueries = null;
    
    InquiryQuery(final String query) {
        _query = query;
    }
    
    public String getQuery() {
        return _query;
    }
    
    public synchronized static void initInquiryQueries () {
        if (_inquiryQueries == null) {
            _inquiryQueries = new Hashtable();        
            _inquiryQueries.put("find_business", InquiryQuery.FIND_BUSINESS);
            _inquiryQueries.put("find_service", InquiryQuery.FIND_SERVICE);
            _inquiryQueries.put("find_binding", InquiryQuery.FIND_BINDING);
            _inquiryQueries.put("find_tModel", InquiryQuery.FIND_TMODEL);
            _inquiryQueries.put("find_relatedBusinesses", InquiryQuery.FIND_RELATEDBUSINESSES);
            _inquiryQueries.put("get_businessDetail", InquiryQuery.GET_BUSINESSDETAIL);
            _inquiryQueries.put("get_serviceDetail", InquiryQuery.GET_SERVICEDETAIL);
            _inquiryQueries.put("get_bindingDetail", InquiryQuery.GET_BINDINGDETAIL);
            _inquiryQueries.put("get_tModelDetail", InquiryQuery.GET_TMODELDETAIL);
            _inquiryQueries.put("get_operationalInfo", InquiryQuery.GET_OPERATIONALINFO);
        }
    }
    
    public static List<String> getQueries() {
        if (_inquiryQueries == null) {
            initInquiryQueries();
        }
        
        List list = new ArrayList<String>(_inquiryQueries.keySet());
        return list;
    }
        
    public static InquiryQuery fromQuery(final String query) {
        if (_inquiryQueries == null) {
            initInquiryQueries();
        }
        
        if (_inquiryQueries.contains(query)) {
            return _inquiryQueries.get(query);
        } else {
            throw new IllegalArgumentException("Unrecognized query " + query);
        }
    }    
}
