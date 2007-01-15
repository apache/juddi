/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.juddi.registry.rmi;

import java.util.Properties;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public class JNDIRegistration
{
	/** Instance of the InqueryService, so we have a running instance we 
	 *  can remotely attach to it later. */
	public static Inquiry mInquery=null;
	/** Instance of the PublishService, so we have a running instance we
	 *  can remotely attach to it later. */
	public static Publish mPublish=null;
	/** Name of the inquiry service in JNDI */
	public static String INQUIRY_SERVICE="/InquiryService";
	/** Name of the publish service in JNDI */
	public static String PUBLISH_SERVICE="/PublishService";
	/** Logger */
	private static Log log = LogFactory.getLog(JNDIRegistration.class);
	/**
	 * Registers the Publish and Inquiry Services to JNDI and instantiates a
	 * instance of each so we can remotely attach to it later.
	 */
	public static void register()
	{
		try {
			//Getting the JNDI setting from the config
			String factoryInitial = Config.getStringProperty(
					  RegistryEngine.PROPNAME_JAVA_NAMING_FACTORY_INITIAL,
					  RegistryEngine.DEFAULT_JAVA_NAMING_FACTORY_INITIAL);
			String providerURL = Config.getStringProperty(
					  RegistryEngine.PROPNAME_JAVA_NAMING_PROVIDER_URL,
					  RegistryEngine.DEFAULT_JAVA_NAMING_PROVIDER_URL);
			String factoryURLPkgs = Config.getStringProperty(
					  RegistryEngine.PROPNAME_JAVA_NAMING_FACTORY_URL_PKGS,
					  RegistryEngine.DEFAULT_JAVA_NAMING_FACTORY_URL_PKGS);
			//Setting them in the properties for the Initial Context 
			Properties env = new Properties();
			env.setProperty(RegistryEngine.PROPNAME_JAVA_NAMING_FACTORY_INITIAL, factoryInitial);
			env.setProperty(RegistryEngine.PROPNAME_JAVA_NAMING_PROVIDER_URL, providerURL);
			env.setProperty(RegistryEngine.PROPNAME_JAVA_NAMING_FACTORY_URL_PKGS, factoryURLPkgs); 
			if (log.isDebugEnabled()) {
			log.debug("Creating Initial Context using: \n" 
				+ RegistryEngine.PROPNAME_JAVA_NAMING_FACTORY_INITIAL + "=" + factoryInitial + "\n"
				+ RegistryEngine.PROPNAME_JAVA_NAMING_PROVIDER_URL    + "=" + providerURL + "\n"
				+ RegistryEngine.PROPNAME_JAVA_NAMING_FACTORY_URL_PKGS + "=" + factoryURLPkgs + "\n");
			}
			InitialContext context = new InitialContext(env);
			Inquiry inquiry = new InquiryService();
			if (log.isDebugEnabled()) {
				log.debug("Setting " + INQUIRY_SERVICE + ", " + inquiry.getClass().getName());
			}
			mInquery = inquiry;
			context.bind(INQUIRY_SERVICE, inquiry);
			Publish publish = new PublishService();
			if (log.isDebugEnabled()) {
				log.debug("Setting " + PUBLISH_SERVICE + ", " + publish.getClass().getName());
			}
			mPublish = publish;
			context.bind(PUBLISH_SERVICE, publish);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
