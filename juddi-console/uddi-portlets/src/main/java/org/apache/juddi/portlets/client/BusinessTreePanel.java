package org.apache.juddi.portlets.client;

import java.util.List;

import org.apache.juddi.portlets.client.model.Business;
import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.service.FindResponse;
import org.apache.juddi.portlets.client.service.FindService;
import org.apache.juddi.portlets.client.service.FindServiceAsync;
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
	private FindServiceAsync findService = (FindServiceAsync) GWT.create(FindService.class);
	
	public BusinessTreePanel() {
		publisherTree = new Tree(UDDIBrowser.images);
		publisherTree.addTreeListener(this);
		initWidget(publisherTree);
	}
	
	public void loadBusinesses() {
		getBusinesses("all");
	}
	
	public void findAllBusiness() {
		findAllBusinesses();
	}
	
	protected void getBusinesses(String infoSelection) {

		publicationService.getBusinesses(UDDIBrowser.getInstance().getToken(), infoSelection, new AsyncCallback<PublicationResponse>() 
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
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
		});
	}
	
	protected void findAllBusinesses() {

		String name = "%";
		String[] findQualifyers = new String[3];
		findQualifyers[0]="orLikeKeys";
		findQualifyers[1]="caseInsensitiveMatch";
		findQualifyers[2]="approximateMatch";
		
		findService.getBusinesses(name, findQualifyers, new AsyncCallback<FindResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(FindResponse response) {
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
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
		});
	}


	public void onTreeItemSelected(TreeItem treeItem) {
		System.out.println("Selected " + treeItem.getText());
		if (treeItem.getUserObject()!=null && Service.class.equals(treeItem.getUserObject().getClass())) {
			Service service = (Service) treeItem.getUserObject();
			UDDIBrowser.getInstance().getDetailPanel().setVisible(true);
			UDDIBrowser.getInstance().getDetailPanel().displayService(service.getKey());
		} else if (treeItem.getUserObject()!=null && Business.class.equals(treeItem.getUserObject().getClass())) {
			Business business = (Business) treeItem.getUserObject();
			UDDIBrowser.getInstance().getDetailPanel().setVisible(true);
			if (SERVICES_LABEL.equals(treeItem.getText())) {
				UDDIBrowser.getInstance().getDetailPanel().displayServices(business.getKey());
			} else {
				UDDIBrowser.getInstance().getDetailPanel().displayBusiness(business.getKey());
			}
		}
	}

	public void onTreeItemStateChanged(TreeItem arg0) {
		// TODO Auto-generated method stub
		System.out.println("StateChanged " + arg0.getText());
	}
	
}
