using System;
using System.Collections.Generic;
using System.Text;

namespace org.uddi.apiv3
{

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void find_bindingCompletedEventHandler(object sender, find_bindingCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class find_bindingCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal find_bindingCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public bindingDetail Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((bindingDetail)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void find_businessCompletedEventHandler(object sender, find_businessCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class find_businessCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal find_businessCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public businessList Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((businessList)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void find_relatedBusinessesCompletedEventHandler(object sender, find_relatedBusinessesCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class find_relatedBusinessesCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal find_relatedBusinessesCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public relatedBusinessesList Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((relatedBusinessesList)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void find_serviceCompletedEventHandler(object sender, find_serviceCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class find_serviceCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal find_serviceCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public serviceList Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((serviceList)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void find_tModelCompletedEventHandler(object sender, find_tModelCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class find_tModelCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal find_tModelCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public tModelList Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((tModelList)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_bindingDetailCompletedEventHandler(object sender, get_bindingDetailCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_bindingDetailCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_bindingDetailCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public bindingDetail Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((bindingDetail)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_businessDetailCompletedEventHandler(object sender, get_businessDetailCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_businessDetailCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_businessDetailCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public businessDetail Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((businessDetail)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_operationalInfoCompletedEventHandler(object sender, get_operationalInfoCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_operationalInfoCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_operationalInfoCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public operationalInfos Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((operationalInfos)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_serviceDetailCompletedEventHandler(object sender, get_serviceDetailCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_serviceDetailCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_serviceDetailCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public serviceDetail Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((serviceDetail)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_tModelDetailCompletedEventHandler(object sender, get_tModelDetailCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_tModelDetailCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_tModelDetailCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public tModelDetail Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((tModelDetail)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void add_publisherAssertionsCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void delete_bindingCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void delete_businessCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void delete_publisherAssertionsCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void delete_serviceCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void delete_tModelCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_assertionStatusReportCompletedEventHandler(object sender, get_assertionStatusReportCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_assertionStatusReportCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_assertionStatusReportCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public assertionStatusItem[] Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((assertionStatusItem[])(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_publisherAssertionsCompletedEventHandler(object sender, get_publisherAssertionsCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_publisherAssertionsCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_publisherAssertionsCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public publisherAssertion[] Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((publisherAssertion[])(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_registeredInfoCompletedEventHandler(object sender, get_registeredInfoCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_registeredInfoCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_registeredInfoCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public registeredInfo Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((registeredInfo)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void save_bindingCompletedEventHandler(object sender, save_bindingCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class save_bindingCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal save_bindingCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public bindingDetail Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((bindingDetail)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void save_businessCompletedEventHandler(object sender, save_businessCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class save_businessCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal save_businessCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public businessDetail Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((businessDetail)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void save_serviceCompletedEventHandler(object sender, save_serviceCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class save_serviceCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal save_serviceCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public serviceDetail Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((serviceDetail)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void save_tModelCompletedEventHandler(object sender, save_tModelCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class save_tModelCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal save_tModelCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public tModelDetail Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((tModelDetail)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void set_publisherAssertionsCompletedEventHandler(object sender, set_publisherAssertionsCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class set_publisherAssertionsCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal set_publisherAssertionsCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public publisherAssertion[] Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((publisherAssertion[])(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void discard_authTokenCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_authTokenCompletedEventHandler(object sender, get_authTokenCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_authTokenCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_authTokenCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public authToken Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((authToken)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void discard_transferTokenCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_transferTokenCompletedEventHandler(object sender, get_transferTokenCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_transferTokenCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_transferTokenCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public transferToken Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((transferToken)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void transfer_entitiesCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_changeRecordsCompletedEventHandler(object sender, get_changeRecordsCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_changeRecordsCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_changeRecordsCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public changeRecord[] Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((changeRecord[])(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void notify_changeRecordsAvailableCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void do_pingCompletedEventHandler(object sender, do_pingCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class do_pingCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal do_pingCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public string Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((string)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_highWaterMarksCompletedEventHandler(object sender, get_highWaterMarksCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_highWaterMarksCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_highWaterMarksCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public changeRecordID_type[] Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((changeRecordID_type[])(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void transfer_custodyCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void delete_subscriptionCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_subscriptionResultsCompletedEventHandler(object sender, get_subscriptionResultsCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_subscriptionResultsCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_subscriptionResultsCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public subscriptionResultsList Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((subscriptionResultsList)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_subscriptionsCompletedEventHandler(object sender, get_subscriptionsCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_subscriptionsCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_subscriptionsCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public subscription[] Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((subscription[])(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void save_subscriptionCompletedEventHandler(object sender, save_subscriptionCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class save_subscriptionCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal save_subscriptionCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public subscription[] Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((subscription[])(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void notify_subscriptionListenerCompletedEventHandler(object sender, notify_subscriptionListenerCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class notify_subscriptionListenerCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal notify_subscriptionListenerCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public dispositionReport Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((dispositionReport)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void validate_valuesCompletedEventHandler(object sender, validate_valuesCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class validate_valuesCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal validate_valuesCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public dispositionReport Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((dispositionReport)(this.results[0]));
            }
        }
    }

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    public delegate void get_allValidValuesCompletedEventHandler(object sender, get_allValidValuesCompletedEventArgs e);

    /// <remarks/>
    [System.CodeDom.Compiler.GeneratedCodeAttribute("wsdl", "4.0.30319.17929")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class get_allValidValuesCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs
    {

        private object[] results;

        internal get_allValidValuesCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
            base(exception, cancelled, userState)
        {
            this.results = results;
        }

        /// <remarks/>
        public validValuesList Result
        {
            get
            {
                this.RaiseExceptionIfNecessary();
                return ((validValuesList)(this.results[0]));
            }
        }
    }

}
