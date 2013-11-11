using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.subscription
{
    class UnexpectedResponseException : Exception
    {
        private string p;
        private Exception ex;

        public UnexpectedResponseException(string p)
        {
            // TODO: Complete member initialization
            this.p = p;
        }

        public UnexpectedResponseException(string p, Exception ex)
        {
            // TODO: Complete member initialization
            this.p = p;
            this.ex = ex;
        }
    }
}
