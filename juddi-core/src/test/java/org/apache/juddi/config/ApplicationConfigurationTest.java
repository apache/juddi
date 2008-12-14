package org.apache.juddi.config;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationConfigurationTest 
{
	@Test
	public void readPropertyFromFile() throws ConfigurationException
	{
		try {
			long refreshDelay = AppConfig.getConfiguration().getLong(Property.JUDDI_CONFIGURATION_RELOAD_DELAY);
			System.out.println(refreshDelay);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void readNonExistingProperty() throws ConfigurationException
	{
		try {
			long nonexisting = AppConfig.getConfiguration().getLong("nonexisting.property",3000l);
			System.out.println(nonexisting);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
