/*
 * Copyright 2014 The Apache Software Foundation.
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
package org.apache.juddi.webconsole;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Alex O'Ree
 */
public class YesCacheFilter implements Filter {
        
        @Override
        public void init(FilterConfig fc) throws ServletException {
                
        }
        
        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
                HttpServletResponse hsr = (HttpServletResponse) res;
                hsr.setHeader("Cache-Control", "public, max-age=86400"); // HTTP 1.1.
                Calendar c = new GregorianCalendar();
                c.add(Calendar.MONTH, -1);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
                
                hsr.setHeader("Last-Modified", sdf.format(c.getTime())); // HTTP 1.0.
                hsr.setDateHeader("Expires", System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000)); // 1 month.
                chain.doFilter(req, res);
                
        }
        
        @Override
        public void destroy() {
                
        }
        
}
