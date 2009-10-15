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

import org.apache.juddi.portlets.client.service.NotifyService;
import org.apache.juddi.portlets.client.service.NotifyServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 *
 */
public class NotifyPanel extends VerticalPanel {

	//private UDDISubscriptionNotification notification = null;
	//private NotifyPanel notifyPanel = null;
	private TextArea textArea = null;
	FlexTable table = null;

	private NotifyServiceAsync notifyService = (NotifyServiceAsync) GWT.create(NotifyService.class);
	
	public NotifyPanel(UDDISubscriptionNotification notification) {
		super();
		//this.notification = notification;
		//notifyPanel = this;
		getElement().setId("notifications-body");
		Label notifyLabel = new Label ("Subscription notifications:");
		notifyLabel.setStyleName("portlet-form-field-label");
		add(notifyLabel);
		
	    textArea = new TextArea();
	    textArea.setCharacterWidth(50);
	    textArea.setVisibleLines(50);
	    add(textArea);
	}
	
	public TextArea getTextArea() {
		return textArea;
	}
	
	public void setTextArea(TextArea textArea) {
		this.textArea = textArea;
	}
	
	public NotifyServiceAsync getSecurityService() {
		return notifyService;
	}

	public void setNotifyService(NotifyServiceAsync notifyService) {
		this.notifyService = notifyService;
	}

}
