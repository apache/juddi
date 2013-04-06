/*
 * Copyright 2001-2013 The Apache Software Foundation.
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

function AddName()
{
    currentNameEntries++;
    var i =  currentNameEntries;         
    $("<div id=\"Name" + i + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('Name" + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"Name" + i + "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_lang + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"Name" + i + "Lang\"></div>"
        +"</div>").prependTo("#nameContainer");
    Reedit();
}

//primarily used in edit service binding templates
function AddDescriptionSpecific(div)
{
    //javascript:Remove('bindingTemplate0Description0'); 
    currentDescriptionSpecific++;
    var i = currentDescriptionSpecific;
    $("<div id=\""+ div + i + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + div + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"" + div + i + "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_lang + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"" + div + i + "Lang\"></div>"
        +"</div>").prependTo("#" + div);
    Reedit();
}


function AddDescription()
{
    currentDescriptionEntries++;
    var i = currentDescriptionEntries;
    $("<div id=\"Description" + i + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('Description" + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"Description" + i + "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_lang + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"Description" + i + "Lang\"></div>"
        +"</div>").prependTo("#Description");
    Reedit();
}


function AddDisco()
{
    currentDisco++;
    var i=currentDisco;
    
    $("<div id=\"disco" + i + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('disco" + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"disco" + i + "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_type + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"disco" + i + "Type\"></div>"
        +"</div>").prependTo("#discoContainer");
    
    
    Reedit();
}



function AddContact()
{
    currentContacts++;
    var i=currentContacts;
    
    $("<div id=\"contact" + i + "\" style=\"border-width:2px; border-style:solid; border-color:red\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_contactType+ ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + i + "Type\">" + i18n_contactTypeDefault + "</div>"
        +"<a href=\"javascript:AddContactName('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>" + i18n_name + " &nbsp"
        +"<a href=\"javascript:AddContactEmail('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>" + i18n_email + " &nbsp"
        +"<a href=\"javascript:AddContactDescription('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>"+i18n_description+" &nbsp"
        +"<a href=\"javascript:AddContactPhone('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>" + i18n_phone + " &nbsp"
        +"<a href=\"javascript:AddContactAddress('" + i + "');\"><i class=\"icon-plus-sign\"></i></a>" + i18n_address + " &nbsp"
     
        +"</div>").prependTo("#contactsContainer");
    
    
    Reedit();
}
var contactname=0;
function AddContactName(contactid)
{
    $("<div id=\"contact" + contactid + "Name" + contactname + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Name" + contactname
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_name + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Name" + contactname + "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_lang + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Name" + contactname + "Lang\"></div>"
        +"</div>").insertAfter("#contact" + contactid);
    Reedit();
    contactname++;
}

var contactemail=0;
function AddContactEmail(contactid)
{
    $("<div id=\"contact" + contactid + "Email" + contactemail + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Email" + contactemail
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_type + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Email" + contactemail + "Type\">" + i18n_email+ "</div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Email" + contactemail + "Value\"></div>"
        +"</div>").insertAfter("#contact" + contactid);
    Reedit();
    contactemail++;
}

var contactdescription=0;
function AddContactDescription(contactid)
{
    $("<div id=\"contact" + contactid + "Description" + contactdescription + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Description" + contactdescription
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_description + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Description" + contactdescription + "Value\">"+i18n_contactPrimary+"</div>"
        +"<div style=\"float:left\">" + i18n_lang + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Description" + contactdescription + "Lang\"></div>"
        +"</div>").insertAfter("#contact" + contactid);
    Reedit();
    contactdescription++;
}
var contactphone=0;
function AddContactPhone(contactid)
{
    $("<div id=\"contact" + contactid + "Phone" + contactphone + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Phone" + contactphone
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_phone + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Phone" + contactphone + "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_type + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Phone" + contactphone + "Type\">" + i18n_phoneType + "</div>"
        +"</div>").insertAfter("#contact" + contactid);
    Reedit();
    contactphone++;
}

var contactaddress=0;
function AddContactAddress(contactid)
{
    $("<div id=\"contact" + contactid + "Address" + contactaddress + "\" style=\"border-width:1px; border-style:solid\" >" 
        
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "Address" + contactaddress
        +"');\"><i class=\"icon-remove-sign\"></i></a>" + i18n_address + "</div><br>"
        +"<div style=\"float:left\">" + i18n_lang + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Address" + contactaddress + "Lang\"></div>"
        +"<div style=\"float:left\">" + i18n_addressSortCode + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Address" + contactaddress + "Sortcode\">" + contactaddress +"</div>"
        +"<div style=\"float:left\">" + i18n_type + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Address" + contactaddress + "Type\">" + i18n_addressDefaultType + "</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "Address" + contactaddress + "KeyName\"></div>"
        +"<div><a href=\"javascript:AddContactAddressLine('" + contactid+ "Address" + 
        contactaddress+"');\"><i class=\"icon-plus-sign\"></i></a> " + i18n_addressLineAdd + "</div>"
        +"</div>").insertAfter("#contact" + contactid);
    Reedit();
    contactaddress++;
}

var contactaddresslines=0;
function AddContactAddressLine(contactid)
{
    $("<div id=\"contact" + contactid +  "addressLine" +contactaddresslines + 
        "\" style=\"border-width:1px; border-style:solid\" >" 
        
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('contact" + contactid + "addressLine" +contactaddresslines 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_addressValue + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "addressLine" + contactaddresslines + "Value\"></div>"
        +"<div style=\"float:left\">" +i18n_keyname_optional + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "addressLine" + contactaddresslines + "KeyName\"></div>"
        +"<div style=\"float:left\">" + i18n_keyvalue_optional + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"contact" + contactid + "addressLine" + contactaddresslines + "KeyValue\"></div>"
    
        +"</div>").insertAfter("#contact" + contactid);
    Reedit();
    contactaddress++;
}


function AddCategoryKeyReference()
{
    currentcatkeyref++;
    var i=currentcatkeyref;
    
    $("<div id=\"catbagkeyref" + i + "\" style=\"border-width:1px; border-style:solid\">"+
        "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('catbagkeyref" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>"+
        "<div style=\"float:left\">" + i18n_key +": &nbsp;</div>"
        + "<div class=\"edit\" id=\"catbagkeyref" + i + "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_name + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"catbagkeyref" + i + "KeyName\"></div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"catbagkeyref" + i + "KeyValue\"></div>"
        +"</div>").appendTo("#catContainer");
    Reedit();
}

function AddCategoryKeyReferenceGroup()
{
    currentcatkeyrefgrp++;
    var i=currentcatkeyrefgrp;
    
    $("<div id=\"catbaggrpkeyref" + i + "\" style=\"border-width:2px; border-style:solid\">"+
        "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('catbaggrpkeyref" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>"+
        "<div style=\"float:left\">" + i18n_key + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"catbaggrpkeyref" + i + "Value\"></div>"
        
        + "<div id=\"catbaggrpkeyref" + i + "keyref\" style=\"border-width:1px; border-style:solid\">"
        + "<div style=\"float:left;height:100%\"><a href=\"javascript:AddCategoryKeyReferenceGroupKeyRef('catbaggrpkeyref" + i + "keyref');\"><i class=\"icon-plus-sign\"></i></a></div>"
    
        +i18n_addrefcat
        + "</div>"
    
        +"</div>").appendTo("#catContainerGrp");
    Reedit();
}
var currentcatkeyrefgrpitems=0;
function AddCategoryKeyReferenceGroupKeyRef(div)
{
    var i=currentcatkeyrefgrp;
    currentcatkeyrefgrpitems++;
    var k=currentcatkeyrefgrpitems;
    
    $("<div id=\"catbaggrpkeyref" + i + "keyref" + k + "\" style=\"border-width:1px; border-style:solid\">"+
        "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('catbaggrpkeyref" + i + "keyref" + k+  
        "');\"><i class=\"icon-remove-sign\"></i></a></div>"+
        "<div style=\"float:left\">" + i18n_key + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"catbaggrpkeyref" + i +"keyref" + k+ "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_name + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"catbaggrpkeyref" + i +"keyref" + k+ "KeyName\"></div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"catbaggrpkeyref" + i +"keyref" + k+ "KeyValue\"></div>"
        +"</div>").appendTo("#" + div);
    Reedit();
}
function AddIdentKeyReference()
{
    currentident++;
    var i=currentident;
    
    $("<div id=\"identbagkeyref" + i + "\" style=\"border-width:1px; border-style:solid\">"+
        "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('identbagkeyref" + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>"+
        "<div style=\"float:left\">" + i18n_key + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"identbagkeyref" + i + "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_name + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"identbagkeyref" + i + "KeyName\"></div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"identbagkeyref" + i + "KeyValue\"></div>"
        +"</div>").insertAfter("#identContainer");
    Reedit();
}

function RemoveName(id)
{
    var x=$("#" + id).remove();
//   if (x.length > 0)
//       currentNameEntries--;
}


function Remove(id)
{
    $("#" + id).remove();
}



function deleteBusiness()
{
    //businessKey
    var bizid=$("#businessKey").text();
    var url='ajax/deletebusiness.jsp?id=' + bizid;
    var postbackdata = new Array();
    postbackdata.push({
        name:"nonce", 
        value: $("#nonce").val()
    });
    var request=   $.ajax({
        url: url,
        type:"POST",
        //  data" + i18n_type + ": "html", 
        cache: false, 
        //  processData: false,f
        data: postbackdata
    });

    request.done(function(msg) {
        window.console && console.log('postback done '  + url);                
        
        $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' + msg);
        $("#resultBar").show();
        
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('postback failed ' + url);                                
        $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>' + jqXHR.responseText );
        //$(".alert").alert();
        $("#resultBar").show();
        
    });
}


function saveBusiness()
{
    var url='ajax/savebusiness.jsp';
    var postbackdata = new Array();
    $("div.edit").each(function()
    {
        //TODO filter out (click to edit) values
        var id=$(this).attr("id");
        var value=$(this).text();
        postbackdata.push({
            name: id, 
            value: value
        });
    }); 
    postbackdata.push({
        name:"nonce", 
        value: $("#nonce").val()
    });
    $("div.noedit").each(function()
    {
        var id=$(this).attr("id");
        var value=$(this).text();
        postbackdata.push({
            name: id, 
            value: value
        });
    }); 
    
    
    var request=   $.ajax({
        url: url,
        type:"POST",
        //  data" + i18n_type + ": "html", 
        cache: false, 
        //  processData: false,f
        data: postbackdata
    });
                
                
    request.done(function(msg) {
        window.console && console.log('postback done '  + url);                
        
        $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;'  + '</a>' + msg);
        $("#resultBar").show();
        
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('postback failed ' + url);                                
        $("#resultBar").html('<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>' +jqXHR.responseText );
        //$(".alert").alert();
        $("#resultBar").show();
        
    });
}