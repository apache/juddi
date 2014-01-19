/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
package org.apache.juddi.adminconsole;

/**
 * Provides a very basic, no stack trace exception, useful for throwing at a browser without revealing any details 
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class CrossSiteRequestForgeryException extends Exception {
    //Parameterless Constructor
/**
 * CrossSiteRequestForgeryException
 */
    public CrossSiteRequestForgeryException() {
        super(msg, null);
    }

    /**
     * Constructor that accepts a message
     */
    public CrossSiteRequestForgeryException(String message) {
        super(msg, null);
    }
/**
 * CrossSiteRequestForgeryException
 * @param message
 * @param cause 
 */
    public CrossSiteRequestForgeryException(String message, Throwable cause) {
        super(msg, null);
    }
    private static final String msg = "Cross Site Request Forgery";

    @Override
    public String toString() {
        return msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public String getLocalizedMessage() {
        return msg;
    }

    @Override
    public Throwable getCause() {
        return null;
    }
}
