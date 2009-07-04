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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 *  @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class JUDDIPublisher implements Application, EntryPoint, ClickListener {

	private FlowPanel flowPanel = new FlowPanel();
	private String token = null;
	private LoginPanel loginPanel = null;
	private PublisherListPanel applicationPanel = null;
	
	private String username;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() { 
		
		loginPanel = new LoginPanel(this);
		loginPanel.setVisible(false);
		flowPanel.add(loginPanel);
		
		applicationPanel = new PublisherListPanel(this);
		applicationPanel.setVisible(false);
		flowPanel.add(applicationPanel);
		
		RootPanel.get("publisher").add(flowPanel);
		loginPanel.getToken(null,null);
	}

	public void onClick(Widget sender) {
//	    if (sender == getTModelButton) {
//			if (token!=null) {
//				getTModels(token,tmodelKeyBox.getText());
//			}
//		} else {
//			System.err.println("undefined");
//		}
	}

	public ApplicationPanel getApplicationPanel() {
		return applicationPanel;
	}

	public LoginPanel getLoginPanel() {
		return loginPanel;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
}
