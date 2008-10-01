package org.apache.juddi.test;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.testng.annotations.Test;

public class ApplicationConfigurationTest 
{
	@Test
	public void readPropertyFromFile() throws ConfigurationException
	{
		long refreshDelay = AppConfig.getConfiguration().getLong(Property.JUDDI_RELOAD_DELAY);
		System.out.println(refreshDelay);
	}
	
	@Test
	public void readNonExistingProperty() throws ConfigurationException
	{
		long nonexisting = AppConfig.getConfiguration().getLong("nonexisting.property",3000l);
		System.out.println(nonexisting);
	}
}
