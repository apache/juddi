/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.validation.vsv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.InvalidValueException;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.KeyedReferenceGroup;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * To distinguish among various types of concept, UDDI has established the Types
 * category system. Publishers should categorize the tModels they publish using
 * values from uddi-org:types to make them easy to find. The approach to
 * categorization of tModels within the UDDI Type Category system is consistent
 * with that used for categorizing other entities using other category systems.
 * The categorization information for each tModel is added to the <categoryBag>
 * elements in a save_tModel API. One or more <keyedReference> elements are
 * added to the category bag to indicate the types of the tModel that is being
 * registered. See Appendix F Using Categorization for more information.
 *
 * <h4 style="margin-left:0in;text-indent:0in">11.1.1.4 Valid Values</h4>
 *
 * <p class="MsoBodyText">Checking of references to this value set consists of
 * ensuring that the keyValues are from the set of categories listed
 * below.&nbsp; No contextual checks are performed unless otherwise specified
 * for a given value.</p>
 *
 * <p class="MsoBodyText">The following constitute the value set for this
 * category system. The valid values are those categories marked as being
 * "allowed". These values are used in the keyValue attributes of keyedReference
 * elements that are contained in categoryBag elements.</p>
 *
 * <p class="MsoBodyText">&nbsp;</p>
 *
 * <table class="specTable"
 * style="width:6.0in;margin-left:.5in;border-collapse:collapse;border:none"
 * border="1" cellpadding="0" cellspacing="0" width="576">
 * <thead>
 * <tr style="page-break-inside:avoid;height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt;
 * background:#FFFFCA;padding:0in 5.4pt 0in 5.4pt;height:6.75pt" valign="top"
 * width="26%">
 * <p class="MsoNormal"><b>ID</b></p>
 * </td>
 * <td style="width:19.4%;border:solid black 1.0pt;
 * border-left:none;background:#FFFFCA;padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal"><b>Parent ID</b></p>
 * </td>
 * <td style="width:13.14%;border:solid black 1.0pt;
 * border-left:none;background:#FFFFCA;padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal"><b>Allowed</b></p>
 * </td>
 * <td style="width:40.68%;border:solid black 1.0pt;
 * border-left:none;background:#FFFFCA;padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal"><b>Description</b></p>
 * </td>
 * </tr>
 * </thead>
 * <tbody><tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">&nbsp;</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">No</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">These types are used for tModels</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; valueSet</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Value set</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp;&nbsp;&nbsp; identifier</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">valueSet</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Identifier system</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp;&nbsp;&nbsp; namespace</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">valueSet</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Namespace</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp;&nbsp;&nbsp; categorization </p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">valueSet</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Categorization system</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; postalAddress</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">categorization</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Postal address system</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; categorizationGroup</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Category group system </p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; relationship</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Relationship type system</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; specification </p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Specification for a Web service</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp;&nbsp;&nbsp; xmlSpec</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">specification</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Specification for a Web service using XML messages</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; soapSpec</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">xmlSpec</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Specification for interaction with a Web service using
 * SOAP messages</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp;&nbsp;&nbsp; wsdlSpec</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">specification</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Specification for a Web service described in WSDL</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; protocol</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Protocol</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp;&nbsp;&nbsp; transport</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">protocol</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Wire/transport protocol</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; signatureComponent</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Signature component</p>
 * </td>
 * </tr>
 * <tr style="height:6.75pt">
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt;height:6.75pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; unvalidatable</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in
 * 5.4pt;height:6.75pt" valign="top" width="40%">
 * <p class="MsoNormal">Prevents a checked value set from being used</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; checked</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Checked value set</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; unchecked</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Unchecked value set</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; cacheable</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Cacheable checked value set</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; uncacheable</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Uncacheable checked value set</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; keyGenerator</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Key generator (Note: A contextual check is performed as
 * specified below if this value is used)</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; findQualifier</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Find qualifier</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp;&nbsp;&nbsp; sortOrder</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">findQualifier</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Sort order</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; useTypeDesignator</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">tModel</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">Designates a kind of usage for the pieces of data with
 * which it is associated</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">bindingTemplate</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">&nbsp;</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">No</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">These types are used for bindingTemplates</p>
 * </td>
 * </tr>
 * <tr>
 * <td style="width:26.78%;border:solid black 1.0pt; border-top:none;padding:0in
 * 5.4pt 0in 5.4pt" valign="top" width="26%">
 * <p class="MsoNormal">&nbsp; wsdlDeployment</p>
 * </td>
 * <td style="width:19.4%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="19%">
 * <p class="MsoNormal">bindingTemplate</p>
 * </td>
 * <td style="width:13.14%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="13%">
 * <p class="MsoNormal">Yes</p>
 * </td>
 * <td style="width:40.68%;border-top:none;border-left: none;border-bottom:solid
 * black 1.0pt;border-right:solid black 1.0pt; padding:0in 5.4pt 0in 5.4pt"
 * valign="top" width="40%">
 * <p class="MsoNormal">bindingTemplate represents the WSDL deployment of a Web
 * service</p>
 * </td>
 * </tr>
 * </tbody></table>
 *
 * <p class="MsoBodyText">&nbsp;</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>tModel: The UDDI type category system is structured to allow
 * for categorization of registry entries other than tModels. This key is the
 * root of the branch of the category system that is intended for use in
 * categorization of tModels within the UDDI registry. Categorization is not
 * allowed with this key.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>valueSet: A valueSet is the parent branch for the identifier,
 * namespace, and categorization values in this category system. A tModel
 * categorized with this value indicates it can be referenced by some other
 * value set tModel to indicate redefinition of purpose, derivation, extension
 * or equivalence.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>identifier: An identifier tModel represents a specific set of
 * values used to uniquely identify information. Identifier tModels are intended
 * to be used in keyedReferences inside of identifierBags. For example, a Dun
 * &amp; Bradstreet D-U-N-S® Number uniquely identifies companies globally. The
 * D-U-N-S® Number system is an identifier system. </p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>namespace: A namespace tModel represents a scoping constraint
 * or domain for a set of information. In contrast to an identifier, a namespace
 * does not have a predefined set of values within the domain, but acts to avoid
 * collisions. It is similar to the namespace functionality used for XML. For
 * example, the uddi-org:relationships tModel, which is used to assert
 * relationships between businessEntity elements, is a namespace tModel.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>categorization: A categorization tModel is used for category
 * systems within the UDDI registry. NAICS and UNSPSC are examples of
 * categorization tModels.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>postalAddress: A postalAddress tModel is used to identify
 * different forms of postal address within the UDDI registry. postalAddress
 * tModels may be used with the address element to distinguish different forms
 * of postal address.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>categorizationGroup: A categorizationGroup tModel is used to
 * relate one or more category system tModels to one another so that they can be
 * used in keyedReferenceGroups.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>relationship: A relationship tModel is used for relationship
 * categorizations within the UDDI registry. relationship tModels are typically
 * used in connection with publisher relationship assertions.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>specification: A specification tModel is used for tModels that
 * define interactions with a Web service. These interactions typically include
 * the definition of the set of requests and responses, or other types of
 * interaction that are prescribed by the Web service. tModels describing XML,
 * COM, CORBA, or any other Web services are specification tModels.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>xmlSpec: An xmlSpec tModel is a refinement of the specification
 * tModel type. It is used to indicate that the interaction with the Web service
 * is via XML. The UDDI API tModels are xmlSpec tModels.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>soapSpec: Further refining the xmlSpec tModel type, a soapSpec
 * is used to indicate that the interaction with the Web service is via SOAP.
 * The UDDI API tModels are soapSpec tModels, in addition to xmlSpec
 * tModels.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>wsdlSpec: A tModel for a Web service described using WSDL is
 * categorized as a wsdlSpec.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>protocol: A tModel describing a protocol of any sort.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>transport: A transport tModel is a specific type of protocol.
 * HTTP, FTP, and SMTP are types of transport tModels.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>signatureComponent: A signature component is used to for cases
 * where a single tModel can not represent a complete specification for a Web
 * service. This is the case for specifications like RosettaNet, where
 * implementation requires the composition of three tModels to be complete - a
 * general tModel indicating RNIF, one for the specific PIP, and one for the
 * error handling services. Each of these tModels would be of type signature
 * component, in addition to any others as appropriate.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>unvalidatable: Used to mark a categorization or identifier
 * tModel as unavailable for use by keyedReferences.&nbsp; A value set provider
 * may mark its value set tModel <i>unvalidatable</i> if it wants to temporarily
 * disallow its use.&nbsp; See Section <a href="#_Ref8981064 ">6.4</a>
 * <i>Checked Value Set Validation</i> for more information.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>checked: Marking a tModel with this categorization asserts that
 * it represents a value set or category group system whose use, through
 * keyedReferences, may be checked.&nbsp; Registry, and possibly node policy
 * determines when and how a checked value set is supported.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>unchecked: Marking a tModel with this categorization asserts
 * that it represents a value set or category group system whose use, through
 * keyedReferences, is not checked.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>cacheable: Marking a tModel with this categorization asserts
 * that it represents a checked value set or category group system whose values
 * may be cached for validation.&nbsp; The validation algorithm for a supported
 * cacheable checked value set or category group system must rely solely upon
 * matching references against the cached set of&nbsp; values.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>uncacheable: Marking a tModel with this categorization asserts
 * that it represents a checked value set or category group system whose values
 * must not be cached for validation.&nbsp; The validation algorithm for a
 * supported uncacheable checked value set must be specified and associated with
 * the tModel marked with this categorization and may consider contextual
 * criteria involving the entity associated with the reference.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>keyGenerator: Marking a tModel with this categorization
 * designates it as one whose tModelKey identifies a key generator partition
 * that can be used by its owner to derive and assign other entity keys. This
 * categorization is reserved for key generator tModels. Any attempt to use this
 * categorization for something other than a key generator tModel will fail with
 * E_valueNotAllowed returned. </p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>findQualifier: A findQualifier tModel is used as the value of a
 * findQualifier element to indicate the type of processing to occur for the
 * inquiry function in which it is included.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>sortOrder: A sort order tModel defines a collation sequence
 * that can be used during inquiries to control ordering of the results.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>useTypeDesignator: A useTypeDesignator tModel is used to
 * describe the way a piece of data should be interpreted.&nbsp; It is
 * frequently used to extend the space of resource types found at a URI, such as
 * access points, overview URLs, and discovery URLs.&nbsp; UDDI designates a set
 * of common use types as simple strings; tModels of the useTypeDesignator type
 * are used to describe others.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>bindingTemplate: This key is the root of the branch of the
 * category system that is intended for use in categorization of
 * bindingTemplates within the UDDI registry. Categorization is not allowed with
 * this key.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span
 * style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span></span>wsdlDeployment: A bindingTemplate categorized as a
 * wsdlDeployment contains within its accessPoint the endpoint for a WSDL
 * deployment document.</p>
 *
 * @author Alex O'Ree
 */
