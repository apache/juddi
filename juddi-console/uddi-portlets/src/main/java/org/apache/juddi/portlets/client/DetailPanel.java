package org.apache.juddi.portlets.client;

import org.apache.juddi.portlets.client.model.Business;
import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.model.ServiceBinding;
import org.apache.juddi.portlets.client.service.InquiryResponse;
import org.apache.juddi.portlets.client.service.InquiryService;
import org.apache.juddi.portlets.client.service.InquiryServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class DetailPanel extends FlowPanel {

	private InquiryServiceAsync inquiryService = (InquiryServiceAsync) GWT.create(InquiryService.class); 
	private DetailPanel detailPanel = null;
	FlexTable table = null;

	public DetailPanel() {
		detailPanel = this;
	}
	
	public void displayServices( String businessKey) {
		inquiryService.getBusinessDetail(UDDIBrowser.getInstance().getToken(), businessKey, new AsyncCallback<InquiryResponse>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(InquiryResponse response) {
				if (response.isSuccess()) {
					Business business = response.getBusiness();
					if (table!=null) detailPanel.remove(table);
					table = new FlexTable();
					detailPanel.add(table);
					int row = 0;
					for (Service service : business.getServices()) {
						table.getFlexCellFormatter().setColSpan(row, 0, 2);
						table.setText(row++, 0, "service");
						table.setHTML(row, 0, UDDIBrowser.images.service().getHTML());
						table.setText(row++, 1, service.getName());
						table.setHTML(row, 0, UDDIBrowser.images.key().getHTML());
						table.setText(row++, 1, service.getKey());
						table.setHTML(row, 0, UDDIBrowser.images.description().getHTML());
						table.setHTML(row++, 1, service.getDescription());
					}
				} else {
					Window.alert("error: " + response.getMessage() 
							+ ". Make sure the UDDI service is up and running.");
				}
			}
		});
	}
	
	public void displayBusiness( String businessKey) {
		inquiryService.getBusinessDetail(UDDIBrowser.getInstance().getToken(), businessKey, new AsyncCallback<InquiryResponse>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(InquiryResponse response) {
				if (response.isSuccess()) {
					Business business = response.getBusiness();
					if (table!=null) detailPanel.remove(table);
					table = new FlexTable();
					detailPanel.add(table);
					//table.setBorderWidth(1);
					int row = 0;
					table.getFlexCellFormatter().setColSpan(row, 0, 2);
					table.setText(row++, 0, "business");
					table.setHTML(row, 0, UDDIBrowser.images.business().getHTML());
					table.setText(row++, 1, business.getName());
					table.setHTML(row, 0, UDDIBrowser.images.key().getHTML());
					table.setText(row++, 1, business.getKey());
					table.setHTML(row, 0, UDDIBrowser.images.description().getHTML());
					table.setText(row++, 1, business.getDescription());
					
					//Business Contact
					
				} else {
					Window.alert("error: " + response.getMessage() 
							+ ". Make sure the UDDI service is up and running.");
				}
			}
		});
	}

	public void displayService(String serviceKey) {

		inquiryService.getServiceDetail(UDDIBrowser.getInstance().getToken(), serviceKey, new AsyncCallback<InquiryResponse>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(InquiryResponse response) {
				if (response.isSuccess()) {
					Service service = response.getService();
					if (table!=null) detailPanel.remove(table);
					table = new FlexTable();
					detailPanel.add(table);
					//table.setBorderWidth(1);
					int row = 0;
					table.getFlexCellFormatter().setColSpan(row, 0, 2);
					table.setHTML(row++, 0, UDDIBrowser.images.service().getHTML() + " service - " + service.getName());
					table.setHTML(row, 0, UDDIBrowser.images.key().getHTML());
					table.setText(row++, 1, service.getKey());
					table.setHTML(row, 0, UDDIBrowser.images.description().getHTML());
					table.setText(row++, 1, service.getDescription());
					
					for (ServiceBinding serviceBinding : service.getServiceBindings()) {
						table.getFlexCellFormatter().setColSpan(row, 0, 2);
						table.setText(row++, 0, "binding");
						table.setHTML(row, 0, UDDIBrowser.images.key().getHTML());
						table.setText(row++, 1, serviceBinding.getKey());
						table.setHTML(row, 0, UDDIBrowser.images.description().getHTML());
						table.setText(row++, 1,  serviceBinding.getDescription());
						table.setText(row, 0, serviceBinding.getUrlType());
						table.setHTML(row++, 1, new HTML("<a href='" 
								+ serviceBinding.getAccessPoint() + "'>" 
								+ serviceBinding.getAccessPoint() + "</a>").getHTML());
					}
				} else {
					Window.alert("error: " + response.getMessage() 
							+ ". Make sure the UDDI service is up and running.");
				}
			}
		});
	}

}
