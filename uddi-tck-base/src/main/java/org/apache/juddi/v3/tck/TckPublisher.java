/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.tck;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckPublisher 
{	
	private static Properties tckProperties = new Properties();
	public final static String JOE_PUBLISHER_XML    = "uddi_data/joepublisher/publisher.xml";
	public final static String TMODEL_PUBLISHER_XML = "uddi_data/tmodels/publisher.xml";
	public final static String SAM_SYNDICATOR_XML   = "uddi_data/samsyndicator/publisher.xml";
    public final static String MARY_PUBLISHER_XML   = "uddi_data/marypublisher/publisher.xml";
    
	static {
		try {
			InputStream inputSteam = TckPublisher.class.getResourceAsStream("/tck.properties");
			tckProperties.load(inputSteam);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

  
    public final static String getRootPublisherId() {
    	return tckProperties.getProperty(Property.ROOT_PUBLISHER);
    }
    
    public final static String getRootPassword() {
    	return tckProperties.getProperty(Property.ROOT_PASSWORD);
    }
    
    public final static String getUDDIPublisherId() {
    	return tckProperties.getProperty(Property.UDDI_PUBLISHER);
    }
    
    public final static String getUDDIPassword() {
    	return tckProperties.getProperty(Property.UDDI_PASSWORD);
    }
    
    public final static String getJoePublisherId() {
    	return tckProperties.getProperty(Property.JOE_PUBLISHER);
    }
    
    public final static String getJoePassword() {
    	return tckProperties.getProperty(Property.JOE_PASSWORD);
    }
    
    public final static String getTModelPublisherId() {
    	return tckProperties.getProperty(Property.TMODEL_PUBLISHER);
    }
    
    public final static String getTModelPassword() {
    	return tckProperties.getProperty(Property.TMODEL_PASSWORD);
    }
    
    public final static String getSamPublisherId() {
    	return tckProperties.getProperty(Property.SAM_PUBLISHER);
    }
    
    public final static String getSamPassword() {
    	return tckProperties.getProperty(Property.SAM_PASSWORD);
    }
    
    public final static String getMaryPublisherId() {
    	return tckProperties.getProperty(Property.MARY_PUBLISHER);
    }
    
    public final static String getMaryPassword() {
    	return tckProperties.getProperty(Property.MARY_PASSWORD);
    }
    
    public final static String getRiftSawPublisherId() {
    	return tckProperties.getProperty(Property.RIFTSAW_PUBLISHER);
    }
    
    public final static String getRiftSawPassword() {
    	return tckProperties.getProperty(Property.RIFTSAW_PASSWORD);
    }
}