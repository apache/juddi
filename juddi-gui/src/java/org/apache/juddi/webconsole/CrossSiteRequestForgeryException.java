/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.webconsole;

/**
 *
 * @author Alex O'Ree
 */
public class CrossSiteRequestForgeryException extends Exception {
    //Parameterless Constructor

    public CrossSiteRequestForgeryException() {
        super(msg, null);
    }

    //Constructor that accepts a message
    public CrossSiteRequestForgeryException(String message) {
        super(msg, null);
    }

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
