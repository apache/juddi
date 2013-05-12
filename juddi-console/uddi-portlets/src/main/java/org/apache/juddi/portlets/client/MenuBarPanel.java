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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class MenuBarPanel extends FlowPanel implements ClickListener {

	public static final String PUBLISHER = "publisher";
	public static final String ADMIN = "admin";
	public static final String SUBSCRIPTION = "subscription";
	public static final Images images = (Images) GWT.create(Images.class);
	PushButton saveButton = new PushButton();
	PushButton newButton = new PushButton();
	PushButton deleteButton = new PushButton();
	PushButton syncButton = new PushButton();
	PushButton clientButton = new PushButton();
	String context = PUBLISHER;

	public MenuBarPanel(String context) {
		
		this.context = context;
		HorizontalPanel pushPanel = new HorizontalPanel();
	    pushPanel.setSpacing(7);

		newButton.setHTML(images.create().getHTML());
		newButton.setStyleName(("portlet-form-button"));
		newButton.addClickListener(this);
		pushPanel.add(newButton);
		
		saveButton.setHTML(images.save().getHTML());
		saveButton.setStyleName(("portlet-form-button"));
		saveButton.addClickListener(this);
		pushPanel.add(saveButton);
		
		deleteButton.setHTML(images.delete().getHTML());
		deleteButton.setStyleName(("portlet-form-button"));
		deleteButton.addClickListener(this);
		pushPanel.add(deleteButton);
		
		if (SUBSCRIPTION.equals(context)) {
			syncButton.setHTML(images.sync().getHTML());
			syncButton.setStyleName(("portlet-form-button"));
			syncButton.addClickListener(this);
			syncButton.setTitle("Refresh subscriptions");
			pushPanel.add(syncButton);
			newButton.setTitle("Create New Subscription");
			deleteButton.setTitle("Delete Selected Subscription");
			saveButton.setTitle("Save Subscription");
		}
		
		if (PUBLISHER.equals(context)) {
			clientButton.setHTML(images.manager().getHTML());
			clientButton.setStyleName(("portlet-form-button"));
			clientButton.addClickListener(this);
			clientButton.setTitle("Restart Client");
			pushPanel.add(clientButton);
			newButton.setTitle("Create New Publisher");
			deleteButton.setTitle("Delete Selected Publisher");
			saveButton.setTitle("Save Publisher");
			
		}
		
		add(pushPanel);
		
	}

	public void onClick(Widget sender) {
		if (sender == saveButton) {
			if (PUBLISHER.equals(context)) JUDDIPublisher.getInstance().savePublisher();
			else if (SUBSCRIPTION.equals(context)) UDDISubscription.getInstance().saveSubscription();
		} else if (sender == newButton) {
			if (PUBLISHER.equals(context)) JUDDIPublisher.getInstance().newPublisher();
			else if (SUBSCRIPTION.equals(context)) UDDISubscription.getInstance().newSubscription();
		} else if (sender == deleteButton) {
			if (PUBLISHER.equals(context)) JUDDIPublisher.getInstance().deletePublisher();
			else if (SUBSCRIPTION.equals(context)) UDDISubscription.getInstance().deleteSubscription();
		} else if (sender == syncButton) {
			UDDISubscription.getInstance().syncListedServices();
		} else if (sender == clientButton) {
			JUDDIPublisher.getInstance().crossRegister();
		}
		
	}
}
