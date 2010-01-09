/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.xlt.util;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xceptance.common.xml.DomUtils;
import com.xceptance.xlt.api.engine.RequestData;
import com.xceptance.xlt.api.engine.Session;

/**
 *
 */
public class LoggingHandler implements SOAPHandler<SOAPMessageContext>
{
    private static final Log LOG = LogFactory.getLog(LoggingHandler.class);

    /**
     * 
     */
    private static final String INBOUND_MESSAGE_FORMAT = "Inbound Message:\nHTTP Headers:\n%s\nSoap Message:\n%s\nMessage Context Properties:\n%s";

    /**
     * 
     */
    private static final String OUTBOUND_MESSAGE_FORMAT = "Outbound Message:\nHTTP Headers:\n%s\nSoap Message:\n%s\nMessage Context Properties:\n%s";

    /**
     * 
     */
    private static final String XLT_FAULT = "com.xceptance.xlt.ws.handler.fault";

    /**
     * 
     */
    private static final String XLT_REQUEST_DATA = "com.xceptance.xlt.ws.handler.requestData";

    /**
     * {@inheritDoc}
     */
    public void close(MessageContext context)
    {
        unregisterMessage(context);
    }

    /**
     * {@inheritDoc}
     */
    public Set<QName> getHeaders()
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handleFault(SOAPMessageContext context)
    {
        logMessage(context);

        setFaultReceived(context);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean handleMessage(SOAPMessageContext context)
    {
        logMessage(context);

        if (isOutboundMessage(context))
        {
            registerMessage(context);
        }

        return true;
    }

    private String getContentType(MessageContext context)
    {
        // TODO: get it from Content-type HTTP header
        return "text/xml";
    }

    private String getOperationName(SOAPMessageContext context)
    {
        // service is optional :-(
        QName service = (QName) context.get(MessageContext.WSDL_SERVICE);
        if (service == null)
        {
            service = new QName("<unknown>");
        }

        // operation is optional :-(
        QName operation = (QName) context.get(MessageContext.WSDL_OPERATION);
        if (operation == null)
        {
            // operation = new QName("<unknown>");

            try
            {
                operation = new QName(context.getMessage().getSOAPBody().getFirstChild().getLocalName());
            }
            catch (SOAPException ex)
            {
                throw new RuntimeException("", ex);
            }
        }

        return service.getLocalPart() + "." + operation.getLocalPart();
    }

    private int getResponseCode(MessageContext context)
    {
        Integer responseCode = (Integer) context.get(MessageContext.HTTP_RESPONSE_CODE);

        return responseCode.intValue();
    }

    private String getServiceUrl(MessageContext context)
    {
        // TODO: service URL is optional :-(
        return "???";
    }

    private boolean isFaultReceived(MessageContext context)
    {
        return context.containsKey(XLT_FAULT);
    }

    private boolean isOutboundMessage(MessageContext context)
    {
        Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        return outboundProperty.booleanValue();
    }

    private void logMessage(SOAPMessageContext context)
    {
        boolean isOutbound = isOutboundMessage(context);

        // optionally append the HTTP request/response headers
        String headersKey = isOutbound ? MessageContext.HTTP_REQUEST_HEADERS : MessageContext.HTTP_RESPONSE_HEADERS;
        StringBuilder httpHeaders = new StringBuilder();
        Map<Object, Object> headers = (Map<Object, Object>) context.get(headersKey);
        if (headers != null && headers.size() > 0)
        {
            for (Entry<Object, Object> entry : headers.entrySet())
            {
                httpHeaders.append("- " + entry.getKey() + " = " + entry.getValue() + "\n");
            }
        }

        // append the SOAP message
        String soapMessage = DomUtils.prettyPrintNode(context.getMessage().getSOAPPart());

        // append the message context properties
        StringBuilder messageContextProperties = new StringBuilder();
        TreeMap<String, Object> sortedContextProperties = new TreeMap<String, Object>(context);
        for (Entry<String, Object> entry : sortedContextProperties.entrySet())
        {
            messageContextProperties.append("- " + entry.getKey() + " = " + entry.getValue() + "\n");
        }

        // finally log all
        String format = isOutbound ? OUTBOUND_MESSAGE_FORMAT : INBOUND_MESSAGE_FORMAT;
        LOG.debug(String.format(format, httpHeaders, soapMessage, messageContextProperties));
    }

    private void registerMessage(SOAPMessageContext context)
    {
        RequestData reqData = new RequestData(getOperationName(context));
        reqData.setBytesSent(0);

        context.put(XLT_REQUEST_DATA, reqData);
        context.setScope(XLT_REQUEST_DATA, MessageContext.Scope.HANDLER);
    }

    private void setFaultReceived(MessageContext context)
    {
        context.put(XLT_FAULT, true);
    }

    private void unregisterMessage(MessageContext context)
    {
        RequestData reqData = (RequestData) context.get(XLT_REQUEST_DATA);

        if (reqData != null)
        {
            reqData.setRunTime();
            reqData.setFailed(isFaultReceived(context));
            reqData.setBytesReceived(0);
            reqData.setResponseCode(getResponseCode(context));
            reqData.setUrl(getServiceUrl(context));
            reqData.setContentType(getContentType(context));

            Session.getCurrent().getDataManager().logDataRecord(reqData);
        }
    }
}
