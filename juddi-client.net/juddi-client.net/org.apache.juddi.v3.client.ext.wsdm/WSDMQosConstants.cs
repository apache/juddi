/**
 * Copyright 2013 The Apache Software Foundation.
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
using System;
using System.Collections.Generic;
using System.Text;

namespace org.apache.juddi.v3.client.ext.wsdm
{
    /// <summary>
    /// Defines constants for OASIS's WSDM specification. Specifically tModels for use in UDDI
    /// https://www.oasis-open.org/committees/download.php/6227/uddi-spec-tc-tn-QoS-metrics-20040224.doc
    /// @author Alex O'Ree 
    /// </summary>
    public static class WSDMQosConstants
    {
        
    /**
     * This is the 'for more information' url for WSDM + UDDI
     */
    public static readonly string moreInfoUrl = "https://www.oasis-open.org/committees/download.php/6227/uddi-spec-tc-tn-QoS-metrics-20040224.doc";
    public static readonly string WSDM_KEYGEN = "urn:wsdm.org:keygenerator";
    public static readonly string WSDM_METRIC_KEYGEN = "urn:wsdm.org:metric:keygenerator";
    public static readonly string WSDM_IDENTITY_KEYGEN = "urn:wsdm.org:identity:keygenerator";
    public static readonly string WSDM_QOS_KEYGEN = "urn:wsdm.org:qos:keygenerator";
    /**
     * The number of requests to a given service. (number of requests)
     */
    public static readonly string METRIC_REQUEST_COUNT_KEY = "urn:wsdm.org:metric:requestcount";
    public static readonly string METRIC_RequestCount = "RequestCount";
    /**
     * The number of replies from a given service. (number of replies)
     */
    public static readonly string METRIC_REPLY_COUNT_KEY = "urn:wsdm.org:metric:replycount";
    public static readonly string METRIC_ReplyCount = "ReplyCount";
    /**
     * The number of faults from a given service. (number of faults)
     */
    public static readonly string METRIC_FAULT_COUNT_KEY = "urn:wsdm.org:metric:faultcount";
    public static readonly string METRIC_FaultCount = "FaultCount";
    /**
     * This is the unique identity by which the resource (service) is known to
     * the management system. It is useful for further queries. (resource
     * identification in URI format)
     */
    public static readonly string IDENTITY_RESOURCE_ID_KEY = "urn:wsdm.org:identity:resourceId";
    public static readonly string IDENTITY_ResourceId = "ResourceId";
    /**
     * Represents the last time this metric was updated. (time value)
     */
    public static readonly string METRIC_LAST_UPDATE_TIME_KEY = "urn:wsdm.org:metric:lastupdatetime";
    public static readonly string METRIC_LastUpdateTime = "LastUpdateTime";
    /**
     * Average response time of the service. (numeric value or symbolic rating)
     */
    public static readonly string QOS_RESPONSE_TIME_AVG_KEY = "urn:wsdm.org:qos:responsetime_average";
    public static readonly string QOS_ResponseTime_Average = "ResponseTime_Average";
    /**
     * Throughput count. (numeric value or symbolic rating)
     */
    public static readonly string QOS_THROUGHPUT_COUNT_KEY = "urn:wsdm.org:qos:throughput_count";
    public static readonly string QOS_Throughput_count = "Throughput_count";
    /**
     * Throughput bytes. (numeric value or symbolic rating)
     */
    public static readonly string QOS_THROUGHPUT_BYTES_KEY = "urn:wsdm.org:qos:throughput_bytes";
    public static readonly string QOS_Throughput_bytes = "Throughput_bytes";
    /**
     * Reliability or the measure of. (numeric value or symbolic rating)
     */
    public static readonly string QOS_RELIABILITY_KEY = "urn:wsdm.org:qos:reliability";
    public static readonly string QOS_Reliability = "Reliability";
    /**
     * The beginning on the reporting time period used for the information
     * above. (dateTime)
     */
    public static readonly string QOS_REPORTING_PERIOD_START_KEY = "urn:wsdm.org:qos:reportingperiodstart";
    public static readonly string QOS_ReportingPeriodStart = "ReportingPeriodStart";
    /**
     * The end of the reporting time period used for the information above.
     * (dateTime)
     */
    public static readonly string QOS_REPORTING_PERIOD_END_KEY = "urn:wsdm.org:qos:reportingperiodend";
    public static readonly string QOS_ReportingPeriodEnd = "ReportingPeriodEnd";
    /**
     * How often is this information updated in UDDI (it is not assumed to be
     * realtime). (duration)
     */
    public static readonly string QOS_UPDATE_INTERVAL_KEY = "urn:wsdm.org:qos:updateinterval";
    public static readonly string QOS_UpdateInterval = "UpdateInterval";
    }
}
