using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.subscription
{
   
    [Serializable]
    public class RegistrationAbortedException : Exception
    {

        public RegistrationAbortedException() { }

        public RegistrationAbortedException(string message)
            : base(message)
        {
        }
        public RegistrationAbortedException(string message, Exception innerException)
            : base(message, innerException)
        {
        }
        protected RegistrationAbortedException(SerializationInfo info, StreamingContext context)
            : base(info, context)
        {
        }
    }
}
