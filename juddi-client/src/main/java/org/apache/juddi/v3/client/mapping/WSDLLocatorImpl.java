/*
 * Copyright 2001-2011 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.v3.client.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;

import javax.wsdl.xml.WSDLLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;

/**
 * Implementation of the interface {@link WSDLLocatorImpl}.
 *
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a> Modified for
 * supporting http based credentials by Alex O'Ree
 */
public class WSDLLocatorImpl implements WSDLLocator {

    private final Log log = LogFactory.getLog(this.getClass());
    private InputStream inputStream = null;
    private URI baseURI;
    private String latestImportURI;
    private String username = null, password = null;

    /**
     * Constructor taking the URI to the WSDL. This class implements the
     * {@link WSDLLocatorImpl} Interface.
     *
     * @param baseURI - URI of the WSDL
     */
    public WSDLLocatorImpl(URI baseURI) {
        this.baseURI = baseURI;
    }

    /**
     * Constructor taking the URI to the WSDL. This class implements the
     * {@link WSDLLocatorImpl} Interface and includes support for HTTP
     * Authentication
     *
     * @param baseURI
     * @param username
     * @param password
     * @param domain
     */
    public WSDLLocatorImpl(URI baseURI, String username, String password) {
        this.baseURI = baseURI;
        this.username = username;
        this.password = password;
    }

    /**
     * @see WSDLLocatorImpl.getBaseInputSource
     */
    public InputSource getBaseInputSource() {
        return getImportFromUrl(baseURI.toString());
    }

    /**
     * Internal method to normalize the importUrl. The importLocation can be
     * relative to the parentLocation.
     *
     * @param parentLocation
     * @param importLocation
     * @return
     */
    protected URL constructImportUrl(String parentLocation, String importLocation) {
        URL importUrl = null;
        try {
            URI importLocationURI = new URI(importLocation);
            if (importLocationURI.getScheme() != null || parentLocation == null) {
                importUrl = importLocationURI.toURL();
            } else {
                String parentDir = parentLocation.substring(0, parentLocation.lastIndexOf("/"));
                URI uri = new URI(parentDir + "/" + importLocation);
                importUrl = uri.normalize().toURL();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (importUrl != null) {
            log.debug("importUrl: " + importUrl.toExternalForm());
        } else {
            log.error("importUrl is null!");
        }
        return importUrl;
    }

    private InputSource getImportFromUrl(String url) {
        InputSource inputSource = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            URL url2 = new URL(url);
            if (!url.toLowerCase().startsWith("http")) {
                return getImportFromFile(url);
            }

            if (username != null && username.length() > 0
                    && password != null && password.length() > 0) {
                int port = 80;
                if (url.toLowerCase().startsWith("https://")) {
                    port = 443;
                }

                if (url2.getPort() > 0) {
                    port = url2.getPort();
                }

                httpclient.getCredentialsProvider().setCredentials(
                        new AuthScope(url2.getHost(), port),
                        new UsernamePasswordCredentials(username, password));
            }
            HttpGet httpGet = new HttpGet(url);
            try {

                HttpResponse response1 = httpclient.execute(httpGet);
                //System.out.println(response1.getStatusLine());
                // HttpEntity entity1 = response1.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String handleResponse = responseHandler.handleResponse(response1);
                StringReader sr = new StringReader(handleResponse);
                inputSource = new InputSource(sr);


            } finally {
                httpGet.releaseConnection();

            }

            //  InputStream inputStream = importUrl.openStream();
            //inputSource = new InputSource(inputStream);
            latestImportURI = url;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return inputSource;
    }

    /**
     * @see WSDLLocatorImpl.getImportInputSource
     */
    public InputSource getImportInputSource(String parentLocation, String importLocation) {
        InputSource inputSource = null;
        try {
            URL importUrl = constructImportUrl(parentLocation, importLocation);
            return getImportFromUrl(importUrl.toExternalForm());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return inputSource;
    }

    /**
     * @see WSDLLocatorImpl.getBaseURI
     */
    public String getBaseURI() {
        String baseURIStr = null;
        try {
            baseURIStr = baseURI.toURL().toExternalForm();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return baseURIStr;
    }

    /**
     * @see WSDLLocatorImpl.getLatestImportURI
     */
    public String getLatestImportURI() {
        return latestImportURI;
    }

    /**
     * @see WSDLLocatorImpl.close
     */
    public void close() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private InputSource getImportFromFile(String url) {
        InputSource inputSource = null;
        try {
            URL importUrl = new URL(url);
            inputStream = importUrl.openStream();
            inputSource = new InputSource(inputStream);
            latestImportURI = importUrl.toExternalForm();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return inputSource;
    }
}
