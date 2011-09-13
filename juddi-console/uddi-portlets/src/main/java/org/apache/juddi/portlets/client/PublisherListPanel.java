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

import java.util.ArrayList;
import java.util.List;

import org.apache.juddi.portlets.client.model.Publisher;
import org.apache.juddi.portlets.client.service.JUDDIApiResponse;
import org.apache.juddi.portlets.client.service.JUDDIApiService;
import org.apache.juddi.portlets.client.service.JUDDIApiServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;

public class PublisherListPanel extends Composite implements TableListener {

	private int selectedRow = -1;
	private String selectedPublisher = "";

	private FlexTable table = new FlexTable();
	private List<Publisher> publishers =  new ArrayList<Publisher>();

	private JUDDIApiServiceAsync juddiApiService = (JUDDIApiServiceAsync) GWT.create(JUDDIApiService.class);

	public PublisherListPanel() {

		table.setCellSpacing(0);
		table.setCellPadding(0);
		table.setWidth("100%");

		initWidget(table);
		// Hook up events.
		table.addTableListener(this);
		initTable();
		setStyleName("List");

	}

	private void initTable() {
		
		// Create the header row.
		table.setText(0, 0, "Publisher Id");
		table.setText(0, 1, "Publisher Name");
		table.getRowFormatter().setStyleName(0, "ListHeader");
		

		// Initialize the rest of the rows.
//		for (int i = 0; i < VISIBLE_PUBLISHER_COUNT; ++i) {
//			table.setText(i + 1, 0, "");
//			table.setText(i + 1, 1, "");
//			table.getCellFormatter().setWordWrap(i + 1, 0, false);
//			table.getCellFormatter().setWordWrap(i + 1, 1, false);
//			table.getFlexCellFormatter().setColSpan(i + 1, 1, 1);
//		}
//		selectRow(0);
	}

	/**
	 * Obtains a list of all publishers
	 * @param user
	 * @param password
	 */
	protected void listPublishers(final JUDDIPublisher juddiPublisher) {
		final String token = juddiPublisher.getToken();
		final String publisherId = juddiPublisher.getPublisherId();
		juddiPublisher.setIsAdmin(false);
		juddiApiService.getPublishers(token, publisherId,  new AsyncCallback<JUDDIApiResponse>() 
				{
			public void onFailure(Throwable caught) {
				Window.Location.reload();
			}

			public void onSuccess(JUDDIApiResponse response) {
				if (response.isSuccess()) {
					publishers = response.getPublishers();
					for (int row=1; row<table.getRowCount(); row++) {
						table.removeRow(row);
					}
					for (int i=0; i < publishers.size(); i++) {
						table.setText(i+1, 0, publishers.get(i).getAuthorizedName());
						table.setText(i+1, 1, publishers.get(i).getPublisherName());
						if (selectedRow==i+1 || selectedPublisher.equals(publishers.get(i).getAuthorizedName())) {
							selectRow(i+1);
						}
						if ( (publishers.get(i).getAuthorizedName().equals(publisherId) ) && 
								("true".equalsIgnoreCase(publishers.get(i).getIsAdmin())) ) {
							juddiPublisher.setIsAdmin(true);
						}
					}
				} else {
					Window.alert("error: " + response.getMessage());
				}
			}
				});
	}

	public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
		// Select the row that was clicked (-1 to account for header row).
		if (row > 0) {
			selectedPublisher="";
			selectedRow=row;
			JUDDIPublisher.getInstance().displayPublisher(publishers.get(row -1));
		}
	}

	protected void selectRow(int selectedRow) {
		for (int row=1; row < table.getRowCount(); row++) {
			styleRow(row, false);
		}
		this.selectedRow=selectedRow;
		if (selectedRow>0) {
			styleRow(selectedRow, true);
		}
	}

	private void styleRow(int row, boolean selected) {
		if (row != -1) {
			if (selected) {
				table.getRowFormatter().addStyleName(row, "SelectedRow");
			} else {
				table.getRowFormatter().removeStyleName(row, "SelectedRow");
			}
		}
	}

	public void setSelectedPublisher(String selectedPublisher) {
		this.selectedPublisher = selectedPublisher;
	}



}
