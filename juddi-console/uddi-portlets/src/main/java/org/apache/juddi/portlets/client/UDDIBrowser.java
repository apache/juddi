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
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 *  @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDIBrowser implements EntryPoint, Login {

	DockPanel dockPanel = new DockPanel();
	public static final Images images = (Images) GWT.create(Images.class);
	private BusinessTreePanel applicationPanel = null;
	private DetailPanel detailPanel = null;
	private LoginPanel loginPanel = null;
	private String token = null;
	private String username;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		loginPanel = new LoginPanel(this);
		loginPanel.setVisible(false);
		dockPanel.add(loginPanel, DockPanel.NORTH);
		
		applicationPanel = new BusinessTreePanel(this);
		applicationPanel.setVisible(false);
		dockPanel.add(applicationPanel,DockPanel.WEST);
		
		detailPanel = new DetailPanel(this);
		detailPanel.setVisible(false);
		dockPanel.add(detailPanel,DockPanel.CENTER);
		
		RootPanel.get("browser").add(dockPanel);
	}
	
	public void login() {
		String token = loginPanel.getToken();
		if (token == null ) {
			loginPanel.setVisible(true);
		} else {
			loginPanel.setVisible(false);
			applicationPanel.setVisible(true);
		}
	}

	public BusinessTreePanel getApplicationPanel() {
		return applicationPanel;
	}

	protected void setApplicationPanel(BusinessTreePanel applicationPanel) {
		this.applicationPanel = applicationPanel;
	}

	public LoginPanel getLoginPanel() {
		return loginPanel;
	}

	protected void setLoginPanel(LoginPanel loginPanel) {
		this.loginPanel = loginPanel;
	}

	public DetailPanel getDetailPanel() {
		return detailPanel;
	}

	public void setDetailPanel(DetailPanel detailPanel) {
		this.detailPanel = detailPanel;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	
}
	
 