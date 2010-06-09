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

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.apache.juddi.jaxb.EntityCreator;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckTModel 
{
	final static String JOE_PUBLISHER_TMODEL_XML      = "uddi_data/joepublisher/tModelKeyGen.xml";
    final static String JOE_PUBLISHER_TMODEL_KEY      = "uddi:uddi.joepublisher.com:keygenerator";
    final static String MARY_PUBLISHER_TMODEL_XML     = "uddi_data/marypublisher/tModelKeyGen.xml";
    final static String MARY_PUBLISHER_TMODEL_KEY     = "uddi:uddi.marypublisher.com:keygenerator";
    final static String SAM_SYNDICATOR_TMODEL_XML     = "uddi_data/samsyndicator/tModelKeyGen.xml";
    final static String SAM_SYNDICATOR_TMODEL_KEY     = "uddi:www.samco.com:keygenerator";
 
    
	private Logger logger = Logger.getLogger(this.getClass());
	private UDDIPublicationPortType publication = null;
    private UDDIInquiryPortType inquiry = null;
	
	public TckTModel(UDDIPublicationPortType publication, 
				  UDDIInquiryPortType inquiry) {
		super();
		this.publication = publication;
		this.inquiry = inquiry;
	}

	public void saveTModel(String authInfo, String tModelXml, String tModelKey) {
		
		// Add the tModel
		try {
			SaveTModel st = new SaveTModel();
			st.setAuthInfo(authInfo);

			org.uddi.api_v3.TModel tmIn = (org.uddi.api_v3.TModel)EntityCreator.buildFromDoc(tModelXml, "org.uddi.api_v3");
			st.getTModel().add(tmIn);
			publication.saveTModel(st);
	
			// Now get the entity and check the values
			GetTModelDetail gt = new GetTModelDetail();
			gt.getTModelKey().add(tModelKey);
			TModelDetail td = inquiry.getTModelDetail(gt);
			List<org.uddi.api_v3.TModel> tmOutList = td.getTModel();
			org.uddi.api_v3.TModel tmOut = tmOutList.get(0);

			assertEquals(tmIn.getTModelKey(), tmOut.getTModelKey());
			assertEquals(tmIn.getName().getLang(), tmOut.getName().getLang());
			assertEquals(tmIn.getName().getValue(), tmOut.getName().getValue());
			TckValidator.checkDescriptions(tmIn.getDescription(), tmOut.getDescription());
			TckValidator.checkCategories(tmIn.getCategoryBag(), tmOut.getCategoryBag());
			for (OverviewDoc overviewDoc : tmIn.getOverviewDoc()) {
				TckValidator.checkOverviewDocs(overviewDoc, tmOut.getOverviewDoc());
			}
			
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}
	}
	
	public void deleteTModel(String authInfo, String tModelXml, String tModelKey) {
		
		try {
			//Now deleting the TModel
			// Delete the entity and make sure it is removed
			DeleteTModel dt = new DeleteTModel();
			dt.setAuthInfo(authInfo);
			
			dt.getTModelKey().add(tModelKey);
			publication.deleteTModel(dt);
			
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}
	}
	
	public void saveJoePublisherTmodel(String authInfoJoe) {
		saveTModel(authInfoJoe, JOE_PUBLISHER_TMODEL_XML, JOE_PUBLISHER_TMODEL_KEY);
	}
	
	public void deleteJoePublisherTmodel(String authInfoJoe) {
		deleteTModel(authInfoJoe, JOE_PUBLISHER_TMODEL_XML, JOE_PUBLISHER_TMODEL_KEY);
	}
	
	public void saveMaryPublisherTmodel(String authInfoMary) {
		saveTModel(authInfoMary, MARY_PUBLISHER_TMODEL_XML, MARY_PUBLISHER_TMODEL_KEY);
	}
	
	public void deleteMaryPublisherTmodel(String authInfoMary) {
		deleteTModel(authInfoMary, MARY_PUBLISHER_TMODEL_XML, MARY_PUBLISHER_TMODEL_KEY);
	}
	
	public void saveSamSyndicatorTmodel(String authInfoSam) {
		saveTModel(authInfoSam, SAM_SYNDICATOR_TMODEL_XML, SAM_SYNDICATOR_TMODEL_KEY);
	}
	
	public void deleteSamSyndicatorTmodel(String authInfoSam) {
		deleteTModel(authInfoSam, SAM_SYNDICATOR_TMODEL_XML, SAM_SYNDICATOR_TMODEL_KEY);
	}
}