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

import org.apache.juddi.portlets.client.model.Publisher;
import org.apache.juddi.portlets.client.service.JUDDIApiResponse;
import org.apache.juddi.portlets.client.service.JUDDIApiService;
import org.apache.juddi.portlets.client.service.JUDDIApiServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 *  @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class JUDDIPublisher implements EntryPoint, Login {

	private static JUDDIPublisher singleton;
	
	private FlowPanel flowPanel = new FlowPanel();
	private MenuBarPanel menuBar = new MenuBarPanel(MenuBarPanel.PUBLISHER);
	private StatusBarPanel statusBar = new StatusBarPanel(this);
	private DockPanel dockPanel = new DockPanel();
	private LoginPanel loginPanel = new LoginPanel(this);
	private PublisherListPanel publisherListPanel = new PublisherListPanel();
	private PublisherPanel publisherPanel = null;
	private JUDDIApiServiceAsync juddiApiService = (JUDDIApiServiceAsync) GWT.create(JUDDIApiService.class);
	private boolean isAdmin = false;
	
	public static JUDDIPublisher getInstance() {
		return singleton;
	}
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() { 
		singleton = this;
		
		dockPanel.setWidth("100%");
		dockPanel.setSpacing(8);
		
		menuBar.setVisible(false);
		menuBar.setStyleName("menu");
		
		flowPanel.setWidth("100%");
		flowPanel.add(menuBar);
		
		statusBar.setVisible(false);
		statusBar.setStyleName("status");
		
		flowPanel.add(statusBar);
		
		dockPanel.add(flowPanel,DockPanel.NORTH);
		
		loginPanel.setVisible(false);
		dockPanel.add(loginPanel,DockPanel.WEST);
		
		publisherListPanel.setVisible(false);
		publisherListPanel.setWidth("100%");
		dockPanel.add(publisherListPanel,DockPanel.CENTER);
		
		RootPanel.get("publisher").add(dockPanel);
	}
	
	public void login() {
		String token = loginPanel.getToken();
		if (token == null ) {
			loginPanel.setVisible(true);
			publisherListPanel.setVisible(false);
			menuBar.setVisible(false);
			statusBar.setUser("");
			statusBar.setVisible(false);
		} else {
			loginPanel.setVisible(false);
			menuBar.setVisible(true);
			if (getIsAdmin()) menuBar.setVisible(true);
			statusBar.setVisible(true);
			statusBar.setUser(getPublisherId());
			publisherListPanel.setVisible(true);
			publisherListPanel.listPublishers(this);
		}
	}
	
	public void logout() {
		loginPanel.setToken(null);
		loginPanel.setVisible(true);
		publisherListPanel.setVisible(false);
		menuBar.setVisible(false);
		statusBar.setUser("");
		statusBar.setVisible(false);
		if (publisherPanel!=null) publisherPanel.setVisible(false);
	}
	
	public void displayPublisher(Publisher publisher) {
		if (publisherPanel!=null ) dockPanel.remove(publisherPanel);
		publisherPanel = new PublisherPanel(publisher);
		publisherPanel.setWidth("100%");
		publisherPanel.setStyleName("detail-panel");
		dockPanel.add(publisherPanel,DockPanel.EAST);
		publisherListPanel.listPublishers(this);
	}
	
	public void setSelectedPublisher(String selectedPublisherId) {
		publisherListPanel.setSelectedPublisher(selectedPublisherId);
	}
	
	public void hidePublisher() {
		publisherPanel.setVisible(false);
		publisherListPanel.selectRow(0);
		if (publisherPanel!=null ) dockPanel.remove(publisherPanel);
		publisherPanel=null;
		publisherListPanel.listPublishers(this);
	}

	public String getToken() {
		return loginPanel.getToken();
	}
	
	public String getPublisherId() {
		return loginPanel.getPublisherId();
	}
	
	public void savePublisher() {
		if (publisherPanel!=null) {
			publisherPanel.savePublisher(getToken());
		}
		publisherListPanel.listPublishers(this);
	}
	
	public void newPublisher() {
		if (publisherPanel!=null ) dockPanel.remove(publisherPanel);
		publisherPanel = new PublisherPanel(null);
		publisherPanel.setWidth("100%");
		publisherPanel.setStyleName("detail-panel");
		dockPanel.add(publisherPanel,DockPanel.EAST);
		publisherListPanel.selectRow(0);
	}
	
	public void deletePublisher() {
		if (publisherPanel!=null) {
			publisherPanel.deletePublisher(getToken());
		} else {
			Window.alert("Please select a publisher first.");
		}
	}
	
	public void crossRegister() {
		String token = getToken();
		juddiApiService.restartClient(token, new AsyncCallback<JUDDIApiResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage());
			}

			public void onSuccess(JUDDIApiResponse response) {
				if (response.isSuccess()) {
					Window.alert(response.getMessage());
				} else {
					Window.alert("error: " + response.getMessage());
				}
			}
		});
	}
	
	public boolean getIsAdmin() {
		return isAdmin;
	}
	
	public void setIsAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
		
		menuBar.setVisible(isAdmin);
		if (publisherPanel!=null) {
			publisherPanel.setUserIsAdmin(isAdmin);
		}
	}
	
}
