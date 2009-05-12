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
 *  @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class UDDISubscriptionNotification implements EntryPoint {

	//public static final Images images = (Images) GWT.create(Images.class);
	private NotifyPanel notifyPanel = null;
	private String token = null;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		notifyPanel = new NotifyPanel(this);
		notifyPanel.setVisible(true);
		setNotifyPanel(notifyPanel);
		RootPanel.get("notification").add(notifyPanel);
	}

	protected NotifyPanel getNotifyPanel() {
		return notifyPanel;
	}

	protected void setNotifyPanel(NotifyPanel notifyPanel) {
		this.notifyPanel = notifyPanel;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}	
}
	
 