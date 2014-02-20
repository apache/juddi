<%-- 
    Document   : quickref_tmodelinstance
    Created on : Feb 9, 2014, 3:19:10 PM
    Author     : Alex O'Ree
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="btn-group">
        <a class="btn dropdown-toggle btn-info" data-toggle="dropdown"  href="#">Protocol<span class="caret"></span></a>
        <ul class="dropdown-menu">
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:protocol:soap');">SOAP 1.1</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:protocol:soap12');">SOAP 1.2</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:protocol:rest');">REST</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:protocol:http');">HTTP</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:protocol:serverauthenticatedssl3');">SSL</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:protocol:mutualauthenticatedssl3');">SSL w/Client Cert</a></li>
        </ul>
</div>
<div class="btn-group">
        <a class="btn dropdown-toggle btn-info" data-toggle="dropdown" href="#">Transport<span class="caret"></span></a>
        <ul class="dropdown-menu">
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:http');">HTTP</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:amqp');">AMQP</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:jms');">JMS</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:ftp');">FTP</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:smtp');">SMTP</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:userfriendlysmtp');">User Friendly SMTP</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:jndi-rmi');">JNDI-RMI</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:rmi');">RMI</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:omgdds');">OMG DDS</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:telephone');">POTS</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:fax');">FAX</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('uddi:uddi.org:transport:udp');">UDP</a></li>
        </ul>
</div>


<div class="btn-group">
        <a class="btn dropdown-toggle btn-info" data-toggle="dropdown" href="#">QoS<span class="caret"></span></a>
        <ul class="dropdown-menu">
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:identity:resourceid');">Resource ID</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:qos:updateinterval');">Update Interval</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:qos:reportingperiodend');">Reporting Period End</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:qos:reportingperiodstart');">Reporting Period Start</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:metric:lastupdatetime');">Last Update Time</a></li>
                <li class="divider"></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:metric:requestcount');">Request Count</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:metric:replycount');">Reply Count</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:metric:faultcount');">Fault Count</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:qos:throughput_bytes');">Throughput Bytes</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:qos:throughput_count');">Throughput Count</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:qos:responsetime_average');">Response Time Average</a></li>
                <li><a href="javascript:AddTmodelInstanceParam('urn:wsdm.org:qos:reliability');">Reliability</a></li>
        </ul>
</div>

<br><br>