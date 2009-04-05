/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.juddi.portlets.client;

import java.util.List;
import java.util.Map;

import org.apache.juddi.portlets.client.model.Business;
import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.service.InquiryResponse;
import org.apache.juddi.portlets.client.service.InquiryService;
import org.apache.juddi.portlets.client.service.InquiryServiceAsync;
import org.apache.juddi.portlets.client.service.PublicationResponse;
import org.apache.juddi.portlets.client.service.PublicationService;
import org.apache.juddi.portlets.client.service.PublicationServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 *  @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDISearch implements EntryPoint, ClickListener {

	Label tokenLabel = new Label("");

	private String token = null;
	private TextBox tmodelKeyBox = new TextBox();
	
	private VerticalPanel tmodelPanel = new VerticalPanel();
	Label tmodelLabel = new Label("");
	private Button getTModelButton = new Button("getTModel");
	private InquiryServiceAsync inquiryService = (InquiryServiceAsync) GWT.create(InquiryService.class);
	
	private FlowPanel searchPanel = new FlowPanel();
	private PublicationServiceAsync publicationService = (PublicationServiceAsync) GWT.create(PublicationService.class);
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() { 
		
		getTModelButton.addClickListener(this);
		Label tmodel = new Label ("TModel Key:");
		tmodel.setStyleName("portlet-form-field-label");
		tmodelPanel.add(tmodel);
		tmodelKeyBox.setStyleName("portlet-form-input-field");
		tmodelPanel.add(tmodelKeyBox);
		getTModelButton.setStyleName(("portlet-form-button"));
		tmodelPanel.add(getTModelButton);
		tmodelLabel.setStyleName("portlet-form-field-label");
		tmodelPanel.add(tmodelLabel);
		
		Label searchQuery = new Label ("Search Query");
		searchQuery.setStyleName("portlet-form-field-label");
		searchPanel.add(searchQuery);
		RootPanel.get("search").add(searchPanel);
		
	}

	public void onClick(Widget sender) {
	    if (sender == getTModelButton) {
			if (token!=null) {
				getTModels(token,tmodelKeyBox.getText());
			}
		} else {
			System.err.println("undefined");
		}
	}
	
	private void getTModels(String token, String tmodelKey) {

		inquiryService.getTModelDetail(token, tmodelKey, new AsyncCallback<InquiryResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(InquiryResponse response) {
				if (response.isSuccess()) {
					Map<String,String> tModelMap= response.getResponse();
					tmodelLabel.setText("tmodelMap: " + tModelMap);
				} else {
					tmodelLabel.setText("error: " + response.getMessage());
				}
			}
		});
	}
	
	private void getBusinesses(String token, String infoSelection) {

		publicationService.getBusinesses(token, infoSelection, new AsyncCallback<PublicationResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(PublicationResponse response) {
				if (response.isSuccess()) {
					List<Business> businesses= response.getBusinesses();
					businesses.add(new Business("Another one"));
					System.out.println("Businesses=" + businesses);
					
					for (Business business : businesses) {
						TreeItem serviceTree = new TreeItem(business.getName());
						for (Service service : business.getServices()) {
							serviceTree.addItem("Service " + service.getName());
						}
						//shipublisherTree.addItem(serviceTree);
					}
					//tmodelLabel.setText("tmodelMap: " + tModelMap);
				} else {
					//tmodelLabel.setText("error: " + response.getMessage());
				}
			}
		});
	}
	
}
