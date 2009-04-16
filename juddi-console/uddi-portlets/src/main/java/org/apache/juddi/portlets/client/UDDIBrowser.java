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
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 *  @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDIBrowser implements EntryPoint {

	DockPanel dockPanel = new DockPanel();
	
	private BusinessTreePanel browsePanel = null;
	private DetailPanel detailPanel = null;
	private LoginPanel loginPanel = null;
	private String token = null;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		loginPanel = new LoginPanel(this);
		loginPanel.setVisible(false);
		dockPanel.add(loginPanel, DockPanel.NORTH);
		
		browsePanel = new BusinessTreePanel(this);
		browsePanel.setWidth("200px");
		browsePanel.setVisible(false);
		dockPanel.add(browsePanel,DockPanel.WEST);
		
		detailPanel = new DetailPanel(this);
		detailPanel.setVisible(false);
		dockPanel.add(detailPanel,DockPanel.CENTER);
		
		//dockPanel.add(detailPanel);
		
		RootPanel.get("browser").add(dockPanel);
		loginPanel.getToken(null,null);
	}

	protected BusinessTreePanel getBrowsePanel() {
		return browsePanel;
	}

	protected void setBrowsePanel(BusinessTreePanel browsePanel) {
		this.browsePanel = browsePanel;
	}

	protected LoginPanel getLoginPanel() {
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
	
	
	
	
}
	
 