public class Uddiuddiorgcategorizationtype implements ValueSetValidator {

        public static final String key = "uddi:uddi.org:categorization:types";

        private static Map<String, Boolean> NotAllowedOnBindings;
        private static Map<String, Boolean> NotAllowedOnTModels;

        static {
                NotAllowedOnTModels = new HashMap<String, Boolean>();
                NotAllowedOnTModels.put("wsdlDeployment".toLowerCase(), true);
                NotAllowedOnBindings = new HashMap<String, Boolean>();
                NotAllowedOnBindings.put("valueSet".toLowerCase(), true);
                NotAllowedOnBindings.put("identifier".toLowerCase(), true);
                NotAllowedOnBindings.put("namespace".toLowerCase(), true);
                NotAllowedOnBindings.put("categorization".toLowerCase(), true);
                NotAllowedOnBindings.put("postalAddress".toLowerCase(), true);
                NotAllowedOnBindings.put("categorizationGroup".toLowerCase(), true);
                NotAllowedOnBindings.put("relationship".toLowerCase(), true);
                NotAllowedOnBindings.put("specification".toLowerCase(), true);
                NotAllowedOnBindings.put("xmlSpec".toLowerCase(), true);
                NotAllowedOnBindings.put("soapSpec".toLowerCase(), true);
                NotAllowedOnBindings.put("wsdlSpec".toLowerCase(), true);
                NotAllowedOnBindings.put("protocol".toLowerCase(), true);
                NotAllowedOnBindings.put("transport".toLowerCase(), true);
                NotAllowedOnBindings.put("signatureComponent".toLowerCase(), true);
                NotAllowedOnBindings.put("unvalidatable".toLowerCase(), true);
                NotAllowedOnBindings.put("checked".toLowerCase(), true);
                NotAllowedOnBindings.put("unchecked".toLowerCase(), true);
                NotAllowedOnBindings.put("cacheable".toLowerCase(), true);
                NotAllowedOnBindings.put("uncacheable".toLowerCase(), true);
                NotAllowedOnBindings.put("keyGenerator".toLowerCase(), true);
                NotAllowedOnBindings.put("findQualifier".toLowerCase(), true);
                NotAllowedOnBindings.put("sortOrder".toLowerCase(), true);
                NotAllowedOnBindings.put("useTypeDesignator".toLowerCase(), true);
        }

