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

import org.apache.juddi.v3.client.UDDIConstants;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_040_BusinessServiceLoadIntegrationTest extends UDDI_040_BusinessServiceIntegrationTest
{	
	int numberOfServices=1100;
	@Test @Override
	public void joepublisher() throws Exception{
          Assume.assumeTrue(TckPublisher.isEnabled());
                logger.info("UDDI_040_BusinessServiceLoadIntegrationTest joepublisher Servoce Load test " + numberOfServices);
                Assume.assumeTrue(TckPublisher.isLoadTest());
                numberOfServices = TckPublisher.getMaxLoadServices();
		tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
		tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
		long startSave = System.currentTimeMillis();
		tckBusinessServiceJoe.saveJoePublisherServices(authInfoJoe, 0, numberOfServices);
		long saveDuration = System.currentTimeMillis() - startSave;
		logger.info("****************** Save " + numberOfServices + " Joes Services Duration=" + saveDuration);
		
                long startFind=System.currentTimeMillis();
                FindService fs = new FindService();
                fs.setAuthInfo(authInfoJoe);
                fs.getName().add(new Name(UDDIConstants.WILDCARD, null));
                fs.setFindQualifiers(new FindQualifiers());
                fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                inquiryJoe.findService(fs);
                long endFind = System.currentTimeMillis() - startFind;
                logger.info("****************** Find " + numberOfServices + " Joes Services Duration= " + endFind);
                
                long startDelete = System.currentTimeMillis();
		tckBusinessServiceJoe.deleteJoePublisherServices(authInfoJoe, 0, numberOfServices);
		long deleteDuration = System.currentTimeMillis() - startDelete;
		logger.info("****************** Delete " + numberOfServices + " Joes Services Duration= " + deleteDuration);
		tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
		tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                
                Assert.assertTrue("That took way too long at " + endFind, endFind < (5*60*1000));
	}
	
	@Test @Override
	public void samsyndicator() throws Exception {
          Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isLoadTest());
                logger.info("UDDI_040_BusinessServiceLoadIntegrationTest samsyndicator Servoce Load test " + numberOfServices);
                numberOfServices = TckPublisher.getMaxLoadServices();
		tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
		tckBusinessSam.saveSamSyndicatorBusiness(authInfoSam);
		long startSave = System.currentTimeMillis();
		tckBusinessServiceSam.saveSamSyndicatorServices(authInfoSam, 0, numberOfServices);
		long saveDuration = System.currentTimeMillis() - startSave;
		logger.info("****************** Save " + numberOfServices + " Sams Services Duration=" + saveDuration);
                
                long startFind=System.currentTimeMillis();
                FindService fs = new FindService();
                fs.setAuthInfo(authInfoSam);
                fs.getName().add(new Name(UDDIConstants.WILDCARD, null));
                fs.setFindQualifiers(new FindQualifiers());
                fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                inquirySam.findService(fs);
                long endFind = System.currentTimeMillis() - startFind;
                logger.info("****************** Find " + numberOfServices + " Sams Services Duration= " + endFind);
                
		long startDelete = System.currentTimeMillis();
		tckBusinessServiceSam.deleteSamSyndicatorServices(authInfoSam, 0, numberOfServices);
		long deleteDuration = System.currentTimeMillis() - startDelete;
		logger.info("****************** Delete " + numberOfServices + " Sams Services Duration= " + deleteDuration);
		tckBusinessSam.deleteSamSyndicatorBusiness(authInfoSam);
		tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                Assert.assertTrue("That took way too long at " + endFind, endFind < (5*60*1000));
	}
	
}
