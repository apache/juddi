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

import org.apache.juddi.portlets.client.service.NotifyResponse;
import org.apache.juddi.portlets.client.service.NotifyService;
import org.apache.juddi.portlets.client.service.NotifyServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	private NotifyServiceAsync notifyService = (NotifyServiceAsync) GWT.create(NotifyService.class);
	
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

	public void onModuleLoad() {
		notifyPanel = new NotifyPanel(this);
		notifyPanel.setVisible(true);
		notifyPanel.getTextArea().setText("");
		setNotifyPanel(notifyPanel);
		RootPanel.get("notification").add(notifyPanel);

		Timer timer = new Timer() {
			public void run() {
				getSubscriptionNotifications();
			}
		};
	    timer.scheduleRepeating(20000);
	    getSubscriptionNotifications();
	}	
	
	public void getSubscriptionNotifications() {
       	final String ta = notifyPanel.getTextArea().getText();
        
       	notifyService.getSubscriptionNotifications("", new AsyncCallback<NotifyResponse>() {
            public void onFailure(Throwable caught) {
                Window.alert("Could not connect to SubscriptionListener WS : " + caught.getMessage() );
            }
            public void onSuccess(NotifyResponse result) {
           		if (result.getSubscriptionNotifications() != null) {
           			if (("".equals(ta)) || (ta == null)) {
               			notifyPanel.getTextArea().setText(result.getSubscriptionNotifications());				
           			} else {
           				notifyPanel.getTextArea().setText(ta + " \n" + result.getSubscriptionNotifications());				
           			}
           		}
           	}
		});
		
	}
}
	
 