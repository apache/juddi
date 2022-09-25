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
package org.apache.juddi.subscription.notify;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.ClassUtil;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.model.TmodelInstanceInfo;
/**
 * The factory figures out which Notifier class to instantiate.
 * 
 * @author kstam
 *
 */
public class NotifierFactory {
	
	Log log = LogFactory.getLog(this.getClass());
	public static final String UDDI_TRANSPORT_KEY = "uddi:uddi.org:transport:";
	
	public Notifier getNotifier(BindingTemplate bindingTemplate) 
		throws URISyntaxException, IllegalArgumentException, SecurityException, 
		InstantiationException, IllegalAccessException, InvocationTargetException, 
		NoSuchMethodException, ClassNotFoundException 
	{
	    String notifierClassName = null;
		for (TmodelInstanceInfo tModelInstanceInfo : bindingTemplate.getTmodelInstanceInfos()) {
			if (tModelInstanceInfo.getTmodelKey().startsWith(UDDI_TRANSPORT_KEY)) {
				log.debug("Found transport tModelKey " + tModelInstanceInfo.getTmodelKey());
				String transport = tModelInstanceInfo.getTmodelKey().substring(UDDI_TRANSPORT_KEY.length(),tModelInstanceInfo.getTmodelKey().length());
				transport = transport.replaceAll("-", "_");
				notifierClassName = "org.apache.juddi.subscription.notify." + transport.toUpperCase() +  "Notifier";
				break;
			}
		}
		if (notifierClassName == null) {
			//JUDDI-496 TODO make sure the tModel is loaded
			log.warn("The bindingTemplate " + bindingTemplate.getEntityKey() + " does not contain a tModel to define its type of transport. Defaulting " 
				  +	"to http.");
                        //JUDDI-596, attempt http transport, even if a transport isn't specified
			notifierClassName =   "org.apache.juddi.subscription.notify.HTTPNotifier";
		}
		if (log.isDebugEnabled()) log.debug("Going find and instantiate notifier class: " + notifierClassName);
		
		@SuppressWarnings("unchecked")
		Class<Notifier> notifierClass = (Class<Notifier>) ClassUtil.forName(notifierClassName,this.getClass());
		Notifier notifier = notifierClass.getConstructor(BindingTemplate.class).newInstance(bindingTemplate);
	    return notifier;
	}
	
}
