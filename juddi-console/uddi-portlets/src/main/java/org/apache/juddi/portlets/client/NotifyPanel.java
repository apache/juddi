package org.apache.juddi.portlets.client;

import org.apache.juddi.portlets.client.service.NotifyService;
import org.apache.juddi.portlets.client.service.NotifyServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 *
 */
public class NotifyPanel extends VerticalPanel {

	//private UDDISubscriptionNotification notification = null;
	//private NotifyPanel notifyPanel = null;
	private TextArea textArea = null;
	FlexTable table = null;

	private NotifyServiceAsync notifyService = (NotifyServiceAsync) GWT.create(NotifyService.class);
	
	public NotifyPanel(UDDISubscriptionNotification notification) {
		super();
		//this.notification = notification;
		//notifyPanel = this;
		getElement().setId("notifications-body");
		Label notifyLabel = new Label ("Subscription notifications:");
		notifyLabel.setStyleName("portlet-form-field-label");
		add(notifyLabel);
		
	    textArea = new TextArea();
	    textArea.setCharacterWidth(50);
	    textArea.setVisibleLines(50);
	    add(textArea);
	}
	
	public TextArea getTextArea() {
		return textArea;
	}
	
	public void setTextArea(TextArea textArea) {
		this.textArea = textArea;
	}
	
	public NotifyServiceAsync getSecurityService() {
		return notifyService;
	}

	public void setNotifyService(NotifyServiceAsync notifyService) {
		this.notifyService = notifyService;
	}

}
