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

import org.apache.juddi.portlets.client.service.InquiryService;
import org.apache.juddi.portlets.client.service.InquiryServiceAsync;
import org.apache.juddi.portlets.client.service.SearchResponse;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 *  @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDISearch implements EntryPoint, ClickListener {

	Label tokenLabel = new Label("");
	
    private TextArea queryTextArea = new TextArea();
    private TextArea resultTextArea = new TextArea();
	
	private Button searchButton = new Button("Search");
	
	private InquiryServiceAsync inquiryService = (InquiryServiceAsync) GWT.create(InquiryService.class);
	private FlowPanel searchPanel = new FlowPanel();
	
	private final static String DEFAULT_QUERY = "<find_tModel xmlns=\"urn:uddi-org:api_v3\">"
					+ "<name>uddi-org:v3_policy</name>"
					+ "</find_tModel>";
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() { 
				
		Label searchQuery = new Label ("Search Query");
		searchQuery.setStyleName("portlet-form-field-label");
		searchPanel.add(searchQuery);
		
	    queryTextArea.setCharacterWidth(55);
	    queryTextArea.setVisibleLines(10);
	    queryTextArea.setText(DEFAULT_QUERY);
	    searchPanel.add(queryTextArea);

		Label spacerLabel = new Label ("Query Result");
		spacerLabel.setStyleName("portlet-form-field-label");
		searchPanel.add(spacerLabel);
	    
	    resultTextArea.setCharacterWidth(55);
	    resultTextArea.setVisibleLines(10);
	    searchPanel.add(resultTextArea);
		searchButton.addClickListener(this);
		searchButton.setStyleName("portlet-form-button");

	    searchPanel.add(searchButton);	    
		
		RootPanel.get("search").add(searchPanel);
		
	}

	public void onClick(Widget sender) {
	    if (sender == searchButton) {
			queryJUDDI(queryTextArea.getText());
		} else {
			System.err.println("undefined");
		}
	}
	
	private void queryJUDDI(String query) {
		inquiryService.queryJUDDI(query, new AsyncCallback<SearchResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Error:" + caught.getMessage());
			}

			public void onSuccess(SearchResponse response) {
				if (response.isSuccess()) {
					//Map<String,String> tModelMap= response.getResponse();
					//tmodelLabel.setText("tmodelMap: " + tModelMap);
					resultTextArea.setText(response.getMessage());
				} else {
					resultTextArea.setText(response.getMessage());
				}
			}
		});
	}
	
}
