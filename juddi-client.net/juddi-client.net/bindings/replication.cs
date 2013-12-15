using org.uddi.apiv3;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace org.uddi.repl_v3
{
    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class replicationConfiguration
    {

        private string serialNumberField;

        private string timeOfConfigurationUpdateField;

        private replicationConfigurationRegistryContact registryContactField;

        private @operator[] operatorField;

        private communicationGraph communicationGraphField;

        private string maximumTimeToSyncRegistryField;

        private string maximumTimeToGetChangesField;

        private SignatureType[] signatureField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "integer")]
        public string serialNumber
        {
            get
            {
                return this.serialNumberField;
            }
            set
            {
                this.serialNumberField = value;
            }
        }

        /// <remarks/>
        public string timeOfConfigurationUpdate
        {
            get
            {
                return this.timeOfConfigurationUpdateField;
            }
            set
            {
                this.timeOfConfigurationUpdateField = value;
            }
        }

        /// <remarks/>
        public replicationConfigurationRegistryContact registryContact
        {
            get
            {
                return this.registryContactField;
            }
            set
            {
                this.registryContactField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("operator")]
        public @operator[] @operator
        {
            get
            {
                return this.operatorField;
            }
            set
            {
                this.operatorField = value;
            }
        }

        /// <remarks/>
        public communicationGraph communicationGraph
        {
            get
            {
                return this.communicationGraphField;
            }
            set
            {
                this.communicationGraphField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "integer")]
        public string maximumTimeToSyncRegistry
        {
            get
            {
                return this.maximumTimeToSyncRegistryField;
            }
            set
            {
                this.maximumTimeToSyncRegistryField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "integer")]
        public string maximumTimeToGetChanges
        {
            get
            {
                return this.maximumTimeToGetChangesField;
            }
            set
            {
                this.maximumTimeToGetChangesField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("Signature", Namespace = "http://www.w3.org/2000/09/xmldsig#")]
        public SignatureType[] Signature
        {
            get
            {
                return this.signatureField;
            }
            set
            {
                this.signatureField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class replicationConfigurationRegistryContact
    {

        private contact contactField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public contact contact
        {
            get
            {
                return this.contactField;
            }
            set
            {
                this.contactField = value;
            }
        }
    }


    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class communicationGraph
    {

        private string[] nodeField;

        private string[] controlledMessageField;

        private communicationGraphEdge[] edgeField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("node", DataType = "anyURI")]
        public string[] node
        {
            get
            {
                return this.nodeField;
            }
            set
            {
                this.nodeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("controlledMessage")]
        public string[] controlledMessage
        {
            get
            {
                return this.controlledMessageField;
            }
            set
            {
                this.controlledMessageField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("edge")]
        public communicationGraphEdge[] edge
        {
            get
            {
                return this.edgeField;
            }
            set
            {
                this.edgeField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class communicationGraphEdge
    {

        private string[] messageField;

        private string messageSenderField;

        private string messageReceiverField;

        private string[] messageReceiverAlternateField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("message")]
        public string[] message
        {
            get
            {
                return this.messageField;
            }
            set
            {
                this.messageField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string messageSender
        {
            get
            {
                return this.messageSenderField;
            }
            set
            {
                this.messageSenderField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string messageReceiver
        {
            get
            {
                return this.messageReceiverField;
            }
            set
            {
                this.messageReceiverField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("messageReceiverAlternate", DataType = "anyURI")]
        public string[] messageReceiverAlternate
        {
            get
            {
                return this.messageReceiverAlternateField;
            }
            set
            {
                this.messageReceiverAlternateField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class @operator
    {

        private string operatorNodeIDField;

        private operatorStatus_type operatorStatusField;

        private contact[] contactField;

        private string soapReplicationURLField;

        private KeyInfoType[] keyInfoField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string operatorNodeID
        {
            get
            {
                return this.operatorNodeIDField;
            }
            set
            {
                this.operatorNodeIDField = value;
            }
        }

        /// <remarks/>
        public operatorStatus_type operatorStatus
        {
            get
            {
                return this.operatorStatusField;
            }
            set
            {
                this.operatorStatusField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("contact", Namespace = "urn:uddi-org:api_v3")]
        public contact[] contact
        {
            get
            {
                return this.contactField;
            }
            set
            {
                this.contactField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string soapReplicationURL
        {
            get
            {
                return this.soapReplicationURLField;
            }
            set
            {
                this.soapReplicationURLField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("KeyInfo", Namespace = "http://www.w3.org/2000/09/xmldsig#")]
        public KeyInfoType[] KeyInfo
        {
            get
            {
                return this.keyInfoField;
            }
            set
            {
                this.keyInfoField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:repl_v3")]
    public enum operatorStatus_type
    {

        /// <remarks/>
        @new,

        /// <remarks/>
        normal,

        /// <remarks/>
        resigned,
    }


}
