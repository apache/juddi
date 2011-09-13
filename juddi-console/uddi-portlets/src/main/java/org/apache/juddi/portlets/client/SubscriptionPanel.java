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

import org.apache.juddi.portlets.client.model.Node;
import org.apache.juddi.portlets.client.model.Subscription;
import org.apache.juddi.portlets.client.service.SubscriptionResponse;
import org.apache.juddi.portlets.client.service.SubscriptionService;
import org.apache.juddi.portlets.client.service.SubscriptionServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class SubscriptionPanel extends FlowPanel {

	private SubscriptionServiceAsync subscriptionServiceAsync = (SubscriptionServiceAsync) GWT.create(SubscriptionService.class);
	
	private String toClerkName = null;
	private TextBox clerkNameBox = new TextBox();
	private TextBox bindingKeyBox = new TextBox();
	private CheckBox isBriefBox = new CheckBox();
	private TextBox expiresAfterBox = new TextBox();
	private TextBox maxEntitiesBox = new TextBox();
	private TextBox notificationIntervalBox = new TextBox();
	private TextBox subscriptionFilterBox = new TextBox();
	private TextBox subscriptionKeyBox = new TextBox();
	private TextBox coverageStartBox = new TextBox();
	private TextBox coverageEndBox = new TextBox();
	
	Subscription subscription = null;
	FlexTable flexTable = null;
	
	boolean isAsync=true;
	
	public SubscriptionPanel(Subscription subscription, Node node) {
		
		if (subscription==null) {
			newSubscription(node);
			toClerkName = null;
		} else {
			this.subscription = subscription;
			toClerkName = subscription.getToClerkName();
		}
		
		flexTable = new FlexTable();
		add(flexTable);
		drawPanel();
		
	}
	
	public void drawPanel() {
		
		
		Label clerkName = new Label ("Clerk:");
		clerkName.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(0, 0, clerkName);
		clerkNameBox.setText(String.valueOf(this.subscription.getNode().getClerkName()));
		clerkNameBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(0, 1, clerkNameBox);

		Label subscriptionKey = new Label ("Subscription Key:");
		subscriptionKey.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(1, 0, subscriptionKey);
		subscriptionKeyBox.setText(String.valueOf(this.subscription.getSubscriptionKey()));
		subscriptionKeyBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(1, 1, subscriptionKeyBox);
		
		//async
		Label isBrief = new Label ("Is Brief:");
		isBrief.setStyleName("portlet-form-field-label-right");
		isBrief.setVisible(isAsync);
		flexTable.setWidget(2, 0,isBrief);
		isBriefBox.setChecked(this.subscription.getBrief());
		isBriefBox.setStyleName("portlet-form-input-field");
		isBriefBox.setVisible(isAsync);
		flexTable.setWidget(2, 1, isBriefBox);
		
		Label expiresAfter = new Label ("Expires After:");
		expiresAfter.setStyleName("portlet-form-field-label-right");
		expiresAfter.setVisible(isAsync);
		flexTable.setWidget(3, 0, expiresAfter);
		expiresAfterBox.setText(this.subscription.getExpiresAfter());
		expiresAfterBox.setStyleName("portlet-form-input-field");
		expiresAfterBox.setVisible(isAsync);
		flexTable.setWidget(3, 1, expiresAfterBox);
		
		Label maxEntities = new Label ("Max Entities:");
		maxEntities.setStyleName("portlet-form-field-label-right");
		maxEntities.setVisible(isAsync);
		flexTable.setWidget(4, 0, maxEntities);
		maxEntitiesBox.setText(String.valueOf(this.subscription.getMaxEntities()));
		maxEntitiesBox.setStyleName("portlet-form-input-field");
		maxEntitiesBox.setVisible(isAsync);
		flexTable.setWidget(4, 1, maxEntitiesBox);
		
		Label subscriptionFilter = new Label ("Search Filter:");
		subscriptionFilter.setStyleName("portlet-form-field-label-right");
		subscriptionFilter.setVisible(isAsync);
		flexTable.setWidget(5, 0, subscriptionFilter);
		subscriptionFilterBox.setText(String.valueOf(this.subscription.getSubscriptionFilter()));
		subscriptionFilterBox.setStyleName("portlet-form-input-field");
		subscriptionFilterBox.setHeight("100px");
		subscriptionFilterBox.setVisible(isAsync);
		flexTable.setWidget(5, 1, subscriptionFilterBox);
		
		Label bindingKey = new Label ("Binding Key:");
		bindingKey.setStyleName("portlet-form-field-label-right");
		bindingKey.setVisible(isAsync);
		flexTable.setWidget(6, 0, bindingKey);
		bindingKeyBox.setText(this.subscription.getBindingKey());
		bindingKeyBox.setStyleName("portlet-form-input-field");
		bindingKeyBox.setVisible(isAsync);
		flexTable.setWidget(6, 1, bindingKeyBox);
		
		Label notificationInterval = new Label ("Notification Interval:");
		notificationInterval.setStyleName("portlet-form-field-label-right");
		notificationInterval.setVisible(isAsync);
		flexTable.setWidget(7, 0, notificationInterval);
		notificationIntervalBox.setText(String.valueOf(this.subscription.getNotificationInterval()));
		notificationIntervalBox.setStyleName("portlet-form-input-field");
		notificationIntervalBox.setVisible(isAsync);
		flexTable.setWidget(7, 1, notificationIntervalBox);
		
		subscription.setCoverageStart("2008-01-01T00:00:00");
		subscription.setCoverageEnd("2010-01-01T00:00:00");
		
		Label coverageStart = new Label ("Coverage Start Date:");
		coverageStart.setStyleName("portlet-form-field-label-right");
		coverageStart.setVisible(!isAsync);
		flexTable.setWidget(8, 0, coverageStart);
		coverageStartBox.setText(this.subscription.getCoverageStart());
		coverageStartBox.setStyleName("portlet-form-input-field");
		coverageStartBox.setVisible(!isAsync);
		flexTable.setWidget(8, 1, coverageStartBox);
		
		Label coverageEnd = new Label ("Coverage End Date:");
		coverageEnd.setStyleName("portlet-form-field-label-right");
		coverageEnd.setVisible(!isAsync);
		flexTable.setWidget(9, 0, coverageEnd);
		coverageEndBox.setText(this.subscription.getCoverageEnd());
		coverageEndBox.setStyleName("portlet-form-input-field");
		coverageEndBox.setVisible(!isAsync);
		flexTable.setWidget(9, 1, coverageEndBox);
		
	}
	
	protected void invokeSyncSubscription(String authToken) {
		if (subscription!=null) {
			subscription.setSubscriptionKey(subscriptionKeyBox.getText());
			subscription.setFromClerkName(clerkNameBox.getText());
			subscription.setCoverageStart(coverageStartBox.getText());
			subscription.setCoverageEnd(coverageEndBox.getText());
			
			subscriptionServiceAsync.invokeSyncSubscription(authToken, subscription, new AsyncCallback<SubscriptionResponse>()
					{
				public void onFailure(Throwable caught) {
					Window.Location.reload();
				}
	
				public void onSuccess(SubscriptionResponse response) {
					if (response.isSuccess()) {
						Window.alert("successfull sync");
					} else {
						Window.alert("error: " + response.getMessage());
					}
				}
			}); 
		}
		
	}
	
	protected void deleteSubscription(String authToken){
		if (subscription!=null) {
			subscription.setSubscriptionKey(subscriptionKeyBox.getText());
			subscription.setFromClerkName(clerkNameBox.getText());
			
			subscriptionServiceAsync.deleteSubscription(authToken, subscription, new AsyncCallback<SubscriptionResponse>()
					{
						public void onFailure(Throwable caught) {
							Window.Location.reload();
						}
			
						public void onSuccess(SubscriptionResponse response) {
							if (response.isSuccess()) {
								UDDISubscription.getInstance().refreshSubscriptionTree();
								UDDISubscription.getInstance().removeDetailPanel();
							} else {
								Window.alert("error: " + response.getMessage());
							}
						}
					}); 
		}
	}
	
	protected void newSubscription(Node node){
		subscription = new Subscription();
		subscription.setSubscriptionKey("uddi:marketing.apache.org:subscription:key1");
		subscription.setBindingKey("uddi:juddi.apache.org:servicebindings-subscriptionlistener-ws");
		subscription.setBrief(false);
		subscription.setMaxEntities(1000);
		subscription.setNotificationInterval("P5D");
		subscription.setSubscriptionFilter(
				  "<subscriptionFilter xmlns=\"urn:uddi-org:sub_v3\">"
				    + "<find_service xmlns=\"urn:uddi-org:api_v3\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">"
					+ "<findQualifiers>"
					+	"<findQualifier>exactMatch</findQualifier>"
					+ "</findQualifiers>"
					+ "<name xml:lang=\"en\">HelloWorld</name>"
				    + "</find_service>"
			    + "</subscriptionFilter>");
		subscription.setNode(node);
		subscription.setCoverageStart("2008-01-01T00:00:00");
		subscription.setCoverageEnd("2010-01-01T00:00:00");
				
	}
	
	protected void saveSubscription(String authToken) {
		if (subscription!=null) {
			
			subscription.setBindingKey(bindingKeyBox.getText());
			subscription.setBrief(isBriefBox.isChecked());
			subscription.setExpiresAfter(expiresAfterBox.getText());
			subscription.setMaxEntities("".equals(maxEntitiesBox.getText())?null:Integer.valueOf(maxEntitiesBox.getText()));
			subscription.setNotificationInterval(notificationIntervalBox.getText());
			subscription.setSubscriptionFilter(subscriptionFilterBox.getText());
			subscription.setSubscriptionKey(subscriptionKeyBox.getText());
			subscription.setFromClerkName(clerkNameBox.getText());
			subscription.setToClerkName(toClerkName);
			
			subscriptionServiceAsync.saveSubscription(authToken, subscription, new AsyncCallback<SubscriptionResponse>()
			{
				public void onFailure(Throwable caught) {
					Window.Location.reload();
				}
	
				public void onSuccess(SubscriptionResponse response) {
					if (response.isSuccess()) {
						UDDISubscription.getInstance().displaySubscription(response.getSubscription());
						UDDISubscription.getInstance().refreshSubscriptionTree();
					} else {
						Window.alert("error: " + response.getMessage());
					}
				}
			}); 
		}
	}
	
}
