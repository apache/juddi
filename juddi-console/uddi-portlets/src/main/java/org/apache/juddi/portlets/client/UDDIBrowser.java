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
	private static UDDIBrowser singleton;
	public static final Images images = (Images) GWT.create(Images.class);
	private BusinessTreePanel applicationPanel = null;
	private DetailPanel detailPanel = null;
	private LoginPanel loginPanel = null;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		singleton = this;
		loginPanel = new LoginPanel(this);
		loginPanel.setVisible(false);
		dockPanel.add(loginPanel, DockPanel.NORTH);
		
		applicationPanel = new BusinessTreePanel();
		applicationPanel.setVisible(false);
		dockPanel.add(applicationPanel,DockPanel.WEST);
		
		detailPanel = new DetailPanel();
		detailPanel.setVisible(false);
        detailPanel.setWidth("100%");
        detailPanel.setStyleName("detail-panel");
		dockPanel.add(detailPanel,DockPanel.CENTER);
		
		RootPanel.get("browser").add(dockPanel);
	}
	
	public static UDDIBrowser getInstance() {
		return singleton;
	}
	
	public void login() {
		String token = loginPanel.getToken();
		if (token == null ) {
			loginPanel.setVisible(true);
		} else {
			loginPanel.setVisible(false);
			applicationPanel.setVisible(true);
			applicationPanel.findAllBusinesses();
		}
	}
	
	public String getToken() {
		return loginPanel.getToken();
	}
	
	protected DetailPanel getDetailPanel() {
		return detailPanel;
	}
	
	public void logout() {
		loginPanel.setVisible(true);
		applicationPanel.setVisible(false);
	}

	
}
	
 