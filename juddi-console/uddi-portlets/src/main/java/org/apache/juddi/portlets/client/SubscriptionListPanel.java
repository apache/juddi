package org.apache.juddi.portlets.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.juddi.portlets.client.model.Subscription;
import org.apache.juddi.portlets.client.service.SubscriptionResponse;
import org.apache.juddi.portlets.client.service.SubscriptionService;
import org.apache.juddi.portlets.client.service.SubscriptionServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;

public class SubscriptionListPanel extends Composite implements TableListener {

	private static final int VISIBLE_PUBLISHER_COUNT = 10;
	private int selectedRow = -1;
	private String selectedSubscription = "";

	private FlexTable table = new FlexTable();
	private List<Subscription> subscriptions =  new ArrayList<Subscription>();

	private SubscriptionServiceAsync subscriptionServiceAsync = (SubscriptionServiceAsync) GWT.create(SubscriptionService.class);

	public SubscriptionListPanel() {

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
		table.setText(0, 0, "Binding Key");
		table.setText(0, 1, "Subscription Key");
		table.getRowFormatter().setStyleName(0, "ListHeader");

//		// Initialize the rest of the rows.
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
	 * Obtains list of Subscriptions
	 * @param token
	 */
	protected List<Subscription> listSubscriptions(String token) {
		subscriptionServiceAsync.getSubscriptions(token,  new AsyncCallback<SubscriptionResponse>() 
				{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry. " + caught.getMessage());
			}

			public void onSuccess(SubscriptionResponse response) {
				if (response.isSuccess()) {
					subscriptions = response.getSubscriptions();
					for (int row=1; row<table.getRowCount(); row++) {
						table.removeRow(row);
					}
					for (int i=0; i < subscriptions.size(); i++) {
						table.setText(i+1, 0, subscriptions.get(i).getBindingKey());
						table.setText(i+1, 1, subscriptions.get(i).getSubscriptionKey());
						//if (selectedRow==i+1 || selectedSubscription.equals(subscriptions.get(i).getAuthorizedName())) {
						//	selectRow(i+1);
						//}
					}
				} else {
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
				});
		return subscriptions;
	}

	public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
		// Select the row that was clicked (-1 to account for header row).
		if (row > 0) {
			selectedSubscription="";
			selectedRow=row;
			UDDISubscription.getInstance().displaySubscription(subscriptions.get(row -1));
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

	public void setSelectedSubscription(String selectedSubscription) {
		this.selectedSubscription = selectedSubscription;
	}



}
