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
