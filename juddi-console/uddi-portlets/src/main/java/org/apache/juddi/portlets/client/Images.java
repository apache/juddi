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
	AbstractImagePrototype logout();
	AbstractImagePrototype sync();
	AbstractImagePrototype manager();
}
