/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.webconsole.hub;

/**
 * This simple class enables use to pass rendered html data and paging
 * information back and forth from jsp context to java
 *
 * @author Alex O'Ree
 */
public class PagableContainer {

    public int offset = 0;
    public int totalrecords = 0;
    public int displaycount = 0;
    public String renderedHtml = "";
}
