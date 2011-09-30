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
	private static final String OTHER_BINDING_TYPE = "other";
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
				Window.Location.reload();
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
					Window.alert("error: " + response.getMessage());
				}
			}
		});
	}
	
	public void displayBusiness( String businessKey) {
		inquiryService.getBusinessDetail(UDDIBrowser.getInstance().getToken(), businessKey, new AsyncCallback<InquiryResponse>() {
			public void onFailure(Throwable caught) {
				Window.Location.reload();
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
					Window.alert("error: " + response.getMessage());
				}
			}
		});
	}

	public void displayService(String serviceKey) {

		inquiryService.getServiceDetail(UDDIBrowser.getInstance().getToken(), serviceKey, new AsyncCallback<InquiryResponse>() {
			public void onFailure(Throwable caught) {
				Window.Location.reload();
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
						
						// For JBossESB or others using "other" as UrlType, don't display the accesspoint as 
						// http URL
						if (OTHER_BINDING_TYPE.equals(serviceBinding.getUrlType())) {
							bindingtable.setHTML(bindingRow++, 1, serviceBinding.getUrlType() + ":" 
									+  new HTML(serviceBinding.getAccessPoint()).getHTML());							
						} else {
							bindingtable.setHTML(bindingRow++, 1, serviceBinding.getUrlType() + ":" 
								+  new HTML("<a href='" 
								+ serviceBinding.getAccessPoint() + "'>" 
								+ serviceBinding.getAccessPoint() + "</a>").getHTML());
						}
						table.setHTML(row++, 1, bindingtable.toString());
					}
				} else {
					Window.alert("error: " + response.getMessage());
					Window.Location.reload();
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
