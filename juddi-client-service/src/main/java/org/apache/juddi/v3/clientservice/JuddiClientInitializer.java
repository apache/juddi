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
package org.apache.juddi.v3.clientservice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.jboss.system.ServiceMBeanSupport;

public class JuddiClientInitializer extends ServiceMBeanSupport implements JuddiClientInitializerMBean {

	private final static String DEFAULT_UDDI_CONFIG_FILE="META_INF/uddi.xml";
	
	private Log log = LogFactory.getLog(this.getClass());
	UDDIClerkManager manager = null;
	String nodeName = null;
	private String fileName;
	private Boolean setAsSysParams = Boolean.FALSE;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public Boolean getSetAsSysParams() {
		return setAsSysParams;
	}
	
	public void setSetAsSysParams(Boolean setAsSysParams) {
		this.setAsSysParams = setAsSysParams;
	}
	
	@Override
	protected void startService() throws Exception
	{
		if (fileName==null) {
			fileName = DEFAULT_UDDI_CONFIG_FILE;
		}
		try {
			manager = new UDDIClerkManager(fileName);
			manager.start();
			if (getSetAsSysParams()) {
				System.setProperty("org.apache.juddi.v3.client.manager.name", manager.getName());
				System.setProperty("org.apache.juddi.v3.client.node.name", nodeName);
			}
			super.startService();
		} catch (Exception e) {
			log.error("UDDI Manager could not be started (fileName=" + fileName + " " + e.getMessage(),e);
		}
	}
	
	@Override
	protected void stopService() throws Exception
	{
		if (manager!=null) {
			manager.stop();
		}
		super.stopService();
	}
}