        @Override
        public void validateValuesBindingTemplate(List<BindingTemplate> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                //can't use everything else
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                for (int k = 0; k < items.get(i).getCategoryBag().getKeyedReference().size(); k++) {
                                        if (items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey() != null) {
                                                if (NotAllowedOnBindings.containsKey(items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey().toLowerCase())) {
                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Value [" + items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey() + "] not allowed on bindingtemplates for key " + key));
                                                }
                                        }

                                }

                                for (int k = 0; k < items.get(i).getCategoryBag().getKeyedReferenceGroup().size(); k++) {
                                        if (items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getTModelKey() != null) {
                                                if (NotAllowedOnBindings.containsKey(items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getTModelKey().toLowerCase())) {
                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Value [" + items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getTModelKey() + "] not allowed on bindingtemplates for key " + key));
                                                }
                                                for (int j = 0; j < items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getKeyedReference().size(); j++) {
                                                        if (items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getKeyedReference().get(j).getTModelKey() != null) {
                                                                if (NotAllowedOnBindings.containsKey(items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getKeyedReference().get(j).getTModelKey().toLowerCase())) {
                                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Value [" + items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getKeyedReference().get(j).getTModelKey() + "] not allowed on bindingtemplates for key " + key));
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                        if (items.get(i).getTModelInstanceDetails() != null) {

                                for (int k = 0; i < items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().size(); k++) {
                                        if (key.equalsIgnoreCase(items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getTModelKey())) {
                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", key + " is not allowed on tModelInstanceInfos"));
                                        }
                                }
                        }
                }
        }

        @Override
        public void validateValuesBusinessEntity(List<BusinessEntity> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }

                //cant use the following
                //wsdlDeployment
                for (int i = 0; i < items.size(); i++) {

                        if (items.get(i).getBusinessServices() != null) {
                                validateValuesBusinessService(items.get(i).getBusinessServices().getBusinessService(), "businessEntity(" + i + ").");
                        }
                }
        }

        @Override
        public void validateValuesBusinessService(List<BusinessService> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                //No specific rules for business services

                        }
                        if (items.get(i).getBindingTemplates() != null) {
                                validateValuesBindingTemplate(items.get(i).getBindingTemplates().getBindingTemplate(), xpath + xpath + "businessService(" + i + ").identifierBag.");
                        }
                }
        }

        @Override
        public void validateValuesPublisherAssertion(List<PublisherAssertion> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                //no rules for PA
        }

        @Override
        public void validateTmodelInstanceDetails(List<TModelInstanceInfo> tModelInstanceInfo, String xpath) throws DispositionReportFaultMessage {

        }

        @Override
        public List<String> getValidValues() {
                List<String> ret = new ArrayList<String>();
                ret.addAll(NotAllowedOnBindings.keySet());
                ret.addAll(NotAllowedOnTModels.keySet());
                return ret;
        }

        @Override
        public void validateValuesTModel(List<TModel> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                for (int k = 0; k < items.get(i).getCategoryBag().getKeyedReference().size(); k++) {
                                        if (items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey() != null) {
                                                if (NotAllowedOnTModels.containsKey(items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey().toLowerCase())) {
                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Value [" + items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey() + "] not allowed on tModels for key " + key));
                                                }
                                        }

                                }

                                for (int k = 0; k < items.get(i).getCategoryBag().getKeyedReferenceGroup().size(); k++) {
                                        if (items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getTModelKey() != null) {
                                                if (NotAllowedOnTModels.containsKey(items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getTModelKey().toLowerCase())) {
                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Value [" + items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getTModelKey() + "] not allowed on tModels for key " + key));
                                                }
                                                for (int j = 0; j < items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getKeyedReference().size(); j++) {
                                                        if (items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getKeyedReference().get(j).getTModelKey() != null) {
                                                                if (NotAllowedOnTModels.containsKey(items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getKeyedReference().get(j).getTModelKey().toLowerCase())) {
                                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Value [" + items.get(i).getCategoryBag().getKeyedReferenceGroup().get(k).getKeyedReference().get(j).getTModelKey() + "] not allowed on tModels for key " + key));
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                        if (items.get(i).getIdentifierBag() != null) {
                                for (int k = 0; k < items.get(i).getCategoryBag().getKeyedReference().size(); k++) {
                                        if (items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey() != null) {
                                                if (NotAllowedOnTModels.containsKey(items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey().toLowerCase())) {
                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Value [" + items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey() + "] not allowed on tModels for key " + key));
                                                }
                                        }

                                }
                        }
                }
        }

}
