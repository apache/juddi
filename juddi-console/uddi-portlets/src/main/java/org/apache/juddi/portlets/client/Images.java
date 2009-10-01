package org.apache.juddi.portlets.client;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.TreeImages;

public interface Images extends  ImageBundle, TreeImages {

	AbstractImagePrototype key();
	AbstractImagePrototype business();
	AbstractImagePrototype description();
	AbstractImagePrototype service();
	AbstractImagePrototype services();
	AbstractImagePrototype bindingtemplate();
	AbstractImagePrototype endpointlive();
	AbstractImagePrototype node();
	AbstractImagePrototype subscription();
	AbstractImagePrototype down();
	AbstractImagePrototype up();
	
	//MenuBar
	AbstractImagePrototype create();
	AbstractImagePrototype save();
	AbstractImagePrototype delete();
	AbstractImagePrototype sync();
}
