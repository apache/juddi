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
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;

public class DetailPanel  extends FlowPanel  implements TableListener{

	private InquiryServiceAsync inquiryService = (InquiryServiceAsync) GWT.create(InquiryService.class); 
	private DetailPanel detailPanel = null;
	FlexTable table = new FlexTable();;

	public DetailPanel() {
		detailPanel = this;
		detailPanel.setStyleName("detail-panel");
		detailPanel.setWidth("100%");
		table.addTableListener(this);
		detailPanel.add(table);
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
					table.addTableListener(detailPanel);
					detailPanel.add(table);
					int row = 0;
					for (Service service : business.getServices()) {
						table.getCellFormatter().addStyleName(row, 1, "ListHeader");
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
					table.addTableListener(detailPanel);
					int row = 0;
					table.getCellFormatter().addStyleName(row, 1, "ListHeader");
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
					table.setWidth("100%");
					table.setCellSpacing(0);
					table.setCellPadding(4);
					detailPanel.add(table);
					table.addTableListener(detailPanel);
					int row = 0;
					table.getCellFormatter().addStyleName(row, 1, "ListHeader");
					table.setHTML(row, 0, UDDIBrowser.images.service().getHTML());
					table.setHTML(row++, 1, service.getName());
					table.setHTML(row, 0, UDDIBrowser.images.key().getHTML());
					table.setText(row++, 1, service.getKey());
					table.setHTML(row, 0, UDDIBrowser.images.description().getHTML());
					table.setText(row++, 1, service.getDescription());
					
					for (ServiceBinding serviceBinding : service.getServiceBindings()) {
						
						table.setHTML(row, 0, UDDIBrowser.images.bindingtemplate().getHTML());
						table.setText(row++, 1, "Binding Template");
						
						int bindingRow = 0;
						FlexTable bindingtable = new FlexTable();
						bindingtable.setHTML(bindingRow, 0, UDDIBrowser.images.key().getHTML());
						bindingtable.setText(bindingRow++, 1, serviceBinding.getKey());
						bindingtable.setHTML(bindingRow, 0, UDDIBrowser.images.description().getHTML());
						bindingtable.setText(bindingRow++, 1,  serviceBinding.getDescription());
						bindingtable.setHTML(bindingRow, 0, UDDIBrowser.images.endpointlive().getHTML());
						bindingtable.setHTML(bindingRow++, 1, new HTML("<a href='" 
								+ serviceBinding.getAccessPoint() + "'>" 
								+ serviceBinding.getAccessPoint() + "</a>").getHTML());
						table.setHTML(row, 1, bindingtable.toString());
					}
				} else {
					Window.alert("error: " + response.getMessage() 
							+ ". Make sure the UDDI service is up and running.");
				}
			}
		});
	}

	public void onCellClicked(SourcesTableEvents arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		//if (table.getTitle().equalsIgnoreCase("service"))
		System.out.println("title=" + table.getTitle());
		String text = table.getText(arg1, arg2);
		System.out.println("text=" + text);
		
	}

}
