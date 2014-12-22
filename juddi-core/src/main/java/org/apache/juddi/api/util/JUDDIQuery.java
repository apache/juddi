/*
 * Copyright 2014 The Apache Software Foundation.
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
 */
package org.apache.juddi.api.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 *
 * @author alex
 */
public enum JUDDIQuery implements UDDIQuery {

        SAVE_PUBLISHER("save_publisher"),
        DELETE_PUBLISHER("delete_publisher"),
        GET_PUBLISHER_DETAIL("get_publisherDetail"),
        GET_ALL_PUBLISHER_DETAIL("get_allPublisherDetail"),
        ADMIN_DELETE_TMODEL("admin_deleteTmodel"),
        DELETE_CLIENT_SUB("delete_clientSubscriptionInfo"),
        SAVE_CLIENT_SUB("save_clientSubscriptionInfo"),
        GET_ALL_CLIENT_SUB("get_allClientSubscriptionInfo"),
        GET_CLIENT_SUB("get_clientSubscriptionInfo"),
        SAVE_CLERK("save_clerk"),
        SAVE_NODE("save_node"),
        GET_ALL_NODES("get_allNodes"),
        DELETE_NODE("delete_node"),
        GET_ALL_CLERKS("get_allClerks"),
        DELETE_CLERK("delete_clerk"),
        ADMIN_DELETE_SUB("admin_deleteSubcriptionInfo"),
        ADMIN_SAVE_TMODEL("admin_saveTmodel"),
        ADMIN_SAVE_BUSINESS("admin_saveBusiness"),
        SET_REPLICATION_NODES("set_replicationNodes"),
        GET_REPLICATION_NODES("get_replicationNodes"),
        ADMIN_SAVE_SUB("admin_saveClientSubscription"),
        INVOKE_SYNCSUB("invoke_synchSubscription");

        private String _query;
        private static Hashtable<String, JUDDIQuery> _inquiryQueries = null;

        JUDDIQuery(final String query) {
                _query = query;
        }

        public String getQuery() {
                return _query;
        }

        public synchronized static void initInquiryQueries() {
                if (_inquiryQueries == null) {
                        _inquiryQueries = new Hashtable();
                        _inquiryQueries.put("save_publisher", JUDDIQuery.SAVE_PUBLISHER);
                        _inquiryQueries.put("delete_publisher", JUDDIQuery.DELETE_PUBLISHER);
                        _inquiryQueries.put("get_publisherDetail", JUDDIQuery.GET_PUBLISHER_DETAIL);
                        _inquiryQueries.put("get_allPublisherDetail", JUDDIQuery.GET_ALL_PUBLISHER_DETAIL);
                        _inquiryQueries.put("admin_deleteTmodel", JUDDIQuery.ADMIN_DELETE_TMODEL);
                        _inquiryQueries.put("delete_clientSubscriptionInfo", JUDDIQuery.DELETE_CLIENT_SUB);
                        _inquiryQueries.put("save_clientSubscriptionInfo", JUDDIQuery.SAVE_CLIENT_SUB);
                        _inquiryQueries.put("get_allClientSubscriptionInfo", JUDDIQuery.GET_ALL_CLIENT_SUB);
                        _inquiryQueries.put("get_clientSubscriptionInfo", JUDDIQuery.GET_CLIENT_SUB);
                        _inquiryQueries.put("save_clerk", JUDDIQuery.SAVE_CLERK);
                        _inquiryQueries.put("save_node", JUDDIQuery.SAVE_NODE);
                        _inquiryQueries.put("get_allNodes", JUDDIQuery.GET_ALL_NODES);

                        _inquiryQueries.put("delete_node", JUDDIQuery.DELETE_NODE);

                        _inquiryQueries.put("get_allClerks", JUDDIQuery.GET_ALL_CLERKS);
                        _inquiryQueries.put("delete_clerk", JUDDIQuery.DELETE_CLERK);
                        _inquiryQueries.put("admin_deleteSubcriptionInfo", JUDDIQuery.ADMIN_DELETE_SUB);
                        _inquiryQueries.put("admin_saveTmodel", JUDDIQuery.ADMIN_SAVE_TMODEL);
                        _inquiryQueries.put("admin_saveBusiness", JUDDIQuery.ADMIN_SAVE_BUSINESS);
                        _inquiryQueries.put("set_replicationNodes", JUDDIQuery.SET_REPLICATION_NODES);
                        _inquiryQueries.put("get_replicationNodes", JUDDIQuery.GET_REPLICATION_NODES);
                        _inquiryQueries.put("admin_saveClientSubscription", JUDDIQuery.ADMIN_SAVE_SUB);
                        
                        _inquiryQueries.put("invoke_synchSubscription", JUDDIQuery.INVOKE_SYNCSUB);
        
                        
                }
        }

        public static List<String> getQueries() {
                if (_inquiryQueries == null) {
                        initInquiryQueries();
                }

                List list = new ArrayList<String>(_inquiryQueries.keySet());
                return list;
        }

        public static JUDDIQuery fromQuery(final String query) {
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
