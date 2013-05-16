/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */


package org.uddi.sub_v3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <h1>UDDI Subscription</h1>
 * The save_subscription API registers a request to monitor specific registry content and to have the node periodically notify the subscriber when changes are available.  Notifications are not returned synchronously with results for this API.  Only data that matches the requested subscription criteria and which changes after the point in time that the subscription request is accepted is returned to the subscriber via a notification.

This API returns a duration for which this particular subscription is valid.  Depending upon the policy of the Node, subscriptions need to be renewed before the expiration date in order to insure that they remain active.  Subscriptions can also be redefined or renewed using this API.  The subscriptionKey pertaining to the subscription to be renewed must be supplied in the save_subscription invocation in order to accomplish this. This allows both renewal and changes to the subscription.  Invoking save_subscription automatically resets the expiration period for the subscription in question to a value based upon the node’s policy.
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b><i>authInfo</i></b>:&nbsp; This optional argument is an element
that contains an authentication token.&nbsp; Registries that wish to restrict who
can save a subscription typically require authInfo for this call, though this
is a matter of node policy.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b><i>bindingKey:</i></b>&nbsp; This optional argument of type anyURI
specifies the <i>bindingTemplate</i> which the node is to use to deliver
notifications to subscription listeners.&nbsp; It is only required when asynchronous
notifications are used.&nbsp; This <i>bindingTemplate</i> MUST define either a Web
service that implements notify_subscriptionListener (see below), or an email
address to receive the notifications. If a notify_subscriptionListener Web
service is identified, the node invokes it to deliver notifications.&nbsp; If an
email address is identified, the node delivers notifications via email to the
address supplied. When notifications are delivered via email, the body of the
email contains the body of the SOAP message, which would have been sent to the
notify_subscriptionListener service if that option had been chosen.<span class="MsoCommentReference"><span style="display:none">.</span></span> The
publisher making the subscription request MUST own the bindingTemplate.&nbsp; If
this argument is not supplied, no notifications are sent, although subscribers
may still use the get_subscriptionResults API to obtain subscription results.&nbsp;
See Section <a href="#_Ref536844845 ">5.5.11</a> <i>get_subscriptionResults </i>for
details.&nbsp; If email delivery to the specified address fails, nodes MAY attempt re-delivery, but are not obligated to do so.&nbsp; Depending upon node policy, excessive
delivery failures MAY result in cancellation of the corresponding subscription.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b><i>brief</i></b>:&nbsp; This optional argument controls the level
of detail returned to a subscription listener.&nbsp; The default is "false"
when omitted. When set to "true," it indicates that the subscription
results are to be returned to the subscriber in the form of a keyBag, listing
all of the entities that matched the subscriptionFilter.&nbsp; Refer to Section <a href="#_Ref3401043 ">5.5.6</a> <i>Use of keyBag in Subscription,</i> for
additional information.&nbsp; This option has no effect on the assertionStatusReport
structure, which is returned as part of a notification when the
subscriptionFilter specifies the get_assertionStatusReport filter criteria.&nbsp;
See the explanation of subscriptionFilter below.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b><i>expiresAfter</i></b>:&nbsp; This optional argument allows
subscribers to specify the period of time for which they would like the
subscription to exist.&nbsp; It is of the XML Schema type xsd:dateTime.&nbsp; Specifying
a value for this argument is no guarantee that the node will accept it without
change.&nbsp; Information on the format of expiresAfter can be found in Section <a href="#_Ref535515666 ">5.5.1.1</a> <i>Specifying Durations</i>.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b><i>maxEntities</i></b>:&nbsp; This optional integer specifies the
maximum number of entities in a notification returned to a subscription
listener. If not specified, the number of entities sent is not limited, unless
by node policy.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b><i>subscriptionFilter</i></b>:&nbsp; This argument specifies the
filtering criteria which limits the scope of a subscription to a subset of
registry records. It is required except when renewing an existing subscription.
The get_xx and find_xx APIs are all valid choices for use as a
subscriptionFilter.&nbsp; Only one of these can be chosen for each subscription.&nbsp;
Notifications, based on the subscriptionFilter, are sent to the subscriber if
and only if there are changes at the node, which match this criterion during a
notification period.&nbsp; A subscriptionFilter MUST contain exactly one of the
allowed inquiry elements. The authInfo argument of the specified get_xx or
find_xx API call is not required here and is ignored if specified.&nbsp; All of the
other arguments supported with each of these inquiry APIs are valid for use
here.</p>

