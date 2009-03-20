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

import java.util.Map;

import org.apache.juddi.portlets.client.SecurityService;
import org.apache.juddi.portlets.client.SecurityServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 *  @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class Application implements EntryPoint, ClickListener {

	Label tokenLabel = new Label("");
	Label tmodelLabel = new Label("");
	private FlowPanel loginPanel = new FlowPanel();
	private VerticalPanel browsePanel = new VerticalPanel();
	private Button getTokenButton = new Button("Login");
	private Button getTModelButton = new Button("getTModel");
	private TextBox usernameBox = new TextBox();
	private PasswordTextBox passwordBox = new PasswordTextBox();
	private String token = null;
	private TextBox tmodelKeyBox = new TextBox();
	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);
	private InquiryServiceAsync inquiryService = (InquiryServiceAsync) GWT.create(InquiryService.class);
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() { 
		loginPanel.getElement().setId("browser-body");
		Label publisher = new Label ("Publisher:");
		publisher.setStyleName("portlet-form-field-label");
		loginPanel.add(publisher);
		usernameBox.setStyleName("portlet-form-input-field");
		loginPanel.add(usernameBox);
		Label password = new Label ("Password:");
		password.setStyleName("portlet-form-field-label");
		loginPanel.add(password);
		passwordBox.setStyleName("portlet-form-input-field");
		loginPanel.add(passwordBox);
		getTokenButton.addClickListener(this);
		getTokenButton.setStyleName(("portlet-form-button"));
		loginPanel.add(getTokenButton);
		
		RootPanel.get("browser").add(loginPanel);
		
		getTModelButton.addClickListener(this);
		Label tmodel = new Label ("TModel Key:");
		tmodel.setStyleName("portlet-form-field-label");
		browsePanel.add(tmodel);
		tmodelKeyBox.setStyleName("portlet-form-input-field");
		browsePanel.add(tmodelKeyBox);
		getTModelButton.setStyleName(("portlet-form-button"));
		browsePanel.add(getTModelButton);
		tmodelLabel.setStyleName("portlet-form-field-label");
		browsePanel.add(tmodelLabel);
		
		RootPanel.get("token").add(tokenLabel);
	}

	public void onClick(Widget sender) {
		if (sender == getTokenButton) {
			String user =usernameBox.getText();
			String pw   =passwordBox.getText();
			getToken(user, pw);
		} else if (sender == getTModelButton) {
			if (token!=null) {
				getTModels(token,tmodelKeyBox.getText());
			}
		} else {
			System.err.println("undefined");
		}
	}

	private void getToken(String user, String password) {

		securityService.get(user, password, new AsyncCallback<SecurityResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(SecurityResponse response) {
				if (response.isSuccess) {
					RootPanel.get("browser").clear();
					RootPanel.get("browser").add(browsePanel);
					token = response.getResponse();
					tokenLabel.setText("token: " + token);
					
				} else {
					tokenLabel.setText("error: " + response.getMessage());
				}
			}
		});
	}
	
	private void getTModels(String token, String tmodelKey) {

		inquiryService.getTModelDetail(token, tmodelKey, new AsyncCallback<InquiryResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(InquiryResponse response) {
				if (response.isSuccess) {
					Map<String,String> tModelMap= response.getResponse();
					tmodelLabel.setText("tmodelMap: " + tModelMap);
				} else {
					tmodelLabel.setText("error: " + response.getMessage());
				}
			}
		});
	}
	
}
