/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi;

import java.net.URL;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */

public class ClassUtil {
   
	public static Class<?> forName(String name, Class<?> caller)
    	throws ClassNotFoundException
    {
	    ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
	    if (threadClassLoader != null) {
	        try {
	            return Class.forName(name, true, threadClassLoader) ;
	        } catch (ClassNotFoundException cnfe) {
	        	if (cnfe.getException() != null) {
	        		throw cnfe;
	        	}
	        }
	    }
    
	    ClassLoader callerClassLoader = caller.getClassLoader();
	    if (callerClassLoader != null) {
	        try {
	            return Class.forName(name, true, callerClassLoader) ;
	        } catch (final ClassNotFoundException cnfe) {
	            if (cnfe.getException() != null) {
	                throw cnfe ;
	            }
	        }
	    }
	    
	    return Class.forName(name, true, ClassLoader.getSystemClassLoader()) ;
    }
	
	public static URL getResource(String name, Class<?> caller)
	{
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		if (threadClassLoader != null) {

			URL url = threadClassLoader.getResource(name);

			if (url != null)
				return url;

		}

		ClassLoader callerClassLoader = caller.getClassLoader();

		return callerClassLoader.getResource(name);
	}
}
