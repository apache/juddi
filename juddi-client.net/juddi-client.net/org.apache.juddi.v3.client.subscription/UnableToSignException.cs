using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.subscription
{
    [Serializable]
    public class UnableToSignException : Exception
    {

        public UnableToSignException() { }

        public UnableToSignException(string message)
            : base(message)
        {
        }
        public UnableToSignException(string message, Exception innerException)
            : base(message, innerException)
        {
        }
        protected UnableToSignException(SerializationInfo info, StreamingContext context)
            : base(info, context)
        {
        }
    }
}
