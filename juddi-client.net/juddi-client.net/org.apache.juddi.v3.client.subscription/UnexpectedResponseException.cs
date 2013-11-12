using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.subscription
{
   
    [Serializable]
    public class UnexpectedResponseException : Exception
    {

        public UnexpectedResponseException() { }

        public UnexpectedResponseException(string message)
            : base(message)
        {
        }
        public UnexpectedResponseException(string message, Exception innerException)
            : base(message, innerException)
        {
        }
        protected UnexpectedResponseException(SerializationInfo info, StreamingContext context)
            : base(info, context)
        {
        }
    }
}
