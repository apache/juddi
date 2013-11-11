using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.subscription
{
    class RegistrationAbortedException : Exception
    {
        private string p;

        public RegistrationAbortedException(string p)
        {
            // TODO: Complete member initialization
            this.p = p;
        }
    }
}
