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

import java.util.List;

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
	private Node selectedNode=null;
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

		subscriptionService.getSubscriptions(new AsyncCallback<SubscriptionResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.Location.reload();
			}

			public void onSuccess(SubscriptionResponse response) {
				if (response.isSuccess()) {
					List<Node> nodes = response.getNodes();
					System.out.println("Nodes=" + nodes);
					for (Node node : nodes) {
						String statusImg = UDDIBrowser.images.down().getHTML();
						if ("Up".equals(node.getStatus())) {
							statusImg = UDDIBrowser.images.up().getHTML();
						}
						TreeItem nodeTree = new TreeItem(statusImg + " " + node.getName());
						nodeTree.setUserObject(node);
						nodeTree.setStyleName("portlet-form-field-label");
						nodeTree.setState(true);
						nodeTree.setUserObject(node);
						
						if (node.getStatus().startsWith("D")) {
							TreeItem statItem = new TreeItem(node.getStatus());
							statItem.setStyleName("portlet-form-field-label");
							statItem.setUserObject(node);
							nodeTree.addItem(statItem);
						}
						
						TreeItem descItem = new TreeItem(UDDIBrowser.images.description().getHTML() + " " + node.getDescription());
						descItem.setStyleName("portlet-form-field-label");
						descItem.setUserObject(node);
						nodeTree.addItem(descItem);
						
						for (Subscription subcription : node.getSubscriptions()) {
							TreeItem subcriptionItem = new TreeItem(UDDIBrowser.images.subscription().getHTML() + " " 
									+ subcription.getSubscriptionKey());
							subcriptionItem.setStyleName("portlet-form-field-label");
							subcription.setNode(node);
							subcriptionItem.setUserObject(subcription);
							nodeTree.addItem(subcriptionItem);
						}

						subscriptionTree.addItem(nodeTree);
					}
					
				} else {
					Window.alert("error" + response.getMessage());
				}
			}
		});
	}
	

	public void onTreeItemSelected(TreeItem treeItem) {
		
		if (treeItem.getUserObject()!=null && Node.class.equals(treeItem.getUserObject().getClass())) {
			Node node = (Node) treeItem.getUserObject();
			selectedNode = node;
			System.out.println("Selected " + selectedNode);
		}
		if (treeItem.getUserObject()!=null && Subscription.class.equals(treeItem.getUserObject().getClass())) {
			Subscription subscription = (Subscription) treeItem.getUserObject();
			UDDISubscription.getInstance().displaySubscription(subscription);
			selectedNode = subscription.getNode();
			System.out.println("Selected " + selectedNode);
		}
	}

	public void onTreeItemStateChanged(TreeItem arg0) {
		// TODO Auto-generated method stub
		System.out.println("StateChanged " + arg0.getText());
	}
	
	public Node getSelectedNode() {
		return selectedNode;
	}

	public Tree getSubscriptionTree() {
		return subscriptionTree;
	}
	
}
