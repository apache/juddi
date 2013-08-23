/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

/// extends the generated uddi-client codebase, which is generated via the xsd tool from juddi-client.xsd in the Java project
///<author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
namespace org.apache.juddi.v3.client.config
{

    public partial class uddi
    {
        Properties p = new Properties();
        public  Properties getProperties()
        {
            return p;
        }

        public void setProperties(Properties props)
        {
            p = props;
        }
    }
}
