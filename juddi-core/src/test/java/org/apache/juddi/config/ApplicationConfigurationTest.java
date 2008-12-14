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
			Assert.assertEquals(2000, refreshDelay);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void readNonExistingProperty() throws ConfigurationException
	{
		long defaultValue = 3000l;
		try {
			long nonexisting = AppConfig.getConfiguration().getLong("nonexisting.property",defaultValue);
			Assert.assertEquals(3000, nonexisting);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
