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
 */
package org.apache.juddi.v3.client.mapping;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class URLLocalizerDefaultImpl implements URLLocalizer {

	URL baseUrl;
	
	public URLLocalizerDefaultImpl() {
		super();
	}
	public URLLocalizerDefaultImpl(URL baseUrl) {
		super();
		this.baseUrl = baseUrl;
	}

	private Log log = LogFactory.getLog(this.getClass());
	
	public String rewrite (URL urlIn) {
		return rewriteURL(urlIn).toExternalForm();
	}
	
	public String rewriteToWSDLURL (URL urlIn) {
		URL url = rewriteURL(urlIn);
		return url.toExternalForm() + "?wsdl";
	}
	
	public URL rewriteURL(URL urlIn) {
		URL url = null;
		if (baseUrl!=null && url == null) {
			try {
				url = new URL(baseUrl.getProtocol(),
							baseUrl.getHost(),
							baseUrl.getPort(),
							urlIn.getPath());
			} catch (MalformedURLException e) {
				log.error(e.getMessage(),e);
			}
		} else {
			url = urlIn;
		}
		return url;
	}

	public void setBaseUrl(URL baseUrl) {
		this.baseUrl = baseUrl;
	}


}
