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
package org.apache.juddi.v3.client;

import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDI_040_BusinessServiceLoadTest extends UDDI_040_BusinessServiceIntegrationTest
{	
	int numberOfServices=1100;
	@Test @Override
	public void joepublisher() {
		tckTModel.saveJoePublisherTmodel(authInfoJoe);
		tckBusiness.saveJoePublisherBusiness(authInfoJoe);
		long startSave = System.currentTimeMillis();
		tckBusinessService.saveJoePublisherServices(authInfoJoe, numberOfServices);
		long saveDuration = System.currentTimeMillis() - startSave;
		System.out.println("Save " + numberOfServices + " Joes Services Duration=" + saveDuration);
		long startDelete = System.currentTimeMillis();
		tckBusinessService.deleteJoePublisherServices(authInfoJoe, numberOfServices);
		long deleteDuration = System.currentTimeMillis() - startDelete;
		System.out.println("Delete " + numberOfServices + " Joes Services Duration= " + deleteDuration);
		tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
		tckTModel.deleteJoePublisherTmodel(authInfoJoe);
	}
	
	@Test @Override
	public void samsyndicator() {
		tckTModel.saveSamSyndicatorTmodel(authInfoSam);
		tckBusiness.saveSamSyndicatorBusiness(authInfoSam);
		long startSave = System.currentTimeMillis();
		tckBusinessService.saveSamSyndicatorServices(authInfoSam, numberOfServices);
		long saveDuration = System.currentTimeMillis() - startSave;
		System.out.println("Save " + numberOfServices + " Sams Services Duration=" + saveDuration);
		long startDelete = System.currentTimeMillis();
		tckBusinessService.deleteSamSyndicatorServices(authInfoSam, numberOfServices);
		long deleteDuration = System.currentTimeMillis() - startDelete;
		System.out.println("Delete " + numberOfServices + " Sams Services Duration= " + deleteDuration);
		tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
		tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
	}
	
}
