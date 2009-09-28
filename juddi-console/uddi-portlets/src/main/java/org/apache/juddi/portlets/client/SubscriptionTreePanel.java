package org.apache.juddi.portlets.client;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.portlets.client.model.Node;
import org.apache.juddi.portlets.client.model.Subscription;
import org.apache.juddi.portlets.client.service.SubscriptionResponse;
import org.apache.juddi.portlets.client.service.SubscriptionService;
import org.apache.juddi.portlets.client.service.SubscriptionServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

public class SubscriptionTreePanel extends Composite implements TreeListener {

	private Tree subscriptionTree;
	private SubscriptionServiceAsync subscriptionService = (SubscriptionServiceAsync) GWT.create(SubscriptionService.class);
	
	public SubscriptionTreePanel() {
		subscriptionTree = new Tree(UDDIBrowser.images);
		subscriptionTree.addTreeListener(this);
		initWidget(subscriptionTree);
	}
	
	public void loadSubscriptions() {
		getSubscriptions();
	}
	
	protected void getSubscriptions() {

		subscriptionService.getSubscriptions(UDDISubscription.getInstance().getToken(), new AsyncCallback<SubscriptionResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(SubscriptionResponse response) {
				if (response.isSuccess()) {
					List<Node> nodes = response.getNodes();
					System.out.println("Node=" + nodes);
					for (Node node : nodes) {
						String statusImg = UDDIBrowser.images.down().getHTML();
						if ("Up".equals(node.getStatus())) {
							statusImg = UDDIBrowser.images.down().getHTML();
						}
						TreeItem nodeTree = new TreeItem(statusImg + node.getName());
						nodeTree.setStyleName("portlet-form-field-label");
						nodeTree.setState(true);
						nodeTree.setUserObject(node);
						TreeItem nodeItem = new TreeItem(UDDIBrowser.images.description().getHTML() + " " + node.getDescription());
						nodeItem.setStyleName("portlet-form-field-label");
						nodeTree.addItem(nodeItem);
						
						for (Subscription subcription : node.getSubscriptions()) {
							TreeItem subcriptionItem = new TreeItem(UDDIBrowser.images.subscription().getHTML() + " " 
									+ subcription.getSubscriptionKey() + ":" + subcription.getExpiresAfter());
							subcriptionItem.setStyleName("portlet-form-field-label");
							subcriptionItem.setUserObject(subcription);
							nodeTree.addItem(subcriptionItem);
						}

						subscriptionTree.addItem(nodeTree);
					}
					
				} else {
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
		});
	}
	

	public void onTreeItemSelected(TreeItem treeItem) {
		System.out.println("Selected " + treeItem.getText());
		if (treeItem.getUserObject()!=null && Subscription.class.equals(treeItem.getUserObject().getClass())) {
			Subscription subscription = (Subscription) treeItem.getUserObject();
			UDDISubscription.getInstance().displaySubscription(subscription);
		}
	}

	public void onTreeItemStateChanged(TreeItem arg0) {
		// TODO Auto-generated method stub
		System.out.println("StateChanged " + arg0.getText());
	}
	
}
