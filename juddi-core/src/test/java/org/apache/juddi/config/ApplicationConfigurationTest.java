package org.apache.juddi.config;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

public class ApplicationConfigurationTest 
{
	@Test
	public void readPropertyFromFile() throws ConfigurationException
	{
		long refreshDelay = AppConfig.getConfiguration().getLong(Property.JUDDI_CONFIGURATION_RELOAD_DELAY);
		System.out.println(refreshDelay);
	}
	
	@Test
	public void readNonExistingProperty() throws ConfigurationException
	{
		long nonexisting = AppConfig.getConfiguration().getLong("nonexisting.property",3000l);
		System.out.println(nonexisting);
	}
}
