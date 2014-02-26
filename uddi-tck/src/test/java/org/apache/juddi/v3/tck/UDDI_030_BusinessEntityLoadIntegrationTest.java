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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.UDDIConstants;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.Name;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_030_BusinessEntityLoadIntegrationTest extends UDDI_030_BusinessEntityIntegrationTest {

        int numberOfBusinesses = 1100;

        @BeforeClass
        public static void setup() throws ConfigurationException {
             if (!TckPublisher.isEnabled()) return;
                UDDI_030_BusinessEntityIntegrationTest.startManager();
        }

        @Test
        @Override
        public void testJoePublisherBusinessEntity() throws Exception {
             if (!TckPublisher.isEnabled()) return;
                Assume.assumeTrue(TckPublisher.isLoadTest());

                numberOfBusinesses = TckPublisher.getMaxLoadServices();
                logger.info("UDDI_030_BusinessEntityLoadIntegrationTest testJoePublisherBusinessEntity Load test " + numberOfBusinesses);
                tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                long startSave = System.currentTimeMillis();
                tckBusinessJoe.saveJoePublisherBusinesses(authInfoJoe, numberOfBusinesses);
                long saveDuration = System.currentTimeMillis() - startSave;
                System.out.println("****************** Save " + numberOfBusinesses + " Joes Duration=" + saveDuration);

                long startFind = System.currentTimeMillis();
                FindBusiness fs = new FindBusiness();
                fs.setAuthInfo(authInfoJoe);
                fs.getName().add(new Name(UDDIConstants.WILDCARD, null));
                fs.setFindQualifiers(new FindQualifiers());
                fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                inquiryJoe.findBusiness(fs);
                long endFind = System.currentTimeMillis() - startFind;
                logger.info("****************** Find " + numberOfBusinesses + " Joes Business Duration= " + endFind);

                long startDelete = System.currentTimeMillis();
                tckBusinessJoe.deleteJoePublisherBusinesses(authInfoJoe, numberOfBusinesses);
                long deleteDuration = System.currentTimeMillis() - startDelete;
                logger.info("****************** Delete " + numberOfBusinesses + " Joes Duration= " + deleteDuration);
                tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                Assert.assertTrue("That took way too long at " + endFind, endFind < (5*60*1000));
        }

        @Test
        @Override
        public void testSamSyndicatorBusiness() throws Exception {
             Assume.assumeTrue(TckPublisher.isEnabled());
                Assume.assumeTrue(TckPublisher.isLoadTest());
                logger.info("UDDI_030_BusinessEntityLoadIntegrationTest testSamSyndicatorBusiness Load test " + numberOfBusinesses);
                numberOfBusinesses = TckPublisher.getMaxLoadServices();
                tckTModelSam.saveSamSyndicatorTmodel(authInfoSam);
                long startSave = System.currentTimeMillis();
                tckBusinessSam.saveSamSyndicatorBusinesses(authInfoSam, numberOfBusinesses);
                long saveDuration = System.currentTimeMillis() - startSave;
                logger.info("****************** Save " + numberOfBusinesses + " Sams Duration=" + saveDuration);

                long startFind = System.currentTimeMillis();
                FindBusiness fs = new FindBusiness();
                fs.setAuthInfo(authInfoSam);
                fs.getName().add(new Name(UDDIConstants.WILDCARD, null));
                fs.setFindQualifiers(new FindQualifiers());
                fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                inquirySam.findBusiness(fs);
                long endFind = System.currentTimeMillis() - startFind;
                logger.info("****************** Find " + numberOfBusinesses + " Sams Business Duration= " + endFind);




                long startDelete = System.currentTimeMillis();
                tckBusinessSam.deleteSamSyndicatorBusinesses(authInfoSam, numberOfBusinesses);
                long deleteDuration = System.currentTimeMillis() - startDelete;
                logger.info("****************** Delete " + numberOfBusinesses + " Sams Duration= " + deleteDuration);
                tckTModelSam.deleteSamSyndicatorTmodel(authInfoSam);
                Assert.assertTrue("That took way too long at " + endFind, endFind < (5*60*1000));
        }
}
