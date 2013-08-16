using System;
using System.Collections.Generic;
using System.Reflection;
using System.Text;

namespace juddi_dotnet.org.apache.juddi.v3.client
{
    public class Release
    {
        private static readonly String UDDI_VERSION = "3.0";
        private static readonly String JAR_NAME = "juddi-client";
        private static String registryVersion = null;

        public static String getRegistryVersion()
        {
            if (registryVersion == null)
            {
                registryVersion = getVersionFromManifest(JAR_NAME);
            }
            return registryVersion;

        }

        public static String getUDDIVersion()
        {
            return UDDI_VERSION;
        }

        public static String getVersionFromManifest(String jarName)
        {
            try
            {
                return Assembly.GetExecutingAssembly().GetName().Version.ToString();
            }
            catch { }
            return "unknown";
        }
    }
}
