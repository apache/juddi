/*
 * Copyright 2001-2008 The Apache Software Foundation.
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

package org.apache.juddi.keygen;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * The default jUDDI key generator.  Generates a key like this:
 * 
 * uddiScheme : domain : UUID
 * 
 * where domain is the shortest keyGenerator domain. 
 * 
 * If no domain is set for this publihser, or the publisher is 'uddi' then
 * it defaults to the RootDomain:
 * 
 * uddiScheme : RootDomain : UUID
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class DefaultKeyGenerator implements KeyGenerator {

	public String generate(UddiEntityPublisher publisher) throws DispositionReportFaultMessage {
		String domain = getDomain(publisher);
		if (domain==null) {
			String rootPartition = "";
			try 
			{ rootPartition = AppConfig.getConfiguration().getString(Property.JUDDI_ROOT_PARTITION); }
			catch(ConfigurationException ce) 
			{ throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval", Property.JUDDI_ROOT_PARTITION));}
			domain = rootPartition;
		}
		return domain + PARTITION_SEPARATOR + UUID.randomUUID();
	}
	
	public static String getDomain(UddiEntityPublisher publisher) {
		String domain = null;
		if (publisher==null || "uddi".equalsIgnoreCase(publisher.getAuthorizedName())) return domain; //default to the rootPartition
		List<String> domains = publisher.getKeyGeneratorKeys();
		if (domains!=null && !domains.isEmpty()) {
			Iterator<String> iter = domains.iterator();
			int partsMax = 1000;
			//pick the KeyGenerator with the fewest amount of parts, ignoring the subdomain keys. 
			while (iter.hasNext()) {
				String thisDomain = iter.next();
				String[] parts = thisDomain.split(":");
				if ((domain == null || (2 <= parts.length && parts.length < partsMax)) && thisDomain.length() + 37 < 255) {
					partsMax = parts.length;
					domain = thisDomain;
				}
			}
			domain = domain.substring(0, domain.lastIndexOf(":"));
		}
		return domain;
	}

}