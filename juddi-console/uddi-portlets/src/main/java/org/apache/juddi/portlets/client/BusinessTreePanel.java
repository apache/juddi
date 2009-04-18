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

	private static String SERVICES_LABEL="Services owned by this business";
	private Tree publisherTree;
	private PublicationServiceAsync publicationService = (PublicationServiceAsync) GWT.create(PublicationService.class);
	UDDIBrowser browser = null;
	
	public BusinessTreePanel(UDDIBrowser browser) {
		this.browser = browser;
		publisherTree = new Tree(UDDIBrowser.images);
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
					
						TreeItem businessTree = new TreeItem(UDDIBrowser.images.business().getHTML() + " " + business.getName());
						businessTree.setStyleName("portlet-form-field-label");
						businessTree.setState(true);
						businessTree.setUserObject(business);
						TreeItem serviceTree = new TreeItem(UDDIBrowser.images.services().getHTML() + SERVICES_LABEL);
						serviceTree.setUserObject(business);
						for (Service service : business.getServices()) {
							TreeItem serviceItem = new TreeItem(UDDIBrowser.images.service().getHTML() + " " + service.getName());
							serviceItem.setStyleName("portlet-form-field-label");
							serviceItem.setUserObject(service);
							serviceTree.addItem(serviceItem);
							serviceTree.setTitle("Service:" +  service.getKey());
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
		} else if (treeItem.getUserObject()!=null && Business.class.equals(treeItem.getUserObject().getClass())) {
			Business business = (Business) treeItem.getUserObject();
			browser.getDetailPanel().setVisible(true);
			if (SERVICES_LABEL.equals(treeItem.getText())) {
				browser.getDetailPanel().displayServices(business.getKey());
			} else {
				browser.getDetailPanel().displayBusiness(business.getKey());
			}
		}
	}

	public void onTreeItemStateChanged(TreeItem arg0) {
		// TODO Auto-generated method stub
		System.out.println("StateChanged " + arg0.getText());
	}
	
}
