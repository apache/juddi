using System;
using System.Collections.Generic;
using System.Text;

namespace org.uddi.apiv3
{

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class find_binding
    {

        private string authInfoField;

        private string[] findQualifiersField;

        private string[] tModelBagField;

        private find_tModel find_tModelField;

        private categoryBag categoryBagField;

        private int maxRowsField;

        private bool maxRowsFieldSpecified;

        private string serviceKeyField;

        private int listHeadField;

        private bool listHeadFieldSpecified;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("findQualifier", IsNullable = false)]
        public string[] findQualifiers
        {
            get
            {
                return this.findQualifiersField;
            }
            set
            {
                this.findQualifiersField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("tModelKey", DataType = "anyURI", IsNullable = false)]
        public string[] tModelBag
        {
            get
            {
                return this.tModelBagField;
            }
            set
            {
                this.tModelBagField = value;
            }
        }

        /// <remarks/>
        public find_tModel find_tModel
        {
            get
            {
                return this.find_tModelField;
            }
            set
            {
                this.find_tModelField = value;
            }
        }

        /// <remarks/>
        public categoryBag categoryBag
        {
            get
            {
                return this.categoryBagField;
            }
            set
            {
                this.categoryBagField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int maxRows
        {
            get
            {
                return this.maxRowsField;
            }
            set
            {
                this.maxRowsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool maxRowsSpecified
        {
            get
            {
                return this.maxRowsFieldSpecified;
            }
            set
            {
                this.maxRowsFieldSpecified = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string serviceKey
        {
            get
            {
                return this.serviceKeyField;
            }
            set
            {
                this.serviceKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int listHead
        {
            get
            {
                return this.listHeadField;
            }
            set
            {
                this.listHeadField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool listHeadSpecified
        {
            get
            {
                return this.listHeadFieldSpecified;
            }
            set
            {
                this.listHeadFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class find_tModel
    {

        private string authInfoField;

        private string[] findQualifiersField;

        private name nameField;

        private keyedReference[] identifierBagField;

        private categoryBag categoryBagField;

        private int maxRowsField;

        private bool maxRowsFieldSpecified;

        private int listHeadField;

        private bool listHeadFieldSpecified;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("findQualifier", IsNullable = false)]
        public string[] findQualifiers
        {
            get
            {
                return this.findQualifiersField;
            }
            set
            {
                this.findQualifiersField = value;
            }
        }

        /// <remarks/>
        public name name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public keyedReference[] identifierBag
        {
            get
            {
                return this.identifierBagField;
            }
            set
            {
                this.identifierBagField = value;
            }
        }

        /// <remarks/>
        public categoryBag categoryBag
        {
            get
            {
                return this.categoryBagField;
            }
            set
            {
                this.categoryBagField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int maxRows
        {
            get
            {
                return this.maxRowsField;
            }
            set
            {
                this.maxRowsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool maxRowsSpecified
        {
            get
            {
                return this.maxRowsFieldSpecified;
            }
            set
            {
                this.maxRowsFieldSpecified = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int listHead
        {
            get
            {
                return this.listHeadField;
            }
            set
            {
                this.listHeadField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool listHeadSpecified
        {
            get
            {
                return this.listHeadFieldSpecified;
            }
            set
            {
                this.listHeadFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class name
    {
        private string langField;

        private string valueField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://www.w3.org/XML/1998/namespace")]
        public string lang
        {
            get
            {
                return this.langField;
            }
            set
            {
                this.langField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:subr_v3")]
    public partial class notify_subscriptionListener
    {

        private string authInfoField;

        private subscriptionResultsList subscriptionResultsListField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:sub_v3")]
        public subscriptionResultsList subscriptionResultsList
        {
            get
            {
                return this.subscriptionResultsListField;
            }
            set
            {
                this.subscriptionResultsListField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:sub_v3")]
    public partial class subscriptionResultsList
    {

        private string chunkTokenField;

        private coveragePeriod coveragePeriodField;

        private subscription subscriptionField;

        private object itemField;

        private keyBag1[] keyBagField;

        private bool someResultsUnavailableField;

        private bool someResultsUnavailableFieldSpecified;

        /// <remarks/>
        public string chunkToken
        {
            get
            {
                return this.chunkTokenField;
            }
            set
            {
                this.chunkTokenField = value;
            }
        }

        /// <remarks/>
        public coveragePeriod coveragePeriod
        {
            get
            {
                return this.coveragePeriodField;
            }
            set
            {
                this.coveragePeriodField = value;
            }
        }

        /// <remarks/>
        public subscription subscription
        {
            get
            {
                return this.subscriptionField;
            }
            set
            {
                this.subscriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("assertionStatusReport", typeof(assertionStatusReport), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("bindingDetail", typeof(bindingDetail), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("businessDetail", typeof(businessDetail), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("businessList", typeof(businessList), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("relatedBusinessesList", typeof(relatedBusinessesList), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("serviceDetail", typeof(serviceDetail), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("serviceList", typeof(serviceList), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("tModelDetail", typeof(tModelDetail), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("tModelList", typeof(tModelList), Namespace = "urn:uddi-org:api_v3")]
        public object Item
        {
            get
            {
                return this.itemField;
            }
            set
            {
                this.itemField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("keyBag")]
        public keyBag1[] keyBag
        {
            get
            {
                return this.keyBagField;
            }
            set
            {
                this.keyBagField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool someResultsUnavailable
        {
            get
            {
                return this.someResultsUnavailableField;
            }
            set
            {
                this.someResultsUnavailableField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool someResultsUnavailableSpecified
        {
            get
            {
                return this.someResultsUnavailableFieldSpecified;
            }
            set
            {
                this.someResultsUnavailableFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:sub_v3")]
    public partial class coveragePeriod
    {

        private System.DateTime startPointField;

        private bool startPointFieldSpecified;

        private System.DateTime endPointField;

        private bool endPointFieldSpecified;

        /// <remarks/>
        public System.DateTime startPoint
        {
            get
            {
                return this.startPointField;
            }
            set
            {
                this.startPointField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool startPointSpecified
        {
            get
            {
                return this.startPointFieldSpecified;
            }
            set
            {
                this.startPointFieldSpecified = value;
            }
        }

        /// <remarks/>
        public System.DateTime endPoint
        {
            get
            {
                return this.endPointField;
            }
            set
            {
                this.endPointField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool endPointSpecified
        {
            get
            {
                return this.endPointFieldSpecified;
            }
            set
            {
                this.endPointFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:sub_v3")]
    public partial class subscription
    {

        private string subscriptionKeyField;

        private subscriptionFilter subscriptionFilterField;

        private string bindingKeyField;

        private string notificationIntervalField;

        private int maxEntitiesField;

        private bool maxEntitiesFieldSpecified;

        private System.DateTime expiresAfterField;

        private bool expiresAfterFieldSpecified;

        private bool briefField;

        private bool briefFieldSpecified;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string subscriptionKey
        {
            get
            {
                return this.subscriptionKeyField;
            }
            set
            {
                this.subscriptionKeyField = value;
            }
        }

        /// <remarks/>
        public subscriptionFilter subscriptionFilter
        {
            get
            {
                return this.subscriptionFilterField;
            }
            set
            {
                this.subscriptionFilterField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        public string bindingKey
        {
            get
            {
                return this.bindingKeyField;
            }
            set
            {
                this.bindingKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "duration")]
        public string notificationInterval
        {
            get
            {
                return this.notificationIntervalField;
            }
            set
            {
                this.notificationIntervalField = value;
            }
        }

        /// <remarks/>
        public int maxEntities
        {
            get
            {
                return this.maxEntitiesField;
            }
            set
            {
                this.maxEntitiesField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool maxEntitiesSpecified
        {
            get
            {
                return this.maxEntitiesFieldSpecified;
            }
            set
            {
                this.maxEntitiesFieldSpecified = value;
            }
        }

        /// <remarks/>
        public System.DateTime expiresAfter
        {
            get
            {
                return this.expiresAfterField;
            }
            set
            {
                this.expiresAfterField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool expiresAfterSpecified
        {
            get
            {
                return this.expiresAfterFieldSpecified;
            }
            set
            {
                this.expiresAfterFieldSpecified = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool brief
        {
            get
            {
                return this.briefField;
            }
            set
            {
                this.briefField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool briefSpecified
        {
            get
            {
                return this.briefFieldSpecified;
            }
            set
            {
                this.briefFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:sub_v3")]
    public partial class subscriptionFilter
    {

        private object itemField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("find_binding", typeof(find_binding), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("find_business", typeof(find_business), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("find_relatedBusinesses", typeof(find_relatedBusinesses), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("find_service", typeof(find_service), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("find_tModel", typeof(find_tModel), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("get_assertionStatusReport", typeof(get_assertionStatusReport), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("get_bindingDetail", typeof(get_bindingDetail), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("get_businessDetail", typeof(get_businessDetail), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("get_serviceDetail", typeof(get_serviceDetail), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("get_tModelDetail", typeof(get_tModelDetail), Namespace = "urn:uddi-org:api_v3")]
        public object Item
        {
            get
            {
                return this.itemField;
            }
            set
            {
                this.itemField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class find_business
    {

        private string authInfoField;

        private string[] findQualifiersField;

        private name[] nameField;

        private keyedReference[] identifierBagField;

        private categoryBag categoryBagField;

        private string[] tModelBagField;

        private find_tModel find_tModelField;

        private discoveryURL[] discoveryURLsField;

        private find_relatedBusinesses find_relatedBusinessesField;

        private int maxRowsField;

        private bool maxRowsFieldSpecified;

        private int listHeadField;

        private bool listHeadFieldSpecified;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("findQualifier", IsNullable = false)]
        public string[] findQualifiers
        {
            get
            {
                return this.findQualifiersField;
            }
            set
            {
                this.findQualifiersField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("name")]
        public name[] name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public keyedReference[] identifierBag
        {
            get
            {
                return this.identifierBagField;
            }
            set
            {
                this.identifierBagField = value;
            }
        }

        /// <remarks/>
        public categoryBag categoryBag
        {
            get
            {
                return this.categoryBagField;
            }
            set
            {
                this.categoryBagField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("tModelKey", DataType = "anyURI", IsNullable = false)]
        public string[] tModelBag
        {
            get
            {
                return this.tModelBagField;
            }
            set
            {
                this.tModelBagField = value;
            }
        }

        /// <remarks/>
        public find_tModel find_tModel
        {
            get
            {
                return this.find_tModelField;
            }
            set
            {
                this.find_tModelField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public discoveryURL[] discoveryURLs
        {
            get
            {
                return this.discoveryURLsField;
            }
            set
            {
                this.discoveryURLsField = value;
            }
        }

        /// <remarks/>
        public find_relatedBusinesses find_relatedBusinesses
        {
            get
            {
                return this.find_relatedBusinessesField;
            }
            set
            {
                this.find_relatedBusinessesField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int maxRows
        {
            get
            {
                return this.maxRowsField;
            }
            set
            {
                this.maxRowsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool maxRowsSpecified
        {
            get
            {
                return this.maxRowsFieldSpecified;
            }
            set
            {
                this.maxRowsFieldSpecified = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int listHead
        {
            get
            {
                return this.listHeadField;
            }
            set
            {
                this.listHeadField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool listHeadSpecified
        {
            get
            {
                return this.listHeadFieldSpecified;
            }
            set
            {
                this.listHeadFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class keyedReference
    {

        private string tModelKeyField;

        private string keyNameField;

        private string keyValueField;

        public keyedReference()
        {
            this.keyNameField = "";
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string keyName
        {
            get
            {
                return this.keyNameField;
            }
            set
            {
                this.keyNameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string keyValue
        {
            get
            {
                return this.keyValueField;
            }
            set
            {
                this.keyValueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class categoryBag
    {

        private object[] itemsField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("keyedReference", typeof(keyedReference))]
        [System.Xml.Serialization.XmlElementAttribute("keyedReferenceGroup", typeof(keyedReferenceGroup))]
        public object[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class keyedReferenceGroup
    {

        private keyedReference[] keyedReferenceField;

        private string tModelKeyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("keyedReference")]
        public keyedReference[] keyedReference
        {
            get
            {
                return this.keyedReferenceField;
            }
            set
            {
                this.keyedReferenceField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class discoveryURL
    {

        private string useTypeField;

        private string valueField;

        public discoveryURL()
        {
            this.useTypeField = "";
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string useType
        {
            get
            {
                return this.useTypeField;
            }
            set
            {
                this.useTypeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute(DataType = "anyURI")]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class find_relatedBusinesses
    {

        private string authInfoField;

        private string[] findQualifiersField;

        private string itemField;

        private ItemChoiceType itemElementNameField;

        private keyedReference keyedReferenceField;

        private int maxRowsField;

        private bool maxRowsFieldSpecified;

        private int listHeadField;

        private bool listHeadFieldSpecified;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("findQualifier", IsNullable = false)]
        public string[] findQualifiers
        {
            get
            {
                return this.findQualifiersField;
            }
            set
            {
                this.findQualifiersField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("businessKey", typeof(string), DataType = "anyURI")]
        [System.Xml.Serialization.XmlElementAttribute("fromKey", typeof(string), DataType = "anyURI")]
        [System.Xml.Serialization.XmlElementAttribute("toKey", typeof(string), DataType = "anyURI")]
        [System.Xml.Serialization.XmlChoiceIdentifierAttribute("ItemElementName")]
        public string Item
        {
            get
            {
                return this.itemField;
            }
            set
            {
                this.itemField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public ItemChoiceType ItemElementName
        {
            get
            {
                return this.itemElementNameField;
            }
            set
            {
                this.itemElementNameField = value;
            }
        }

        /// <remarks/>
        public keyedReference keyedReference
        {
            get
            {
                return this.keyedReferenceField;
            }
            set
            {
                this.keyedReferenceField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int maxRows
        {
            get
            {
                return this.maxRowsField;
            }
            set
            {
                this.maxRowsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool maxRowsSpecified
        {
            get
            {
                return this.maxRowsFieldSpecified;
            }
            set
            {
                this.maxRowsFieldSpecified = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int listHead
        {
            get
            {
                return this.listHeadField;
            }
            set
            {
                this.listHeadField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool listHeadSpecified
        {
            get
            {
                return this.listHeadFieldSpecified;
            }
            set
            {
                this.listHeadFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3", IncludeInSchema = false)]
    public enum ItemChoiceType
    {

        /// <remarks/>
        businessKey,

        /// <remarks/>
        fromKey,

        /// <remarks/>
        toKey,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class find_service
    {

        private string authInfoField;

        private string[] findQualifiersField;

        private name[] nameField;

        private categoryBag categoryBagField;

        private string[] tModelBagField;

        private find_tModel find_tModelField;

        private int maxRowsField;

        private bool maxRowsFieldSpecified;

        private string businessKeyField;

        private int listHeadField;

        private bool listHeadFieldSpecified;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("findQualifier", IsNullable = false)]
        public string[] findQualifiers
        {
            get
            {
                return this.findQualifiersField;
            }
            set
            {
                this.findQualifiersField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("name")]
        public name[] name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        public categoryBag categoryBag
        {
            get
            {
                return this.categoryBagField;
            }
            set
            {
                this.categoryBagField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("tModelKey", DataType = "anyURI", IsNullable = false)]
        public string[] tModelBag
        {
            get
            {
                return this.tModelBagField;
            }
            set
            {
                this.tModelBagField = value;
            }
        }

        /// <remarks/>
        public find_tModel find_tModel
        {
            get
            {
                return this.find_tModelField;
            }
            set
            {
                this.find_tModelField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int maxRows
        {
            get
            {
                return this.maxRowsField;
            }
            set
            {
                this.maxRowsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool maxRowsSpecified
        {
            get
            {
                return this.maxRowsFieldSpecified;
            }
            set
            {
                this.maxRowsFieldSpecified = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int listHead
        {
            get
            {
                return this.listHeadField;
            }
            set
            {
                this.listHeadField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool listHeadSpecified
        {
            get
            {
                return this.listHeadFieldSpecified;
            }
            set
            {
                this.listHeadFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class get_assertionStatusReport
    {

        private string authInfoField;

        private completionStatus completionStatusField;

        private bool completionStatusFieldSpecified;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        public completionStatus completionStatus
        {
            get
            {
                return this.completionStatusField;
            }
            set
            {
                this.completionStatusField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool completionStatusSpecified
        {
            get
            {
                return this.completionStatusFieldSpecified;
            }
            set
            {
                this.completionStatusFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public enum completionStatus
    {

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("status:complete")]
        statuscomplete,

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("status:fromKey_incomplete")]
        statusfromKey_incomplete,

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("status:toKey_incomplete")]
        statustoKey_incomplete,

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("status:both_incomplete")]
        statusboth_incomplete,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class get_bindingDetail
    {

        private string authInfoField;

        private string[] bindingKeyField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("bindingKey", DataType = "anyURI")]
        public string[] bindingKey
        {
            get
            {
                return this.bindingKeyField;
            }
            set
            {
                this.bindingKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class get_businessDetail
    {

        private string authInfoField;

        private string[] businessKeyField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("businessKey", DataType = "anyURI")]
        public string[] businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class get_serviceDetail
    {

        private string authInfoField;

        private string[] serviceKeyField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("serviceKey", DataType = "anyURI")]
        public string[] serviceKey
        {
            get
            {
                return this.serviceKeyField;
            }
            set
            {
                this.serviceKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class get_tModelDetail
    {

        private string authInfoField;

        private string[] tModelKeyField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("tModelKey", DataType = "anyURI")]
        public string[] tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class assertionStatusReport
    {

        private assertionStatusItem[] assertionStatusItemField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("assertionStatusItem")]
        public assertionStatusItem[] assertionStatusItem
        {
            get
            {
                return this.assertionStatusItemField;
            }
            set
            {
                this.assertionStatusItemField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class assertionStatusItem
    {

        private string fromKeyField;

        private string toKeyField;

        private keyedReference keyedReferenceField;

        private keysOwned keysOwnedField;

        private completionStatus completionStatusField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string fromKey
        {
            get
            {
                return this.fromKeyField;
            }
            set
            {
                this.fromKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string toKey
        {
            get
            {
                return this.toKeyField;
            }
            set
            {
                this.toKeyField = value;
            }
        }

        /// <remarks/>
        public keyedReference keyedReference
        {
            get
            {
                return this.keyedReferenceField;
            }
            set
            {
                this.keyedReferenceField = value;
            }
        }

        /// <remarks/>
        public keysOwned keysOwned
        {
            get
            {
                return this.keysOwnedField;
            }
            set
            {
                this.keysOwnedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public completionStatus completionStatus
        {
            get
            {
                return this.completionStatusField;
            }
            set
            {
                this.completionStatusField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class keysOwned
    {

        private string[] itemsField;

        private ItemsChoiceType3[] itemsElementNameField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("fromKey", typeof(string), DataType = "anyURI")]
        [System.Xml.Serialization.XmlElementAttribute("toKey", typeof(string), DataType = "anyURI")]
        [System.Xml.Serialization.XmlChoiceIdentifierAttribute("ItemsElementName")]
        public string[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("ItemsElementName")]
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public ItemsChoiceType3[] ItemsElementName
        {
            get
            {
                return this.itemsElementNameField;
            }
            set
            {
                this.itemsElementNameField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3", IncludeInSchema = false)]
    public enum ItemsChoiceType3
    {

        /// <remarks/>
        fromKey,

        /// <remarks/>
        toKey,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class bindingDetail
    {

        private listDescription listDescriptionField;

        private bindingTemplate[] bindingTemplateField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        public listDescription listDescription
        {
            get
            {
                return this.listDescriptionField;
            }
            set
            {
                this.listDescriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("bindingTemplate")]
        public bindingTemplate[] bindingTemplate
        {
            get
            {
                return this.bindingTemplateField;
            }
            set
            {
                this.bindingTemplateField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class listDescription
    {

        private int includeCountField;

        private int actualCountField;

        private int listHeadField;

        /// <remarks/>
        public int includeCount
        {
            get
            {
                return this.includeCountField;
            }
            set
            {
                this.includeCountField = value;
            }
        }

        /// <remarks/>
        public int actualCount
        {
            get
            {
                return this.actualCountField;
            }
            set
            {
                this.actualCountField = value;
            }
        }

        /// <remarks/>
        public int listHead
        {
            get
            {
                return this.listHeadField;
            }
            set
            {
                this.listHeadField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class bindingTemplate
    {

        private description[] descriptionField;

        private object itemField;

        private tModelInstanceInfo[] tModelInstanceDetailsField;

        private categoryBag categoryBagField;

        private SignatureType[] signatureField;

        private string bindingKeyField;

        private string serviceKeyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("accessPoint", typeof(accessPoint))]
        [System.Xml.Serialization.XmlElementAttribute("hostingRedirector", typeof(hostingRedirector))]
        public object Item
        {
            get
            {
                return this.itemField;
            }
            set
            {
                this.itemField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public tModelInstanceInfo[] tModelInstanceDetails
        {
            get
            {
                return this.tModelInstanceDetailsField;
            }
            set
            {
                this.tModelInstanceDetailsField = value;
            }
        }

        /// <remarks/>
        public categoryBag categoryBag
        {
            get
            {
                return this.categoryBagField;
            }
            set
            {
                this.categoryBagField = value;
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

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string bindingKey
        {
            get
            {
                return this.bindingKeyField;
            }
            set
            {
                this.bindingKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string serviceKey
        {
            get
            {
                return this.serviceKeyField;
            }
            set
            {
                this.serviceKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class description
    {

        private string langField;

        private string valueField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://www.w3.org/XML/1998/namespace")]
        public string lang
        {
            get
            {
                return this.langField;
            }
            set
            {
                this.langField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class accessPoint
    {

        private string useTypeField;

        private string valueField;

        public accessPoint()
        {
            this.useTypeField = "";
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string useType
        {
            get
            {
                return this.useTypeField;
            }
            set
            {
                this.useTypeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class hostingRedirector
    {

        private string bindingKeyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string bindingKey
        {
            get
            {
                return this.bindingKeyField;
            }
            set
            {
                this.bindingKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class tModelInstanceInfo
    {

        private description[] descriptionField;

        private instanceDetails instanceDetailsField;

        private string tModelKeyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        public instanceDetails instanceDetails
        {
            get
            {
                return this.instanceDetailsField;
            }
            set
            {
                this.instanceDetailsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class instanceDetails
    {

        private description[] descriptionField;

        private object[] itemsField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("instanceParms", typeof(string))]
        [System.Xml.Serialization.XmlElementAttribute("overviewDoc", typeof(overviewDoc))]
        public object[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class overviewDoc
    {

        private object[] itemsField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description", typeof(description))]
        [System.Xml.Serialization.XmlElementAttribute("overviewURL", typeof(overviewURL))]
        public object[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class overviewURL
    {

        private string useTypeField;

        private string valueField;

        public overviewURL()
        {
            this.useTypeField = "";
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string useType
        {
            get
            {
                return this.useTypeField;
            }
            set
            {
                this.useTypeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute(DataType = "anyURI")]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class SignatureType
    {

        private SignedInfoType signedInfoField;

        private SignatureValueType signatureValueField;

        private KeyInfoType keyInfoField;

        private ObjectType[] objectField;

        private string idField;

        /// <remarks/>
        public SignedInfoType SignedInfo
        {
            get
            {
                return this.signedInfoField;
            }
            set
            {
                this.signedInfoField = value;
            }
        }

        /// <remarks/>
        public SignatureValueType SignatureValue
        {
            get
            {
                return this.signatureValueField;
            }
            set
            {
                this.signatureValueField = value;
            }
        }

        /// <remarks/>
        public KeyInfoType KeyInfo
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

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("Object")]
        public ObjectType[] Object
        {
            get
            {
                return this.objectField;
            }
            set
            {
                this.objectField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "ID")]
        public string Id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class SignedInfoType
    {

        private CanonicalizationMethodType canonicalizationMethodField;

        private SignatureMethodType signatureMethodField;

        private ReferenceType[] referenceField;

        private string idField;

        /// <remarks/>
        public CanonicalizationMethodType CanonicalizationMethod
        {
            get
            {
                return this.canonicalizationMethodField;
            }
            set
            {
                this.canonicalizationMethodField = value;
            }
        }

        /// <remarks/>
        public SignatureMethodType SignatureMethod
        {
            get
            {
                return this.signatureMethodField;
            }
            set
            {
                this.signatureMethodField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("Reference")]
        public ReferenceType[] Reference
        {
            get
            {
                return this.referenceField;
            }
            set
            {
                this.referenceField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "ID")]
        public string Id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class CanonicalizationMethodType
    {

        private System.Xml.XmlNode[] anyField;

        private string algorithmField;

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        [System.Xml.Serialization.XmlAnyElementAttribute(Name="Any")]
        public System.Xml.XmlNode[] Any
        {
            get
            {
                return this.anyField;
            }
            set
            {
                this.anyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string Algorithm
        {
            get
            {
                return this.algorithmField;
            }
            set
            {
                this.algorithmField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class SignatureMethodType
    {

        private string hMACOutputLengthField;

        private System.Xml.XmlNode[] anyField;

        private string algorithmField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "integer")]
        public string HMACOutputLength
        {
            get
            {
                return this.hMACOutputLengthField;
            }
            set
            {
                this.hMACOutputLengthField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlNode[] Any
        {
            get
            {
                return this.anyField;
            }
            set
            {
                this.anyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string Algorithm
        {
            get
            {
                return this.algorithmField;
            }
            set
            {
                this.algorithmField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class ReferenceType
    {

        private TransformType[] transformsField;

        private DigestMethodType digestMethodField;

        private byte[] digestValueField;

        private string idField;

        private string uRIField;

        private string typeField;

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("Transform", IsNullable = false)]
        public TransformType[] Transforms
        {
            get
            {
                return this.transformsField;
            }
            set
            {
                this.transformsField = value;
            }
        }

        /// <remarks/>
        public DigestMethodType DigestMethod
        {
            get
            {
                return this.digestMethodField;
            }
            set
            {
                this.digestMethodField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] DigestValue
        {
            get
            {
                return this.digestValueField;
            }
            set
            {
                this.digestValueField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "ID")]
        public string Id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string URI
        {
            get
            {
                return this.uRIField;
            }
            set
            {
                this.uRIField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string Type
        {
            get
            {
                return this.typeField;
            }
            set
            {
                this.typeField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class TransformType
    {

        private object[] itemsField;

        private string[] textField;

        private string algorithmField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        [System.Xml.Serialization.XmlElementAttribute("XPath", typeof(string))]
        public object[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string[] Text
        {
            get
            {
                return this.textField;
            }
            set
            {
                this.textField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string Algorithm
        {
            get
            {
                return this.algorithmField;
            }
            set
            {
                this.algorithmField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class DigestMethodType
    {

        private System.Xml.XmlNode[] anyField;

        private string algorithmField;

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlNode[] Any
        {
            get
            {
                return this.anyField;
            }
            set
            {
                this.anyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string Algorithm
        {
            get
            {
                return this.algorithmField;
            }
            set
            {
                this.algorithmField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class SignatureValueType
    {

        private string idField;

        private byte[] valueField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "ID")]
        public string Id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute(DataType = "base64Binary")]
        public byte[] Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class KeyInfoType
    {

        private object[] itemsField;

        private ItemsChoiceType2[] itemsElementNameField;

        private string[] textField;

        private string idField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        [System.Xml.Serialization.XmlElementAttribute("KeyName", typeof(string))]
        [System.Xml.Serialization.XmlElementAttribute("KeyValue", typeof(KeyValueType))]
        [System.Xml.Serialization.XmlElementAttribute("MgmtData", typeof(string))]
        [System.Xml.Serialization.XmlElementAttribute("PGPData", typeof(PGPDataType))]
        [System.Xml.Serialization.XmlElementAttribute("RetrievalMethod", typeof(RetrievalMethodType))]
        [System.Xml.Serialization.XmlElementAttribute("SPKIData", typeof(SPKIDataType))]
        [System.Xml.Serialization.XmlElementAttribute("X509Data", typeof(X509DataType))]
        [System.Xml.Serialization.XmlChoiceIdentifierAttribute("ItemsElementName")]
        public object[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("ItemsElementName")]
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public ItemsChoiceType2[] ItemsElementName
        {
            get
            {
                return this.itemsElementNameField;
            }
            set
            {
                this.itemsElementNameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string[] Text
        {
            get
            {
                return this.textField;
            }
            set
            {
                this.textField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "ID")]
        public string Id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class KeyValueType
    {

        private object itemField;

        private string[] textField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        [System.Xml.Serialization.XmlElementAttribute("DSAKeyValue", typeof(DSAKeyValueType))]
        [System.Xml.Serialization.XmlElementAttribute("RSAKeyValue", typeof(RSAKeyValueType))]
        public object Item
        {
            get
            {
                return this.itemField;
            }
            set
            {
                this.itemField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string[] Text
        {
            get
            {
                return this.textField;
            }
            set
            {
                this.textField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class DSAKeyValueType
    {

        private byte[] pField;

        private byte[] qField;

        private byte[] gField;

        private byte[] yField;

        private byte[] jField;

        private byte[] seedField;

        private byte[] pgenCounterField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] P
        {
            get
            {
                return this.pField;
            }
            set
            {
                this.pField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] Q
        {
            get
            {
                return this.qField;
            }
            set
            {
                this.qField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] G
        {
            get
            {
                return this.gField;
            }
            set
            {
                this.gField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] Y
        {
            get
            {
                return this.yField;
            }
            set
            {
                this.yField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] J
        {
            get
            {
                return this.jField;
            }
            set
            {
                this.jField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] Seed
        {
            get
            {
                return this.seedField;
            }
            set
            {
                this.seedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] PgenCounter
        {
            get
            {
                return this.pgenCounterField;
            }
            set
            {
                this.pgenCounterField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class RSAKeyValueType
    {

        private byte[] modulusField;

        private byte[] exponentField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] Modulus
        {
            get
            {
                return this.modulusField;
            }
            set
            {
                this.modulusField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] Exponent
        {
            get
            {
                return this.exponentField;
            }
            set
            {
                this.exponentField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class PGPDataType
    {

        private object[] itemsField;

        private ItemsChoiceType1[] itemsElementNameField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        [System.Xml.Serialization.XmlElementAttribute("PGPKeyID", typeof(byte[]), DataType = "base64Binary")]
        [System.Xml.Serialization.XmlElementAttribute("PGPKeyPacket", typeof(byte[]), DataType = "base64Binary")]
        [System.Xml.Serialization.XmlChoiceIdentifierAttribute("ItemsElementName")]
        public object[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("ItemsElementName")]
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public ItemsChoiceType1[] ItemsElementName
        {
            get
            {
                return this.itemsElementNameField;
            }
            set
            {
                this.itemsElementNameField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#", IncludeInSchema = false)]
    public enum ItemsChoiceType1
    {

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("##any:")]
        Item,

        /// <remarks/>
        PGPKeyID,

        /// <remarks/>
        PGPKeyPacket,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class RetrievalMethodType
    {

        private TransformType[] transformsField;

        private string uRIField;

        private string typeField;

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("Transform", IsNullable = false)]
        public TransformType[] Transforms
        {
            get
            {
                return this.transformsField;
            }
            set
            {
                this.transformsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string URI
        {
            get
            {
                return this.uRIField;
            }
            set
            {
                this.uRIField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string Type
        {
            get
            {
                return this.typeField;
            }
            set
            {
                this.typeField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class SPKIDataType
    {

        private byte[][] sPKISexpField;

        private System.Xml.XmlElement anyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("SPKISexp", DataType = "base64Binary")]
        public byte[][] SPKISexp
        {
            get
            {
                return this.sPKISexpField;
            }
            set
            {
                this.sPKISexpField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlElement Any
        {
            get
            {
                return this.anyField;
            }
            set
            {
                this.anyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class X509DataType
    {

        private object[] itemsField;

        private ItemsChoiceType[] itemsElementNameField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        [System.Xml.Serialization.XmlElementAttribute("X509CRL", typeof(byte[]), DataType = "base64Binary")]
        [System.Xml.Serialization.XmlElementAttribute("X509Certificate", typeof(byte[]), DataType = "base64Binary")]
        [System.Xml.Serialization.XmlElementAttribute("X509IssuerSerial", typeof(X509IssuerSerialType))]
        [System.Xml.Serialization.XmlElementAttribute("X509SKI", typeof(byte[]), DataType = "base64Binary")]
        [System.Xml.Serialization.XmlElementAttribute("X509SubjectName", typeof(string))]
        [System.Xml.Serialization.XmlChoiceIdentifierAttribute("ItemsElementName")]
        public object[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("ItemsElementName")]
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public ItemsChoiceType[] ItemsElementName
        {
            get
            {
                return this.itemsElementNameField;
            }
            set
            {
                this.itemsElementNameField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class X509IssuerSerialType
    {

        private string x509IssuerNameField;

        private string x509SerialNumberField;

        /// <remarks/>
        public string X509IssuerName
        {
            get
            {
                return this.x509IssuerNameField;
            }
            set
            {
                this.x509IssuerNameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "integer")]
        public string X509SerialNumber
        {
            get
            {
                return this.x509SerialNumberField;
            }
            set
            {
                this.x509SerialNumberField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#", IncludeInSchema = false)]
    public enum ItemsChoiceType
    {

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("##any:")]
        Item,

        /// <remarks/>
        X509CRL,

        /// <remarks/>
        X509Certificate,

        /// <remarks/>
        X509IssuerSerial,

        /// <remarks/>
        X509SKI,

        /// <remarks/>
        X509SubjectName,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#", IncludeInSchema = false)]
    public enum ItemsChoiceType2
    {

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("##any:")]
        Item,

        /// <remarks/>
        KeyName,

        /// <remarks/>
        KeyValue,

        /// <remarks/>
        MgmtData,

        /// <remarks/>
        PGPData,

        /// <remarks/>
        RetrievalMethod,

        /// <remarks/>
        SPKIData,

        /// <remarks/>
        X509Data,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class ObjectType
    {

        private System.Xml.XmlNode[] anyField;

        private string idField;

        private string mimeTypeField;

        private string encodingField;

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlNode[] Any
        {
            get
            {
                return this.anyField;
            }
            set
            {
                this.anyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "ID")]
        public string Id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string MimeType
        {
            get
            {
                return this.mimeTypeField;
            }
            set
            {
                this.mimeTypeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string Encoding
        {
            get
            {
                return this.encodingField;
            }
            set
            {
                this.encodingField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class businessDetail
    {

        private businessEntity[] businessEntityField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("businessEntity")]
        public businessEntity[] businessEntity
        {
            get
            {
                return this.businessEntityField;
            }
            set
            {
                this.businessEntityField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class businessEntity
    {

        private discoveryURL[] discoveryURLsField;

        private name[] nameField;

        private description[] descriptionField;

        private contact[] contactsField;

        private businessService[] businessServicesField;

        private keyedReference[] identifierBagField;

        private categoryBag categoryBagField;

        private SignatureType[] signatureField;

        private string businessKeyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public discoveryURL[] discoveryURLs
        {
            get
            {
                return this.discoveryURLsField;
            }
            set
            {
                this.discoveryURLsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("name")]
        public name[] name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public contact[] contacts
        {
            get
            {
                return this.contactsField;
            }
            set
            {
                this.contactsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public businessService[] businessServices
        {
            get
            {
                return this.businessServicesField;
            }
            set
            {
                this.businessServicesField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public keyedReference[] identifierBag
        {
            get
            {
                return this.identifierBagField;
            }
            set
            {
                this.identifierBagField = value;
            }
        }

        /// <remarks/>
        public categoryBag categoryBag
        {
            get
            {
                return this.categoryBagField;
            }
            set
            {
                this.categoryBagField = value;
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

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class contact
    {

        private description[] descriptionField;

        private personName[] personNameField;

        private phone[] phoneField;

        private email[] emailField;

        private address[] addressField;

        private string useTypeField;

        public contact()
        {
            this.useTypeField = "";
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("personName")]
        public personName[] personName
        {
            get
            {
                return this.personNameField;
            }
            set
            {
                this.personNameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("phone")]
        public phone[] phone
        {
            get
            {
                return this.phoneField;
            }
            set
            {
                this.phoneField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("email")]
        public email[] email
        {
            get
            {
                return this.emailField;
            }
            set
            {
                this.emailField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("address")]
        public address[] address
        {
            get
            {
                return this.addressField;
            }
            set
            {
                this.addressField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string useType
        {
            get
            {
                return this.useTypeField;
            }
            set
            {
                this.useTypeField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class personName
    {

        private string langField;

        private string valueField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://www.w3.org/XML/1998/namespace")]
        public string lang
        {
            get
            {
                return this.langField;
            }
            set
            {
                this.langField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class phone
    {

        private string useTypeField;

        private string valueField;

        public phone()
        {
            this.useTypeField = "";
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string useType
        {
            get
            {
                return this.useTypeField;
            }
            set
            {
                this.useTypeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class email
    {

        private string useTypeField;

        private string valueField;

        public email()
        {
            this.useTypeField = "";
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string useType
        {
            get
            {
                return this.useTypeField;
            }
            set
            {
                this.useTypeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class address
    {

        private addressLine[] addressLineField;

        private string langField;

        private string useTypeField;

        private string sortCodeField;

        private string tModelKeyField;

        public address()
        {
            this.useTypeField = "";
            this.sortCodeField = "";
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("addressLine")]
        public addressLine[] addressLine
        {
            get
            {
                return this.addressLineField;
            }
            set
            {
                this.addressLineField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(Form = System.Xml.Schema.XmlSchemaForm.Qualified, Namespace = "http://www.w3.org/XML/1998/namespace")]
        public string lang
        {
            get
            {
                return this.langField;
            }
            set
            {
                this.langField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string useType
        {
            get
            {
                return this.useTypeField;
            }
            set
            {
                this.useTypeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string sortCode
        {
            get
            {
                return this.sortCodeField;
            }
            set
            {
                this.sortCodeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class addressLine
    {

        private string keyNameField;

        private string keyValueField;

        private string valueField;

        public addressLine()
        {
            this.keyNameField = "";
            this.keyValueField = "";
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string keyName
        {
            get
            {
                return this.keyNameField;
            }
            set
            {
                this.keyNameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute("")]
        public string keyValue
        {
            get
            {
                return this.keyValueField;
            }
            set
            {
                this.keyValueField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class businessService
    {

        private name[] nameField;

        private description[] descriptionField;

        private bindingTemplate[] bindingTemplatesField;

        private categoryBag categoryBagField;

        private SignatureType[] signatureField;

        private string serviceKeyField;

        private string businessKeyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("name")]
        public name[] name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public bindingTemplate[] bindingTemplates
        {
            get
            {
                return this.bindingTemplatesField;
            }
            set
            {
                this.bindingTemplatesField = value;
            }
        }

        /// <remarks/>
        public categoryBag categoryBag
        {
            get
            {
                return this.categoryBagField;
            }
            set
            {
                this.categoryBagField = value;
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

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string serviceKey
        {
            get
            {
                return this.serviceKeyField;
            }
            set
            {
                this.serviceKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class businessList
    {

        private listDescription listDescriptionField;

        private businessInfo[] businessInfosField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        public listDescription listDescription
        {
            get
            {
                return this.listDescriptionField;
            }
            set
            {
                this.listDescriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public businessInfo[] businessInfos
        {
            get
            {
                return this.businessInfosField;
            }
            set
            {
                this.businessInfosField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class businessInfo
    {

        private name[] nameField;

        private description[] descriptionField;

        private serviceInfo[] serviceInfosField;

        private string businessKeyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("name")]
        public name[] name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public serviceInfo[] serviceInfos
        {
            get
            {
                return this.serviceInfosField;
            }
            set
            {
                this.serviceInfosField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class serviceInfo
    {

        private name[] nameField;

        private string serviceKeyField;

        private string businessKeyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("name")]
        public name[] name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string serviceKey
        {
            get
            {
                return this.serviceKeyField;
            }
            set
            {
                this.serviceKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class relatedBusinessesList
    {

        private listDescription listDescriptionField;

        private string businessKeyField;

        private relatedBusinessInfo[] relatedBusinessInfosField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        public listDescription listDescription
        {
            get
            {
                return this.listDescriptionField;
            }
            set
            {
                this.listDescriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public relatedBusinessInfo[] relatedBusinessInfos
        {
            get
            {
                return this.relatedBusinessInfosField;
            }
            set
            {
                this.relatedBusinessInfosField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class relatedBusinessInfo
    {

        private string businessKeyField;

        private name[] nameField;

        private description[] descriptionField;

        private sharedRelationships[] sharedRelationshipsField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("name")]
        public name[] name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("sharedRelationships")]
        public sharedRelationships[] sharedRelationships
        {
            get
            {
                return this.sharedRelationshipsField;
            }
            set
            {
                this.sharedRelationshipsField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class sharedRelationships
    {

        private keyedReference[] keyedReferenceField;

        private publisherAssertion[] publisherAssertionField;

        private direction directionField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("keyedReference")]
        public keyedReference[] keyedReference
        {
            get
            {
                return this.keyedReferenceField;
            }
            set
            {
                this.keyedReferenceField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("publisherAssertion")]
        public publisherAssertion[] publisherAssertion
        {
            get
            {
                return this.publisherAssertionField;
            }
            set
            {
                this.publisherAssertionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public direction direction
        {
            get
            {
                return this.directionField;
            }
            set
            {
                this.directionField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class publisherAssertion
    {

        private string fromKeyField;

        private string toKeyField;

        private keyedReference keyedReferenceField;

        private SignatureType[] signatureField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string fromKey
        {
            get
            {
                return this.fromKeyField;
            }
            set
            {
                this.fromKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string toKey
        {
            get
            {
                return this.toKeyField;
            }
            set
            {
                this.toKeyField = value;
            }
        }

        /// <remarks/>
        public keyedReference keyedReference
        {
            get
            {
                return this.keyedReferenceField;
            }
            set
            {
                this.keyedReferenceField = value;
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
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public enum direction
    {

        /// <remarks/>
        fromKey,

        /// <remarks/>
        toKey,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class serviceDetail
    {

        private businessService[] businessServiceField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("businessService")]
        public businessService[] businessService
        {
            get
            {
                return this.businessServiceField;
            }
            set
            {
                this.businessServiceField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class serviceList
    {

        private listDescription listDescriptionField;

        private serviceInfo[] serviceInfosField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        public listDescription listDescription
        {
            get
            {
                return this.listDescriptionField;
            }
            set
            {
                this.listDescriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public serviceInfo[] serviceInfos
        {
            get
            {
                return this.serviceInfosField;
            }
            set
            {
                this.serviceInfosField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class tModelDetail
    {

        private tModel[] tModelField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("tModel")]
        public tModel[] tModel
        {
            get
            {
                return this.tModelField;
            }
            set
            {
                this.tModelField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class tModel
    {

        private name nameField;

        private description[] descriptionField;

        private overviewDoc[] overviewDocField;

        private keyedReference[] identifierBagField;

        private categoryBag categoryBagField;

        private SignatureType[] signatureField;

        private string tModelKeyField;

        private bool deletedField;

        public tModel()
        {
            this.deletedField = false;
        }

        /// <remarks/>
        public name name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("overviewDoc")]
        public overviewDoc[] overviewDoc
        {
            get
            {
                return this.overviewDocField;
            }
            set
            {
                this.overviewDocField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public keyedReference[] identifierBag
        {
            get
            {
                return this.identifierBagField;
            }
            set
            {
                this.identifierBagField = value;
            }
        }

        /// <remarks/>
        public categoryBag categoryBag
        {
            get
            {
                return this.categoryBagField;
            }
            set
            {
                this.categoryBagField = value;
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

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute(false)]
        public bool deleted
        {
            get
            {
                return this.deletedField;
            }
            set
            {
                this.deletedField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class tModelList
    {

        private listDescription listDescriptionField;

        private tModelInfo[] tModelInfosField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        public listDescription listDescription
        {
            get
            {
                return this.listDescriptionField;
            }
            set
            {
                this.listDescriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public tModelInfo[] tModelInfos
        {
            get
            {
                return this.tModelInfosField;
            }
            set
            {
                this.tModelInfosField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class tModelInfo
    {

        private name nameField;

        private description[] descriptionField;

        private string tModelKeyField;

        /// <remarks/>
        public name name
        {
            get
            {
                return this.nameField;
            }
            set
            {
                this.nameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("description")]
        public description[] description
        {
            get
            {
                return this.descriptionField;
            }
            set
            {
                this.descriptionField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(TypeName = "keyBag", Namespace = "urn:uddi-org:sub_v3")]
    public partial class keyBag1
    {

        private bool deletedField;

        private string[] itemsField;

        private ItemsChoiceType4[] itemsElementNameField;

        /// <remarks/>
        public bool deleted
        {
            get
            {
                return this.deletedField;
            }
            set
            {
                this.deletedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("bindingKey", typeof(string), Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        [System.Xml.Serialization.XmlElementAttribute("businessKey", typeof(string), Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        [System.Xml.Serialization.XmlElementAttribute("serviceKey", typeof(string), Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        [System.Xml.Serialization.XmlElementAttribute("tModelKey", typeof(string), Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        [System.Xml.Serialization.XmlChoiceIdentifierAttribute("ItemsElementName")]
        public string[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("ItemsElementName")]
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public ItemsChoiceType4[] ItemsElementName
        {
            get
            {
                return this.itemsElementNameField;
            }
            set
            {
                this.itemsElementNameField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:sub_v3", IncludeInSchema = false)]
    public enum ItemsChoiceType4
    {

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("urn:uddi-org:api_v3:bindingKey")]
        bindingKey,

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("urn:uddi-org:api_v3:businessKey")]
        businessKey,

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("urn:uddi-org:api_v3:serviceKey")]
        serviceKey,

        /// <remarks/>
        [System.Xml.Serialization.XmlEnumAttribute("urn:uddi-org:api_v3:tModelKey")]
        tModelKey,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class SignaturePropertyType
    {

        private System.Xml.XmlElement[] itemsField;

        private string[] textField;

        private string targetField;

        private string idField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlElement[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string[] Text
        {
            get
            {
                return this.textField;
            }
            set
            {
                this.textField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string Target
        {
            get
            {
                return this.targetField;
            }
            set
            {
                this.targetField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "ID")]
        public string Id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class SignaturePropertiesType
    {

        private SignaturePropertyType[] signaturePropertyField;

        private string idField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("SignatureProperty")]
        public SignaturePropertyType[] SignatureProperty
        {
            get
            {
                return this.signaturePropertyField;
            }
            set
            {
                this.signaturePropertyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "ID")]
        public string Id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "http://www.w3.org/2000/09/xmldsig#")]
    public partial class ManifestType
    {

        private ReferenceType[] referenceField;

        private string idField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("Reference")]
        public ReferenceType[] Reference
        {
            get
            {
                return this.referenceField;
            }
            set
            {
                this.referenceField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "ID")]
        public string Id
        {
            get
            {
                return this.idField;
            }
            set
            {
                this.idField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class errInfo
    {

        private string errCodeField;

        private string valueField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string errCode
        {
            get
            {
                return this.errCodeField;
            }
            set
            {
                this.errCodeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlTextAttribute()]
        public string Value
        {
            get
            {
                return this.valueField;
            }
            set
            {
                this.valueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class result
    {

        private errInfo errInfoField;

        private keyType keyTypeField;

        private bool keyTypeFieldSpecified;

        private int errnoField;

        /// <remarks/>
        public errInfo errInfo
        {
            get
            {
                return this.errInfoField;
            }
            set
            {
                this.errInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public keyType keyType
        {
            get
            {
                return this.keyTypeField;
            }
            set
            {
                this.keyTypeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool keyTypeSpecified
        {
            get
            {
                return this.keyTypeFieldSpecified;
            }
            set
            {
                this.keyTypeFieldSpecified = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int errno
        {
            get
            {
                return this.errnoField;
            }
            set
            {
                this.errnoField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public enum keyType
    {

        /// <remarks/>
        businessKey,

        /// <remarks/>
        tModelKey,

        /// <remarks/>
        serviceKey,

        /// <remarks/>
        bindingKey,

        /// <remarks/>
        subscriptionKey,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class dispositionReport
    {

        private result[] resultField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("result")]
        public result[] result
        {
            get
            {
                return this.resultField;
            }
            set
            {
                this.resultField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:vscache_v3")]
    public partial class validValuesList
    {

        private string chunkTokenField;

        private validValue[] validValueField;

        /// <remarks/>
        public string chunkToken
        {
            get
            {
                return this.chunkTokenField;
            }
            set
            {
                this.chunkTokenField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("validValue")]
        public validValue[] validValue
        {
            get
            {
                return this.validValueField;
            }
            set
            {
                this.validValueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:vscache_v3")]
    public partial class validValue
    {

        private string keyValueField;

        /// <remarks/>
        public string keyValue
        {
            get
            {
                return this.keyValueField;
            }
            set
            {
                this.keyValueField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:vscache_v3")]
    public partial class get_allValidValues
    {

        private string authInfoField;

        private string tModelKeyField;

        private string chunkTokenField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        public string tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }

        /// <remarks/>
        public string chunkToken
        {
            get
            {
                return this.chunkTokenField;
            }
            set
            {
                this.chunkTokenField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:vs_v3")]
    public partial class validate_values
    {

        private string authInfoField;

        private object[] itemsField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("bindingTemplate", typeof(bindingTemplate), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("businessEntity", typeof(businessEntity), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("businessService", typeof(businessService), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("publisherAssertion", typeof(publisherAssertion), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("tModel", typeof(tModel), Namespace = "urn:uddi-org:api_v3")]
        public object[] Items
        {
            get
            {
                return this.itemsField;
            }
            set
            {
                this.itemsField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:policy_v3_instanceParms")]
    public partial class UDDIinstanceParmsContainer_type
    {

        private System.Xml.XmlElement[] anyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlElement[] Any
        {
            get
            {
                return this.anyField;
            }
            set
            {
                this.anyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:custody_v3")]
    public partial class transferOperationalInfo
    {

        private string authorizedNameField;

        private string nodeIDField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authorizedName
        {
            get
            {
                return this.authorizedNameField;
            }
            set
            {
                this.authorizedNameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        public string nodeID
        {
            get
            {
                return this.nodeIDField;
            }
            set
            {
                this.nodeIDField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:repl_v3")]
    public partial class transfer_custody
    {

        private transferToken transferTokenField;

        private keyBag keyBagField;

        private transferOperationalInfo transferOperationalInfoField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:custody_v3")]
        public transferToken transferToken
        {
            get
            {
                return this.transferTokenField;
            }
            set
            {
                this.transferTokenField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:custody_v3")]
        public keyBag keyBag
        {
            get
            {
                return this.keyBagField;
            }
            set
            {
                this.keyBagField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:custody_v3")]
        public transferOperationalInfo transferOperationalInfo
        {
            get
            {
                return this.transferOperationalInfoField;
            }
            set
            {
                this.transferOperationalInfoField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:custody_v3")]
    public partial class transferToken
    {

        private string nodeIDField;

        private System.DateTime expirationTimeField;

        private byte[] opaqueTokenField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        public string nodeID
        {
            get
            {
                return this.nodeIDField;
            }
            set
            {
                this.nodeIDField = value;
            }
        }

        /// <remarks/>
        public System.DateTime expirationTime
        {
            get
            {
                return this.expirationTimeField;
            }
            set
            {
                this.expirationTimeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "base64Binary")]
        public byte[] opaqueToken
        {
            get
            {
                return this.opaqueTokenField;
            }
            set
            {
                this.opaqueTokenField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:custody_v3")]
    public partial class keyBag
    {

        private string[] keyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("key", DataType = "anyURI")]
        public string[] key
        {
            get
            {
                return this.keyField;
            }
            set
            {
                this.keyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:sub_v3")]
    public partial class save_subscription
    {

        private string authInfoField;

        private subscription[] subscriptionField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("subscription")]
        public subscription[] subscription
        {
            get
            {
                return this.subscriptionField;
            }
            set
            {
                this.subscriptionField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:sub_v3")]
    public partial class get_subscriptionResults
    {

        private string authInfoField;

        private string subscriptionKeyField;

        private coveragePeriod coveragePeriodField;

        private string chunkTokenField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string subscriptionKey
        {
            get
            {
                return this.subscriptionKeyField;
            }
            set
            {
                this.subscriptionKeyField = value;
            }
        }

        /// <remarks/>
        public coveragePeriod coveragePeriod
        {
            get
            {
                return this.coveragePeriodField;
            }
            set
            {
                this.coveragePeriodField = value;
            }
        }

        /// <remarks/>
        public string chunkToken
        {
            get
            {
                return this.chunkTokenField;
            }
            set
            {
                this.chunkTokenField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:sub_v3")]
    public partial class get_subscriptions
    {

        private string authInfoField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:sub_v3")]
    public partial class delete_subscription
    {

        private string authInfoField;

        private string[] subscriptionKeyField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("subscriptionKey", DataType = "anyURI")]
        public string[] subscriptionKey
        {
            get
            {
                return this.subscriptionKeyField;
            }
            set
            {
                this.subscriptionKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:repl_v3")]
    public partial class highWaterMarkVector_type
    {

        private changeRecordID_type[] highWaterMarkField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("highWaterMark")]
        public changeRecordID_type[] highWaterMark
        {
            get
            {
                return this.highWaterMarkField;
            }
            set
            {
                this.highWaterMarkField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordID_type
    {

        private string nodeIDField;

        private string originatingUSNField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string nodeID
        {
            get
            {
                return this.nodeIDField;
            }
            set
            {
                this.nodeIDField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "integer")]
        public string originatingUSN
        {
            get
            {
                return this.originatingUSNField;
            }
            set
            {
                this.originatingUSNField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:custody_v3")]
    public partial class transfer_entities
    {

        private string authInfoField;

        private transferToken transferTokenField;

        private keyBag keyBagField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        public transferToken transferToken
        {
            get
            {
                return this.transferTokenField;
            }
            set
            {
                this.transferTokenField = value;
            }
        }

        /// <remarks/>
        public keyBag keyBag
        {
            get
            {
                return this.keyBagField;
            }
            set
            {
                this.keyBagField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:custody_v3")]
    public partial class get_transferToken
    {

        private string authInfoField;

        private keyBag keyBagField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        public keyBag keyBag
        {
            get
            {
                return this.keyBagField;
            }
            set
            {
                this.keyBagField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:custody_v3")]
    public partial class discard_transferToken
    {

        private string authInfoField;

        private object itemField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("keyBag", typeof(keyBag))]
        [System.Xml.Serialization.XmlElementAttribute("transferToken", typeof(transferToken))]
        public object Item
        {
            get
            {
                return this.itemField;
            }
            set
            {
                this.itemField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class authToken
    {

        private string authInfoField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class get_authToken
    {

        private string userIDField;

        private string credField;

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string userID
        {
            get
            {
                return this.userIDField;
            }
            set
            {
                this.userIDField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string cred
        {
            get
            {
                return this.credField;
            }
            set
            {
                this.credField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class discard_authToken
    {

        private string authInfoField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class set_publisherAssertions
    {

        private string authInfoField;

        private publisherAssertion[] publisherAssertionField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("publisherAssertion")]
        public publisherAssertion[] publisherAssertion
        {
            get
            {
                return this.publisherAssertionField;
            }
            set
            {
                this.publisherAssertionField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class save_tModel
    {

        private string authInfoField;

        private tModel[] tModelField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("tModel")]
        public tModel[] tModel
        {
            get
            {
                return this.tModelField;
            }
            set
            {
                this.tModelField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class save_service
    {

        private string authInfoField;

        private businessService[] businessServiceField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("businessService")]
        public businessService[] businessService
        {
            get
            {
                return this.businessServiceField;
            }
            set
            {
                this.businessServiceField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class save_business
    {

        private string authInfoField;

        private businessEntity[] businessEntityField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("businessEntity")]
        public businessEntity[] businessEntity
        {
            get
            {
                return this.businessEntityField;
            }
            set
            {
                this.businessEntityField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class save_binding
    {

        private string authInfoField;

        private bindingTemplate[] bindingTemplateField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("bindingTemplate")]
        public bindingTemplate[] bindingTemplate
        {
            get
            {
                return this.bindingTemplateField;
            }
            set
            {
                this.bindingTemplateField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class registeredInfo
    {

        private businessInfo[] businessInfosField;

        private tModelInfo[] tModelInfosField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public businessInfo[] businessInfos
        {
            get
            {
                return this.businessInfosField;
            }
            set
            {
                this.businessInfosField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute(IsNullable = false)]
        public tModelInfo[] tModelInfos
        {
            get
            {
                return this.tModelInfosField;
            }
            set
            {
                this.tModelInfosField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class get_registeredInfo
    {

        private string authInfoField;

        private infoSelection infoSelectionField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public infoSelection infoSelection
        {
            get
            {
                return this.infoSelectionField;
            }
            set
            {
                this.infoSelectionField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public enum infoSelection
    {

        /// <remarks/>
        all,

        /// <remarks/>
        hidden,

        /// <remarks/>
        visible,
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class get_publisherAssertions
    {

        private string authInfoField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class delete_tModel
    {

        private string authInfoField;

        private string[] tModelKeyField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("tModelKey", DataType = "anyURI")]
        public string[] tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class delete_service
    {

        private string authInfoField;

        private string[] serviceKeyField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("serviceKey", DataType = "anyURI")]
        public string[] serviceKey
        {
            get
            {
                return this.serviceKeyField;
            }
            set
            {
                this.serviceKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class delete_publisherAssertions
    {

        private string authInfoField;

        private publisherAssertion[] publisherAssertionField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("publisherAssertion")]
        public publisherAssertion[] publisherAssertion
        {
            get
            {
                return this.publisherAssertionField;
            }
            set
            {
                this.publisherAssertionField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class delete_business
    {

        private string authInfoField;

        private string[] businessKeyField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("businessKey", DataType = "anyURI")]
        public string[] businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class delete_binding
    {

        private string authInfoField;

        private string[] bindingKeyField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("bindingKey", DataType = "anyURI")]
        public string[] bindingKey
        {
            get
            {
                return this.bindingKeyField;
            }
            set
            {
                this.bindingKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class add_publisherAssertions
    {

        private string authInfoField;

        private publisherAssertion[] publisherAssertionField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("publisherAssertion")]
        public publisherAssertion[] publisherAssertion
        {
            get
            {
                return this.publisherAssertionField;
            }
            set
            {
                this.publisherAssertionField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class operationalInfo
    {

        private System.DateTime createdField;

        private bool createdFieldSpecified;

        private System.DateTime modifiedField;

        private bool modifiedFieldSpecified;

        private System.DateTime modifiedIncludingChildrenField;

        private bool modifiedIncludingChildrenFieldSpecified;

        private string nodeIDField;

        private string authorizedNameField;

        private string entityKeyField;

        /// <remarks/>
        public System.DateTime created
        {
            get
            {
                return this.createdField;
            }
            set
            {
                this.createdField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool createdSpecified
        {
            get
            {
                return this.createdFieldSpecified;
            }
            set
            {
                this.createdFieldSpecified = value;
            }
        }

        /// <remarks/>
        public System.DateTime modified
        {
            get
            {
                return this.modifiedField;
            }
            set
            {
                this.modifiedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool modifiedSpecified
        {
            get
            {
                return this.modifiedFieldSpecified;
            }
            set
            {
                this.modifiedFieldSpecified = value;
            }
        }

        /// <remarks/>
        public System.DateTime modifiedIncludingChildren
        {
            get
            {
                return this.modifiedIncludingChildrenField;
            }
            set
            {
                this.modifiedIncludingChildrenField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool modifiedIncludingChildrenSpecified
        {
            get
            {
                return this.modifiedIncludingChildrenFieldSpecified;
            }
            set
            {
                this.modifiedIncludingChildrenFieldSpecified = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string nodeID
        {
            get
            {
                return this.nodeIDField;
            }
            set
            {
                this.nodeIDField = value;
            }
        }

        /// <remarks/>
        public string authorizedName
        {
            get
            {
                return this.authorizedNameField;
            }
            set
            {
                this.authorizedNameField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute(DataType = "anyURI")]
        public string entityKey
        {
            get
            {
                return this.entityKeyField;
            }
            set
            {
                this.entityKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class operationalInfos
    {

        private operationalInfo[] operationalInfoField;

        private bool truncatedField;

        private bool truncatedFieldSpecified;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("operationalInfo")]
        public operationalInfo[] operationalInfo
        {
            get
            {
                return this.operationalInfoField;
            }
            set
            {
                this.operationalInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool truncated
        {
            get
            {
                return this.truncatedField;
            }
            set
            {
                this.truncatedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlIgnoreAttribute()]
        public bool truncatedSpecified
        {
            get
            {
                return this.truncatedFieldSpecified;
            }
            set
            {
                this.truncatedFieldSpecified = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace = "urn:uddi-org:api_v3")]
    public partial class get_operationalInfo
    {

        private string authInfoField;

        private string[] entityKeyField;

        /// <remarks/>
        public string authInfo
        {
            get
            {
                return this.authInfoField;
            }
            set
            {
                this.authInfoField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("entityKey", DataType = "anyURI")]
        public string[] entityKey
        {
            get
            {
                return this.entityKeyField;
            }
            set
            {
                this.entityKeyField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class get_changeRecords
    {

        private string requestingNodeField;

        private changeRecordID_type[] changesAlreadySeenField;

        private object itemField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string requestingNode
        {
            get
            {
                return this.requestingNodeField;
            }
            set
            {
                this.requestingNodeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("highWaterMark", IsNullable = false)]
        public changeRecordID_type[] changesAlreadySeen
        {
            get
            {
                return this.changesAlreadySeenField;
            }
            set
            {
                this.changesAlreadySeenField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("responseLimitCount", typeof(string), DataType = "integer")]
        [System.Xml.Serialization.XmlElementAttribute("responseLimitVector", typeof(highWaterMarkVector_type))]
        public object Item
        {
            get
            {
                return this.itemField;
            }
            set
            {
                this.itemField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecord
    {

        private changeRecordID_type changeIDField;

        private object changeRecordNullField;

        private changeRecordNewData changeRecordNewDataField;

        private changeRecordDelete changeRecordDeleteField;

        private changeRecordPublisherAssertion changeRecordPublisherAssertionField;

        private changeRecordHide changeRecordHideField;

        private changeRecordDeleteAssertion changeRecordDeleteAssertionField;

        private changeRecordAcknowledgement changeRecordAcknowledgementField;

        private changeRecordCorrection changeRecordCorrectionField;

        private changeRecordNewDataConditional changeRecordNewDataConditionalField;

        private changeRecordConditionFailed changeRecordConditionFailedField;

        private bool acknowledgementRequestedField;

        /// <remarks/>
        public changeRecordID_type changeID
        {
            get
            {
                return this.changeIDField;
            }
            set
            {
                this.changeIDField = value;
            }
        }

        /// <remarks/>
        public object changeRecordNull
        {
            get
            {
                return this.changeRecordNullField;
            }
            set
            {
                this.changeRecordNullField = value;
            }
        }

        /// <remarks/>
        public changeRecordNewData changeRecordNewData
        {
            get
            {
                return this.changeRecordNewDataField;
            }
            set
            {
                this.changeRecordNewDataField = value;
            }
        }

        /// <remarks/>
        public changeRecordDelete changeRecordDelete
        {
            get
            {
                return this.changeRecordDeleteField;
            }
            set
            {
                this.changeRecordDeleteField = value;
            }
        }

        /// <remarks/>
        public changeRecordPublisherAssertion changeRecordPublisherAssertion
        {
            get
            {
                return this.changeRecordPublisherAssertionField;
            }
            set
            {
                this.changeRecordPublisherAssertionField = value;
            }
        }

        /// <remarks/>
        public changeRecordHide changeRecordHide
        {
            get
            {
                return this.changeRecordHideField;
            }
            set
            {
                this.changeRecordHideField = value;
            }
        }

        /// <remarks/>
        public changeRecordDeleteAssertion changeRecordDeleteAssertion
        {
            get
            {
                return this.changeRecordDeleteAssertionField;
            }
            set
            {
                this.changeRecordDeleteAssertionField = value;
            }
        }

        /// <remarks/>
        public changeRecordAcknowledgement changeRecordAcknowledgement
        {
            get
            {
                return this.changeRecordAcknowledgementField;
            }
            set
            {
                this.changeRecordAcknowledgementField = value;
            }
        }

        /// <remarks/>
        public changeRecordCorrection changeRecordCorrection
        {
            get
            {
                return this.changeRecordCorrectionField;
            }
            set
            {
                this.changeRecordCorrectionField = value;
            }
        }

        /// <remarks/>
        public changeRecordNewDataConditional changeRecordNewDataConditional
        {
            get
            {
                return this.changeRecordNewDataConditionalField;
            }
            set
            {
                this.changeRecordNewDataConditionalField = value;
            }
        }

        /// <remarks/>
        public changeRecordConditionFailed changeRecordConditionFailed
        {
            get
            {
                return this.changeRecordConditionFailedField;
            }
            set
            {
                this.changeRecordConditionFailedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool acknowledgementRequested
        {
            get
            {
                return this.acknowledgementRequestedField;
            }
            set
            {
                this.acknowledgementRequestedField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordNewData
    {

        private object itemField;

        private operationalInfo operationalInfoField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute("bindingTemplate", typeof(bindingTemplate), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("businessEntity", typeof(businessEntity), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("businessService", typeof(businessService), Namespace = "urn:uddi-org:api_v3")]
        [System.Xml.Serialization.XmlElementAttribute("tModel", typeof(tModel), Namespace = "urn:uddi-org:api_v3")]
        public object Item
        {
            get
            {
                return this.itemField;
            }
            set
            {
                this.itemField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public operationalInfo operationalInfo
        {
            get
            {
                return this.operationalInfoField;
            }
            set
            {
                this.operationalInfoField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordDelete
    {

        private string businessKeyField;

        private string tModelKeyField;

        private string serviceKeyField;

        private string bindingKeyField;

        private System.DateTime modifiedField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        public string businessKey
        {
            get
            {
                return this.businessKeyField;
            }
            set
            {
                this.businessKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        public string tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        public string serviceKey
        {
            get
            {
                return this.serviceKeyField;
            }
            set
            {
                this.serviceKeyField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        public string bindingKey
        {
            get
            {
                return this.bindingKeyField;
            }
            set
            {
                this.bindingKeyField = value;
            }
        }

        /// <remarks/>
        public System.DateTime modified
        {
            get
            {
                return this.modifiedField;
            }
            set
            {
                this.modifiedField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordPublisherAssertion
    {

        private publisherAssertion publisherAssertionField;

        private bool fromBusinessCheckField;

        private bool toBusinessCheckField;

        private System.DateTime modifiedField;

        private SignatureType[] fromSignaturesField;

        private SignatureType[] toSignaturesField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public publisherAssertion publisherAssertion
        {
            get
            {
                return this.publisherAssertionField;
            }
            set
            {
                this.publisherAssertionField = value;
            }
        }

        /// <remarks/>
        public bool fromBusinessCheck
        {
            get
            {
                return this.fromBusinessCheckField;
            }
            set
            {
                this.fromBusinessCheckField = value;
            }
        }

        /// <remarks/>
        public bool toBusinessCheck
        {
            get
            {
                return this.toBusinessCheckField;
            }
            set
            {
                this.toBusinessCheckField = value;
            }
        }

        /// <remarks/>
        public System.DateTime modified
        {
            get
            {
                return this.modifiedField;
            }
            set
            {
                this.modifiedField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("Signature", Namespace = "http://www.w3.org/2000/09/xmldsig#", IsNullable = false)]
        public SignatureType[] fromSignatures
        {
            get
            {
                return this.fromSignaturesField;
            }
            set
            {
                this.fromSignaturesField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("Signature", Namespace = "http://www.w3.org/2000/09/xmldsig#", IsNullable = false)]
        public SignatureType[] toSignatures
        {
            get
            {
                return this.toSignaturesField;
            }
            set
            {
                this.toSignaturesField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordHide
    {

        private string tModelKeyField;

        private System.DateTime modifiedField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3", DataType = "anyURI")]
        public string tModelKey
        {
            get
            {
                return this.tModelKeyField;
            }
            set
            {
                this.tModelKeyField = value;
            }
        }

        /// <remarks/>
        public System.DateTime modified
        {
            get
            {
                return this.modifiedField;
            }
            set
            {
                this.modifiedField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordDeleteAssertion
    {

        private publisherAssertion publisherAssertionField;

        private bool fromBusinessCheckField;

        private bool toBusinessCheckField;

        private System.DateTime modifiedField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(Namespace = "urn:uddi-org:api_v3")]
        public publisherAssertion publisherAssertion
        {
            get
            {
                return this.publisherAssertionField;
            }
            set
            {
                this.publisherAssertionField = value;
            }
        }

        /// <remarks/>
        public bool fromBusinessCheck
        {
            get
            {
                return this.fromBusinessCheckField;
            }
            set
            {
                this.fromBusinessCheckField = value;
            }
        }

        /// <remarks/>
        public bool toBusinessCheck
        {
            get
            {
                return this.toBusinessCheckField;
            }
            set
            {
                this.toBusinessCheckField = value;
            }
        }

        /// <remarks/>
        public System.DateTime modified
        {
            get
            {
                return this.modifiedField;
            }
            set
            {
                this.modifiedField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordAcknowledgement
    {

        private changeRecordID_type acknowledgedChangeField;

        /// <remarks/>
        public changeRecordID_type acknowledgedChange
        {
            get
            {
                return this.acknowledgedChangeField;
            }
            set
            {
                this.acknowledgedChangeField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordCorrection
    {

        private changeRecord changeRecordField;

        /// <remarks/>
        public changeRecord changeRecord
        {
            get
            {
                return this.changeRecordField;
            }
            set
            {
                this.changeRecordField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordNewDataConditional
    {

        private changeRecordNewData changeRecordNewDataField;

        /// <remarks/>
        public changeRecordNewData changeRecordNewData
        {
            get
            {
                return this.changeRecordNewDataField;
            }
            set
            {
                this.changeRecordNewDataField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class changeRecordConditionFailed
    {

        private changeRecordID_type failedChangeIDField;

        /// <remarks/>
        public changeRecordID_type failedChangeID
        {
            get
            {
                return this.failedChangeIDField;
            }
            set
            {
                this.failedChangeIDField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class notify_changeRecordsAvailable
    {

        private string notifyingNodeField;

        private changeRecordID_type[] changesAvailableField;

        /// <remarks/>
        [System.Xml.Serialization.XmlElementAttribute(DataType = "anyURI")]
        public string notifyingNode
        {
            get
            {
                return this.notifyingNodeField;
            }
            set
            {
                this.notifyingNodeField = value;
            }
        }

        /// <remarks/>
        [System.Xml.Serialization.XmlArrayItemAttribute("highWaterMark", IsNullable = false)]
        public changeRecordID_type[] changesAvailable
        {
            get
            {
                return this.changesAvailableField;
            }
            set
            {
                this.changesAvailableField = value;
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class do_ping
    {
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(AnonymousType = true, Namespace = "urn:uddi-org:repl_v3")]
    public partial class get_highWaterMarks
    {
    }

}
