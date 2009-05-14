package org.apache.juddi.portlets.client;

import java.util.List;

import org.apache.juddi.portlets.client.model.Business;
import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.model.ServiceBinding;
import org.apache.juddi.portlets.client.service.InquiryResponse;
import org.apache.juddi.portlets.client.service.NotifyService;
import org.apache.juddi.portlets.client.service.NotifyServiceAsync;
import org.apache.juddi.portlets.client.service.PublicationResponse;
import org.apache.juddi.portlets.client.service.PublicationService;
import org.apache.juddi.portlets.client.service.PublicationServiceAsync;
import org.apache.juddi.portlets.client.service.SecurityService;
import org.apache.juddi.portlets.client.service.SecurityServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NotifyPanel extends VerticalPanel {

	private UDDISubscriptionNotification notification = null;
	private NotifyPanel notifyPanel = null;
	FlexTable table = null;

	private NotifyServiceAsync notifyService = (NotifyServiceAsync) GWT.create(NotifyService.class);
	
	public NotifyPanel(UDDISubscriptionNotification notification) {
		super();
		this.notification = notification;
		notifyPanel = this;
		getElement().setId("notifications-body");
		Label notifyLabel = new Label ("Subscription notifications:");
		notifyLabel.setStyleName("portlet-form-field-label");
		add(notifyLabel);
		
	    TextArea ta = new TextArea();
	    ta.setCharacterWidth(50);
	    ta.setVisibleLines(50);
	    add(ta);
	}
	
	public NotifyServiceAsync getSecurityService() {
		return notifyService;
	}

	public void setNotifyService(NotifyServiceAsync notifyService) {
		this.notifyService = notifyService;
	}

}