<p class="MsoBodyText" style="margin-left:1.0in">Specifying find_relatedBusinesses
is useful for tracking when reciprocal relationships are formed or dissolved.&nbsp;
Specifying get_assertionStatusReport can be used in tracking when reciprocal
relationships (which pertain to a business owned by the subscriber) are formed,
dissolved, or requested by the owners of some other business.</p>

<p class="MsoBodyText" style="margin-left:1.0in">For a get_assertionStatusReport
based subscription, there is a specific status value, <b>status:both_incomplete</b>,
defined in the XML schema. When appearing in an assertionStatusItem of a
subscriptionResultsList, status:both_incomplete indicates that the publisher
assertion embedded in the assertionStatusItem has been deleted from both ends. </p>

<p class="MsoBodyText" style="margin-left:1.0in">Note that the above handling of
deleted publisher assertions is different from the case when a business entity,
business service, binding template, or tModel is removed. In the latter case,
the key to the entity in question is simply put inside a keyBag. A publisher
assertion, on the other hand, has no key and therefore the keyBag idea is not
applicable.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b><i>subscriptionKey</i></b>:&nbsp; This optional argument of type <i>anyURI</i>
identifies the subscription.&nbsp; To renew or change an existing subscription, a
valid subscriptionKey MUST be provided. When establishing a new subscription,
the subscriptionKey MAY also be either omitted or specified as an empty string in
which case the node MUST assign a unique key. If subscriptionKey is specified
for a new subscription, the key MUST conform to the registry’s policy on
publisher-assigned keys.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b><i>notificationInterval</i></b>:&nbsp; This optional argument is
only required when asynchronous notifications are used.&nbsp; It is of type
xsd:duration and specifies how often change notifications are to be provided to
a subscriber.&nbsp; If the notificationInterval specified is not acceptable due to
node policy, then the node adjusts the value to match the next longer time
period that is supported.&nbsp; The adjusted value is provided with the returns from
this API.&nbsp; Also see Section <a href="#_Ref535515666 ">5.5.1.1</a> <i>Specifying
Durations</i>.</p>
 * <p>Java class for subscription complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subscription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:sub_v3}subscriptionKey" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}subscriptionFilter" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:api_v3}bindingKey" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}notificationInterval" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}maxEntities" minOccurs="0"/>
 *         &lt;element ref="{urn:uddi-org:sub_v3}expiresAfter" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="brief" type="{urn:uddi-org:sub_v3}brief" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subscription", propOrder = {
    "subscriptionKey",
    "subscriptionFilter",
    "bindingKey",
    "notificationInterval",
    "maxEntities",
    "expiresAfter"
})
public class Subscription implements Serializable{
	@XmlTransient
	private static final long serialVersionUID = -1016771256986173140L;
	protected String subscriptionKey;
    protected SubscriptionFilter subscriptionFilter;
    @XmlElement(namespace = "urn:uddi-org:api_v3")
    protected String bindingKey;
    protected Duration notificationInterval;
    protected Integer maxEntities;
    protected XMLGregorianCalendar expiresAfter;
    @XmlAttribute
    protected Boolean brief;

    /**
     * Gets the value of the subscriptionKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    /**
     * Sets the value of the subscriptionKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriptionKey(String value) {
        this.subscriptionKey = value;
    }

    /**
     * Gets the value of the subscriptionFilter property.
     * 
     * @return
     *     possible object is
     *     {@link SubscriptionFilter }
     *     
     */
    public SubscriptionFilter getSubscriptionFilter() {
        return subscriptionFilter;
    }

    /**
     * Sets the value of the subscriptionFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriptionFilter }
     *     
     */
    public void setSubscriptionFilter(SubscriptionFilter value) {
        this.subscriptionFilter = value;
    }

    /**
     * Gets the value of the bindingKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBindingKey() {
        return bindingKey;
    }

    /**
     * Sets the value of the bindingKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBindingKey(String value) {
        this.bindingKey = value;
    }

    /**
     * Gets the value of the notificationInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getNotificationInterval() {
        return notificationInterval;
    }

    /**
     * Sets the value of the notificationInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setNotificationInterval(Duration value) {
        this.notificationInterval = value;
    }

    /**
     * Gets the value of the maxEntities property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxEntities() {
        return maxEntities;
    }

    /**
     * Sets the value of the maxEntities property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxEntities(Integer value) {
        this.maxEntities = value;
    }

    /**
     * Gets the value of the expiresAfter property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiresAfter() {
        return expiresAfter;
    }

    /**
     * Sets the value of the expiresAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiresAfter(XMLGregorianCalendar value) {
        this.expiresAfter = value;
    }

    /**
     * Gets the value of the brief property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBrief() {
        return brief;
    }

    /**
     * Sets the value of the brief property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBrief(Boolean value) {
        this.brief = value;
    }

}
