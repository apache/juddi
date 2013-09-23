using System;
using System.Collections.Generic;

using System.Text;

namespace org.apache.juddi.client.org.apache.juddi.v3.client.crypto
{
    /// <summary>
    /// A utility class for signing and verifying JAXB Objects, such as UDDI  entities.   
    /// Notes: This class only supports elements that are signed once. 
    /// Multiple signature are not currently supported.
    /// </summary>
    /// <author><a href="mailto:alexoree@apache.org">Alex O'Ree</a></author> 
    public class DigSigUtil
    {
        /// <summary>
        ///
        /// Verifies the signature on an enveloped digital signature on a UDDI
        /// entity, such as a business, service, tmodel or binding template. <br><Br>
        /// It is expected that either the public key of the signing certificate is
        /// included within the signature keyinfo section OR that sufficient
        /// information is provided in the signature to reference a public key
        /// located within the Trust Store provided<br><Br> Optionally, this function
        /// also validate the signing certificate using the options provided to the
        /// configuration map.

        /// </summary>
        /// <param name="obj"></param>
        /// <param name="OutErrorMessage"></param>
        /// <returns></returns>
        public bool verifySignedUddiEntity(Object obj, out String OutErrorMessage)
        {
            OutErrorMessage = "";
            return false;
        }
    }
}
