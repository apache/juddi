package org.apache.juddi.portlets.client;

import java.util.List;

import org.apache.juddi.portlets.client.model.Business;
import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.service.PublicationResponse;
import org.apache.juddi.portlets.client.service.PublicationService;
import org.apache.juddi.portlets.client.service.PublicationServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;

public class BusinessTreePanel extends Composite implements TreeListener {

	public static final Images images = (Images) GWT.create(Images.class);
	private Tree publisherTree;
	private PublicationServiceAsync publicationService = (PublicationServiceAsync) GWT.create(PublicationService.class);
	UDDIBrowser browser = null;
	
	public BusinessTreePanel(UDDIBrowser browser) {
		this.browser = browser;
		publisherTree = new Tree(images);
		publisherTree.addTreeListener(this);
		initWidget(publisherTree);
	}
	
	protected void getBusinesses(String infoSelection) {

		publicationService.getBusinesses(browser.getToken(), infoSelection, new AsyncCallback<PublicationResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(PublicationResponse response) {
				if (response.isSuccess()) {
					List<Business> businesses= response.getBusinesses();
					System.out.println("Businesses=" + businesses);
					
					for (Business business : businesses) {
					
						TreeItem businessTree = new TreeItem(images.business().getHTML() + " " + business.getName());
						businessTree.setStyleName("portlet-form-field-label");
						businessTree.setState(true);
						
						TreeItem keyItem = new TreeItem(images.key().getHTML() + " " + business.getKey());
						keyItem.setStyleName("portlet-form-field-label");
						businessTree.addItem(keyItem);
						
						TreeItem descriptionItem = new TreeItem(images.description().getHTML() + " " + business.getDescription());
						descriptionItem.setStyleName("portlet-form-field-label");
						businessTree.addItem(descriptionItem);
						TreeItem serviceTree = new TreeItem(images.services().getHTML() + " Services owned by this business");
						for (Service service : business.getServices()) {
							TreeItem serviceItem = new TreeItem(images.service().getHTML() + " " + service.getName());
							serviceItem.setStyleName("portlet-form-field-label");
							serviceItem.setUserObject(service);
							serviceTree.addItem(serviceItem);
							//serviceTree.setTitle("Service:" + );
							TreeItem serviceKey = new TreeItem(images.key().getHTML() + " " + service.getKey());
							serviceKey.setStyleName("portlet-form-field-label");
							serviceTree.addItem(serviceKey);
						}
						businessTree.addItem(serviceTree);

						publisherTree.addItem(businessTree);
					}
					
				} else {
					//tmodelLabel.setText("error: " + response.getMessage());
				}
			}
		});
	}

	public void onTreeItemSelected(TreeItem treeItem) {
		System.out.println("Selected " + treeItem.getText());
		if (treeItem.getUserObject()!=null && Service.class.equals(treeItem.getUserObject().getClass())) {
			Service service = (Service) treeItem.getUserObject();
			browser.getDetailPanel().setVisible(true);
			browser.getDetailPanel().displayService(service.getKey());
			
		}
	}

	public void onTreeItemStateChanged(TreeItem arg0) {
		// TODO Auto-generated method stub
		System.out.println("StateChanged " + arg0.getText());
	}
	
}
