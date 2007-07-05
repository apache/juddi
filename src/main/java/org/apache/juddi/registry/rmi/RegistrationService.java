/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 */
package org.apache.juddi.registry.rmi;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author Kurt Stam (kurt.stam@redhat.com)
 */
public class RegistrationService extends HttpServlet
{
	public static Inquiry mInquery=null;
	public static Publish mPublish=null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException 
	{
		super.init();
		JNDIRegistration.register();
	}

  
}
