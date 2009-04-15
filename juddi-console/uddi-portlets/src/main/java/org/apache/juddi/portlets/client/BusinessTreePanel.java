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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class BusinessTreePanel extends FlowPanel {

	private Tree publisherTree = new Tree();
	private PublicationServiceAsync publicationService = (PublicationServiceAsync) GWT.create(PublicationService.class);
	public static final Images images = (Images) GWT.create(Images.class);
	
	public BusinessTreePanel() {
		Label businesses = new Label ("Businesses");
		businesses.setStyleName("portlet-form-field-label");
		this.add(businesses);
		this.add(publisherTree);
	}
	
	protected void getBusinesses(String token, String infoSelection) {

		publicationService.getBusinesses(token, infoSelection, new AsyncCallback<PublicationResponse>() 
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
							serviceTree.addItem(serviceItem);
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
	
	
}
