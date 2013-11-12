using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.subscription
{
   
    [Serializable]
    public class ServiceAlreadyStartedException : Exception
    {

        public ServiceAlreadyStartedException() { }

        public ServiceAlreadyStartedException(string message)
            : base(message)
        {
        }
        public ServiceAlreadyStartedException(string message, Exception innerException)
            : base(message, innerException)
        {
        }
        protected ServiceAlreadyStartedException(SerializationInfo info, StreamingContext context)
            : base(info, context)
        {
        }
    }
}
