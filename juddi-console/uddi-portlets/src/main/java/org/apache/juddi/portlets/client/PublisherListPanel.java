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

	private static final int VISIBLE_PUBLISHER_COUNT = 10;
	private int selectedRow = -1;
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
		for (int i = 0; i < VISIBLE_PUBLISHER_COUNT; ++i) {
			table.setText(i + 1, 0, "");
			table.setText(i + 1, 1, "");
			table.getCellFormatter().setWordWrap(i + 1, 0, false);
			table.getCellFormatter().setWordWrap(i + 1, 1, false);
			table.getFlexCellFormatter().setColSpan(i + 1, 1, 1);
		}
	}
	
	/**
	 * Obtains an authenticationToken
	 * @param user
	 * @param password
	 */
	protected List<Publisher> listPublishers(String token, String publisherId) {
		juddiApiService.getPublishers(token, publisherId,  new AsyncCallback<JUDDIApiResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry. " + caught.getMessage());
			}

			public void onSuccess(JUDDIApiResponse response) {
				if (response.isSuccess()) {
					publishers = response.getPublishers();
					for (int i=0; i < publishers.size(); i++) {
						table.setText(i+1, 0, publishers.get(i).getAuthorizedName());
						table.setText(i+1, 1, publishers.get(i).getPublisherName());
					}
				} else {
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
		});
		return publishers;
	}
	
	public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
		// Select the row that was clicked (-1 to account for header row).
	    if (row > 0) {
	      selectRow(row);
	    }
	}
	
	 private void selectRow(int row) {
	    // When a row (other than the first one, which is used as a header) is
	    // selected, display its associated MailItem.
	    //MailItem item = MailItems.getMailItem(startIndex + row);
	    //if (item == null) {
	    //  return;
	    //}

	    styleRow(selectedRow, false);
	    styleRow(row, true);

	    //item.read = true;
	    selectedRow = row;
	    
	   JUDDIPublisher.getInstance().displayPublisher(publishers.get(row -1));
	    //Mail.get().displayItem(item);
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
	
	
	
}
