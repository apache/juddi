package org.apache.juddi.portlets.client;

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
	private UDDIBrowser browser = null;
	private FlexTable table = null;

	public DetailPanel(UDDIBrowser browser) {
		this.browser = browser;
		table = new FlexTable();
		table.setTitle("Service");
		this.add(table);
	}

	public void displayService(String key) {
		System.out.println("Going to fetch the Service and Display it");
		getService(browser.getToken(), key);

	}

	private void getService(String token, String serviceKey) {

		inquiryService.getServiceDetail(token, serviceKey, new AsyncCallback<InquiryResponse>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(InquiryResponse response) {
				if (response.isSuccess()) {
					Service service = response.getService();
					
					table.setText(0, 0, service.getName());
					table.getFlexCellFormatter().setColSpan(0, 0, 2);
					table.setHTML(1, 0, UDDIBrowser.images.key().getHTML() + " " + service.getKey());
					table.getFlexCellFormatter().setColSpan(1, 0, 2);
					table.setHTML(2, 0, UDDIBrowser.images.description().getHTML() + " " + service.getDescription());
					table.getFlexCellFormatter().setColSpan(2, 0, 2);
					int row = 4;
					for (ServiceBinding serviceBinding : service.getServiceBindings()) {
						table.setText(row, 0, "EPR");
						table.setHTML(row, 1, new HTML("<a href='" + serviceBinding.getAccessPoint() + "'>" + serviceBinding.getAccessPoint() + "</a>").getHTML());
						table.setText(++row, 0, "Description");
						table.setText(row++, 1,  serviceBinding.getDescription());
					}
				} else {
					Window.alert("error: " + response.getMessage() 
							+ ". Make sure the UDDI service is up and running.");
				}
			}
		});
	}

}
