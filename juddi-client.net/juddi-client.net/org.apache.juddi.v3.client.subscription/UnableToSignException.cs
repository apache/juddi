using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.subscription
{
    class UnableToSignException : Exception
    {
        private Exception ex;

        public UnableToSignException(Exception ex)
        {
            
        }
        
    }
}
