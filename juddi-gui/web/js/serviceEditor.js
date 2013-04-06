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


//init the editable fields
Reedit();

function AddCategoryKeyReferenceSpecific (div)
{
    currentcatkeyrefBT++;
    var i=currentcatkeyrefBT;
    
    $("<div id=\""+div + i + "\" style=\"border-width:1px; border-style:solid\">"+
        "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('"+div + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>"+
        "<div style=\"float:left\">" + i18n_key + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\""+div + i + "Value\"></div>"
        +"<div style=\"float:left\">" + i18n_name + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\""+div + i + "KeyName\"></div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"" +div+ i + "KeyValue\"></div>"
        +"</div>").appendTo("#"+div);
    Reedit();
}


function AddCategoryKeyReferenceGroupSpecificBT(div)
{
    currentcatkeyrefgrpBT++;
    var i=currentcatkeyrefgrpBT;
    
    $("<div id=\""+div + i + "\" style=\"border-width:2px; border-style:solid\">"+
        "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('"+div + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>"+
        "<div style=\"float:left\">" + i18n_key + ": &nbsp;</div>"
        + "<div class=\"edit\" id=\"" +div+ i + "Value\"></div>"
        
        + "<div id=\""+div + i + "keyref\" style=\"border-width:1px; border-style:solid\">"
        + "<div style=\"float:left;height:100%\"><a href=\"javascript:AddCategoryKeyReferenceGroupKeyRef('"+div + i
        + "keyref');\"><i class=\"icon-plus-sign\"></i></a></div>"
        +i18n_addrefcat
        + "</div>"
    
        +"</div>").appendTo("#"+div);
    Reedit();
}

function AddOverviewDocumentSpecific(div)
{
    currentOverviewDocs++;
    var i = currentOverviewDocs;
    $("<div id=\"" + div + i + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + div + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">" + i18n_value + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"" +div+ i + "Value\"></div>"
        +"<div style=\"float:left\">Use " + i18n_type + ": &nbsp;</div>"
        +"<div class=\"edit\" id=\"" + div + i + "Type\"></div>"
        //descriptions
        +"<a href=\"javascript:AddDescriptionSpecific('" + div + i + "Description');\">"
        +"<i class=\"icon-plus-sign\"></i></a> " + i18n_descriptionAdd
        + ("<div id=\"" + div + i + "Description\" style=\"border-width:1px; border-style:dotted\"></div>")
    
        +"</div>").prependTo("#" + div);
    Reedit();
}

function AddBindingTemplate()
{
    

    currentbindingtemplates++;
    var i =  currentbindingtemplates;         
      
    $("<br><div id=\"bindingTemplate" + i + "\" style=\"border-width: 2px; border-style: dashed;; border-color: lightseagreen\"><div style=\"float:left\"><a href=\"javascript:Remove('bindingTemplate"+i+"');\"><i class=\"icon-remove-sign\"></i></a>" + i18n_bindingTemplateKey + ": &nbsp;</div><div class=\"edit\" id=\"bindingTemplate"+i+"Value\">"
        +"</div><br><a href=\"javascript:AddDescriptionSpecific('bindingTemplate"+i+"Description');\"><i class=\"icon-plus-sign\"></i></a>"
        +i18n_bindingTemplateDescriptionAdd
        +"<br>"
        +"<div id=\"bindingTemplate"+i+"Description\" style=\"border-width: 1px; border-style: dotted;\"></div>"
        +"<b>" + i18n_accesspoint + "</b> - " + i18n_accesspointDescription +"<br>"
        +"<div style=\"float:left\">" + i18n_accesspointType + ": &nbsp;</div><div class=\"edit\" id=\"bindingTemplate"+i+"accessPointType\"></div><div style=\"float:left\">" + i18n_accesspointValue + ": &nbsp;</div><div class=\"edit\" id=\"bindingTemplate"+i+"accessPointValue\"></div></div>"
        +"<br><b>" + i18n_tmodelinstanceinfo + "</b> - " + i18n_tmodelinstanceinfoDescription + "<br>"
        +"<a href=\"javascript:AddTmodelInstance('bindingTemplate"+i+"tmodelInstance');\"><i class=\"icon-plus-sign\"></i></a> " + i18n_tmodelinstanceinfoAdd 
        + "<br>"
        +"<div id=\"bindingTemplate"+i+"tmodelInstance\" style=\"border-width: 1px; border-style: solid; border-color: red\"></div><br>"
        +"<b>" + i18n_bindingTemplateKeyRefCat + ":</b><br><a href=\"javascript:AddCategoryKeyReferenceSpecific('bindingTemplate"+i+"catbagkeyref');\"><i class=\"icon-plus-sign\"></i></a> " + i18n_addrefcat + "<br>"
        +"<div id=\"bindingTemplate"+i+"catbagkeyref\" style=\"border-width: 1px; border-style: dotted;\"></div><br>"
        +"<b>" + i18n_bindingTemplateKeyRefCatGrp + "</b><br>"
        +"<a href=\"javascript:AddCategoryKeyReferenceGroupSpecificBT('bindingTemplate"+i+"catbaggrpkeyref');\"><i class=\"icon-plus-sign\"></i></a> " + i18n_keyRefGrpAdd + "<br>"
        +"<div id=\"bindingTemplate"+i+"catbaggrpkeyref\" style=\"border-width: 1px; border-style: dotted;\"></div></div>").appendTo("#bindingTemplatesContainer");

    Reedit();
}


function AddTmodelInstance(div)
{
    currentbindingtemplatesInstance++;
    var i =  currentbindingtemplatesInstance;         
    
    $("<div id=\"" + div + i + "\" style=\"border-width: 2px; border-style: dashed; border-color: red\">"        
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + div + i + "');\"><i class=\"icon-remove-sign\"></i></a></div><div style=\"float:left\">"
        +"<b>" + i18n_tmodelkey + ": </b>&nbsp;</div><div class=\"edit\" id=\"" + div + i + "KeyName\"></div>"
        +"<br><div style=\"float:left\"><b>" + i18n_tmodelInstanceParams + ":</b> &nbsp;</div><div class=\"edit\" id=\"" + div + i + "instanceValue\"></div>"
        +"<br><b>" + i18n_tmodelInstanceDescription + "</b> - " + i18n_tmodelInstanceDescription2 + "<br>"
        +"<a href=\"javascript:AddDescriptionSpecific('" + div + i + "instanceDescription');\"><i class=\"icon-plus-sign\"></i></a> "
        +i18n_tmodelInstanceDescriptionAdd+"<br>"
        +"<div id=\"" + div + i + "instanceDescription\" style=\"border-width: 1px; border-style: groove;\">"
        //issue

        +"<div><br><b>" + i18n_overviewdoc + "</b> - " + i18n_overviewdocDescription + "<br>"
        +"<a href=\"javascript:AddOverviewDocumentSpecific('" + div + i + 
        "instanceoverviewDoc');\"><i class=\"icon-plus-sign\"></i></a> "+ i18n_overviewdocadd + "<br>"
        +"<div id=\"" + div + i + "overviewDoc\" style=\"border-width: 1px; border-style: groove;\"></div></div></div></div></div>").appendTo("#"+div);
    Reedit();
}


function saveService()
{
    var url='ajax/saveservicedetails.jsp';
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
function deleteService()
{
    var bizid=$("#serviceKey").text();
    var url='ajax/deleteservice.jsp?id=' + bizid;
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