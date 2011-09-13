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

import org.apache.juddi.portlets.client.service.SecurityResponse;
import org.apache.juddi.portlets.client.service.SecurityService;
import org.apache.juddi.portlets.client.service.SecurityServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

public class LoginPanel extends FlowPanel {
	
	//UI Widgets
	private Button tokenButton = new Button("Login");
	private TextBox usernameBox = new TextBox();
	private PasswordTextBox passwordBox = new PasswordTextBox();
	
	private String publisherId;
	private Login application;
	private String token;
	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);
	
	public LoginPanel(Login application) {
		super();
		this.application = application;
		getToken(null, null);
		getElement().setId("parentApp-body");
		
		Label publisher = new Label ("Publisher:");
		publisher.setStyleName("portlet-form-field-label");
		add(publisher);
		usernameBox.setStyleName("portlet-form-input-field");
		add(usernameBox);
		
		Label password = new Label ("Password:");
		password.setStyleName("portlet-form-field-label");
		add(password);
		passwordBox.setStyleName("portlet-form-input-field");
		add(passwordBox);
		
		tokenButton.addClickHandler(new ClickHandler() {
	          public void onClick(ClickEvent event) {
	        	  getToken(usernameBox.getText(), passwordBox.getText());
	          }
	      });
		tokenButton.setStyleName(("portlet-form-button"));
		add(tokenButton); 
	}
	
	/**
	 * Obtains an authenticationToken
	 * @param user
	 * @param password
	 */
	protected void getToken(String user, String password) {
		securityService.get(user, password, new AsyncCallback<SecurityResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.Location.reload();
			}

			public void onSuccess(SecurityResponse response) {
				if (response.isSuccess()) {
					token = response.getResponse();
					publisherId = response.getUsername();
					application.login();
				} else {
					application.login();
					//Window.alert("error: " + response.getMessage());
				}
			}
		}); 
	}
	
	/**
	 * Obtains an authenticationToken
	 * @param user
	 * @param password
	 */
	protected void destroyToken(String user, String password) {
		securityService.logout(new AsyncCallback<SecurityResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage());
			}

			public void onSuccess(SecurityResponse response) {
				token = null;
				publisherId = null;
				application.logout();
			}
		}); 
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getPublisherId() {
		return publisherId;
	}
	
}
