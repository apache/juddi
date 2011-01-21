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
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDI_030_BusinessEntityLoadTest extends UDDI_030_BusinessEntityIntegrationTest {
	
	int numberOfBusinesses=1100;
	
	@BeforeClass
	public static void setup() throws ConfigurationException {
		UDDI_030_BusinessEntityIntegrationTest.startManager();
	}
	
	@Test @Override
	public void testJoePublisherBusinessEntity() {
		tckTModel.saveJoePublisherTmodel(authInfoJoe);
		long startSave = System.currentTimeMillis();
		tckBusiness.saveJoePublisherBusinesses(authInfoJoe, numberOfBusinesses);
		long saveDuration = System.currentTimeMillis() - startSave;
		System.out.println("Save " + numberOfBusinesses + " Joes Duration=" + saveDuration);
		long startDelete = System.currentTimeMillis();
		tckBusiness.deleteJoePublisherBusinesses(authInfoJoe, numberOfBusinesses);
		long deleteDuration = System.currentTimeMillis() - startDelete;
	    System.out.println("Delete " + numberOfBusinesses + " Joes Duration= " + deleteDuration);
	    tckTModel.deleteJoePublisherTmodel(authInfoJoe);
	}
	
	@Test @Override
	public void testSamSyndicatorBusiness() {
		tckTModel.saveSamSyndicatorTmodel(authInfoSam);
		long startSave = System.currentTimeMillis();
		tckBusiness.saveSamSyndicatorBusinesses(authInfoSam, numberOfBusinesses);
		long saveDuration = System.currentTimeMillis() - startSave;
		System.out.println("Save " + numberOfBusinesses + " Sams Duration=" + saveDuration);
		long startDelete = System.currentTimeMillis();
		tckBusiness.deleteSamSyndicatorBusinesses(authInfoSam, numberOfBusinesses);
		long deleteDuration = System.currentTimeMillis() - startDelete;
	    System.out.println("Delete " + numberOfBusinesses + " Sams Duration= " + deleteDuration);
	    tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
	}
	
}
