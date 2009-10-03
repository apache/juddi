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
	
	private TextBox clerkNameBox = new TextBox();
	private TextBox bindingKeyBox = new TextBox();
	private CheckBox isBriefBox = new CheckBox();
	private TextBox expiresAfterBox = new TextBox();
	private TextBox maxEntitiesBox = new TextBox();
	private TextBox notificationIntervalBox = new TextBox();
	private TextBox subscriptionFilterBox = new TextBox();
	private TextBox subscriptionKeyBox = new TextBox();
	Subscription subscription = null;
	
	public SubscriptionPanel(Subscription subscription, Node node) {
		
		if (subscription==null) {
			newSubscription(node);
		} else {
			this.subscription = subscription;
		}
		
		FlexTable flexTable = new FlexTable();
		add(flexTable);
		
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
		
		Label isBrief = new Label ("Is Brief:");
		isBrief.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(2, 0,isBrief);
		isBriefBox.setChecked(this.subscription.getBrief());
		isBriefBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(2, 1, isBriefBox);
		
		Label expiresAfter = new Label ("Expires After:");
		expiresAfter.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(3, 0, expiresAfter);
		expiresAfterBox.setText(this.subscription.getExpiresAfter());
		expiresAfterBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(3, 1, expiresAfterBox);
		
		Label maxEntities = new Label ("Max Entities:");
		maxEntities.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(4, 0, maxEntities);
		maxEntitiesBox.setText(String.valueOf(this.subscription.getMaxEntities()));
		maxEntitiesBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(4, 1, maxEntitiesBox);
		
		Label subscriptionFilter = new Label ("Search Filter:");
		subscriptionFilter.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(5, 0, subscriptionFilter);
		subscriptionFilterBox.setText(String.valueOf(this.subscription.getSubscriptionFilter()));
		subscriptionFilterBox.setStyleName("portlet-form-input-field");
		subscriptionFilterBox.setHeight("100px");
		flexTable.setWidget(5, 1, subscriptionFilterBox);
		
		Label id = new Label ("Binding Key:");
		id.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(6, 0, id);
		bindingKeyBox.setText(this.subscription.getBindingKey());
		bindingKeyBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(6, 1, bindingKeyBox);
		
		Label notificationInterval = new Label ("Notification Interval:");
		notificationInterval.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(7, 0, notificationInterval);
		notificationIntervalBox.setText(String.valueOf(this.subscription.getNotificationInterval()));
		notificationIntervalBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(7, 1, notificationIntervalBox);
		
	}
	
	protected void deleteSubscription(){
		if (subscription!=null) {
			subscription.setSubscriptionKey(subscriptionKeyBox.getText());
			subscription.setClerkName(clerkNameBox.getText());
			
			subscriptionServiceAsync.deleteSubscription(subscription, new AsyncCallback<SubscriptionResponse>()
					{
						public void onFailure(Throwable caught) {
							Window.alert("Could not connect to the UDDI registry. " + caught.getMessage());
						}
			
						public void onSuccess(SubscriptionResponse response) {
							if (response.isSuccess()) {
								UDDISubscription.getInstance().refreshSubscriptionTree();
								UDDISubscription.getInstance().removeDetailPanel();
							} else {
								Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
							}
						}
					}); 
		}
	}
	
	protected void newSubscription(Node node){
		subscription = new Subscription();
		subscription.setSubscriptionKey("uddi:uddi.listeningforchanges.com:callthiskeytonotify");
		subscription.setBindingKey("uddi:uddi.example.com:subscriptionone");
		subscription.setBrief(true);
		subscription.setMaxEntities(1000);
		subscription.setNotificationInterval("P5D");
		subscription.setSubscriptionFilter(
				  "<subscriptionFilter xmlns=\"urn:uddi-org:sub_v3\">"
			    + "  <find_service xmlns=\"urn:uddi-org:api_v3\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">"
			    + "    <findQualifiers>"
			    + "      <findQualifier>exactMatch</findQualifier>"
			    + "    </findQualifiers>"
			    + "    <name xml:lang=\"en\">Notifier One</name>"
			    + "    <categoryBag>"
			    + "      <keyedReference tModelKey=\"uddi:tmodelkey:categories\" keyName=\"category\" keyValue=\"peaches\" />"
			    + "      <keyedReference tModelKey=\"uddi:tmodelkey:categories\" keyName=\"category\" keyValue=\"plums\" />"
			    + "      <keyedReferenceGroup tModelKey=\"uddi:tmodelKey:group\">"
			    + "        <keyedReference tModelKey=\"uddi:tmodelKey:blank\" keyName=\"blank\" keyValue=\"blank\" />"
			    + "      </keyedReferenceGroup>"
			    + "    </categoryBag>"
			    + "    </find_service>"
			    + "</subscriptionFilter>");
		subscription.setNode(node);
				
	}
	
	protected void saveSubscription() {
		if (subscription!=null) {
			
			subscription.setBindingKey(bindingKeyBox.getText());
			subscription.setBrief(isBriefBox.isChecked());
			subscription.setExpiresAfter(expiresAfterBox.getText());
			subscription.setMaxEntities("".equals(maxEntitiesBox.getText())?null:Integer.valueOf(maxEntitiesBox.getText()));
			subscription.setNotificationInterval(notificationIntervalBox.getText());
			subscription.setSubscriptionFilter(subscriptionFilterBox.getText());
			subscription.setSubscriptionKey(subscriptionKeyBox.getText());
			subscription.setClerkName(clerkNameBox.getText());
			
			subscriptionServiceAsync.saveSubscription(subscription, new AsyncCallback<SubscriptionResponse>()
			{
				public void onFailure(Throwable caught) {
					Window.alert("Could not connect to the UDDI registry. " + caught.getMessage());
				}
	
				public void onSuccess(SubscriptionResponse response) {
					if (response.isSuccess()) {
						UDDISubscription.getInstance().displaySubscription(response.getSubscription());
						UDDISubscription.getInstance().refreshSubscriptionTree();
					} else {
						Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
					}
				}
			}); 
		}
	}
	
}
