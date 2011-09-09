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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class StatusBarPanel extends FlowPanel implements ClickListener {

	public static final Images images = (Images) GWT.create(Images.class);
	PushButton logoutButton = new PushButton();
	Label user = new Label();

	public StatusBarPanel(JUDDIPublisher publisher) {
		
		HorizontalPanel pushPanel = new HorizontalPanel();
	    pushPanel.setSpacing(7);

	    user.setText("");
	    pushPanel.add(user);
	    
		logoutButton.setHTML(images.logout().getHTML());
		logoutButton.setStyleName(("portlet-form-button"));
		logoutButton.addClickListener(this);
		logoutButton.setTitle("Logout");
		
		// in hosted mode it can be handy to have a logout button
		boolean debug=false;
		if (debug) {
			pushPanel.add(logoutButton);
		}
		
		add(pushPanel);
		
	}

	public void onClick(Widget sender) {
		if (sender == logoutButton) {
			JUDDIPublisher.getInstance().logout();
		}
		
	}
	
	public void setUser(String user) {
		this.user.setText(user);
	}
}
