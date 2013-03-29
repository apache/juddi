/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//init the editable fields
Reedit();

function AddCategoryKeyReferenceSpecific (div)
{
    currentcatkeyrefBT++;
    var i=currentcatkeyrefBT;
    
    $("<div id=\""+div + i + "\" style=\"border-width:1px; border-style:solid\">"+
        "<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('"+div + i + "');\"><i class=\"icon-remove-sign\"></i></a></div>"+
        "<div style=\"float:left\">Key: &nbsp;</div>"
        + "<div class=\"edit\" id=\""+div + i + "Value\"></div>"
        +"<div style=\"float:left\">Name: &nbsp;</div>"
        + "<div class=\"edit\" id=\""+div + i + "KeyName\"></div>"
        +"<div style=\"float:left\">Value: &nbsp;</div>"
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
        "<div style=\"float:left\">Key: &nbsp;</div>"
        + "<div class=\"edit\" id=\"" +div+ i + "Value\"></div>"
        
        + "<div id=\""+div + i + "keyref\" style=\"border-width:1px; border-style:solid\">"
        + "<div style=\"float:left;height:100%\"><a href=\"javascript:AddCategoryKeyReferenceGroupKeyRef('"+div + i + "keyref');\"><i class=\"icon-plus-sign\"></i></a></div>"
        +"Add Key Reference"
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
        +"<div style=\"float:left\">Value: &nbsp;</div>"
        +"<div class=\"edit\" id=\"" +div+ i + "Value\"></div>"
        +"<div style=\"float:left\">Use Type: &nbsp;</div>"
        +"<div class=\"edit\" id=\"" + div + i + "Type\"></div>"
        //descriptions
        +"<a href=\"javascript:AddDescriptionSpecific('" + div + i + "Description');\">"
        +"<i class=\"icon-plus-sign\"></i></a> Add a description"
        + ("<div id=\"" + div + i + "Description\" style=\"border-width:1px; border-style:dotted\"></div>")
    
        +"</div>").prependTo("#" + div);
    Reedit();
}

function AddBindingTemplate()
{
    

    currentbindingtemplates++;
    var i =  currentbindingtemplates;         
    /*  $("<div id=\"bindingTemplate" + i + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('bindingTemplate" + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div class=\"editrableSelectAccessPoint\"></div>"
        +"<div style=\"float:left\">Value: &nbsp;</div>"
        +"<div class=\"edit\" id=\"Name" + i + "Value\"></div>"
        +"<div style=\"float:left\">Language: &nbsp;</div>"
        +"<div class=\"edit\" id=\"Name" + i + "Lang\"></div>"
        +"</div>").appendTo("#bindingTemplatesContainer");
*/    
    $("<br><div id=\"bindingTemplate" + i + "\" style=\"border-width: 2px; border-style: dashed;; border-color: lightseagreen\"><div style=\"float:left\"><a href=\"javascript:Remove('bindingTemplate"+i+"');\"><i class=\"icon-remove-sign\"></i></a>Binding Template Key: &nbsp;</div><div class=\"edit\" id=\"bindingTemplate"+i+"Value\">"
        +"</div><br><a href=\"javascript:AddDescriptionSpecific('bindingTemplate"+i+"Description');\"><i class=\"icon-plus-sign\"></i></a>Add a Binding Template Description - binding templates can have more than one description, such as in a different language.<br>"
        +"<div id=\"bindingTemplate"+i+"Description\" style=\"border-width: 1px; border-style: dotted;\"></div>"
        +"<b>Access Point</b> - UDDI allows for a choice of either a Hosting Redirector OR an Access Point. Access Point is recommend. The access point is usually a URL for the service endpoint.<br>"
        +"<div style=\"float:left\">Access Point Type: &nbsp;</div><div class=\"edit\" id=\"bindingTemplate"+i+"accessPointType\"></div><div style=\"float:left\">Access Point Value: &nbsp;</div><div class=\"edit\" id=\"bindingTemplate"+i+"accessPointValue\"></div></div>"
        +"<br><b>tModel Instance Information</b> - a binding template can have additional information attached to it using the tModel Instance.<br>"
        +"<a href=\"javascript:AddTmodelInstance('bindingTemplate"+i+"tmodelInstance');\"><i class=\"icon-plus-sign\"></i></a> Add a tModel Instance<br>"
        +"<div id=\"bindingTemplate"+i+"tmodelInstance\" style=\"border-width: 1px; border-style: solid; border-color: red\"></div><br>"
        +"<b>Binding Template Keyed Reference Categories:</b><br><a href=\"javascript:AddCategoryKeyReferenceSpecific('bindingTemplate"+i+"catbagkeyref');\"><i class=\"icon-plus-sign\"></i></a> Add Key Reference Category <br>"
        +"<div id=\"bindingTemplate"+i+"catbagkeyref\" style=\"border-width: 1px; border-style: dotted;\"></div><br><b>Binding Template Keyed Reference Groups</b><br>"
        +"<a href=\"javascript:AddCategoryKeyReferenceGroupSpecificBT('bindingTemplate"+i+"catbaggrpkeyref');\"><i class=\"icon-plus-sign\"></i></a> Add Key Reference Group Category<br>"
        +"<div id=\"bindingTemplate"+i+"catbaggrpkeyref\" style=\"border-width: 1px; border-style: dotted;\"></div></div>").appendTo("#bindingTemplatesContainer");

    Reedit();
//reselect();

}

function reselect()
{
    $('.editableSelectAccessPoint').editable(null, { 
        data   : " {'accessPoint':'accessPoint','HostingRedirector':'HostingRedirector', 'selected':'accessPoint'}",
        type   : 'select'
    });
}

function AddTmodelInstance(div)
{
    currentbindingtemplatesInstance++;
    var i =  currentbindingtemplatesInstance;         
    
    $("<div id=\"" + div + i + "\" style=\"border-width: 2px; border-style: dashed; border-color: red\">"        
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + div + i + "');\"><i class=\"icon-remove-sign\"></i></a></div><div style=\"float:left\"><b>tModel Key: </b>&nbsp;</div><div class=\"edit\" id=\"" + div + i + "KeyName\"></div>"
        +"<br><div style=\"float:left\"><b>tModel Instance Parameters:</b> &nbsp;</div><div class=\"edit\" id=\"" + div + i + "instanceValue\"></div>"
        +"<br><b>tModel Instance Description</b> - tModel instance infos can have more than one description, such as in a different language.<br>"
        +"<a href=\"javascript:AddDescriptionSpecific('" + div + i + "instanceDescription');\"><i class=\"icon-plus-sign\"></i></a> Add a tModel Instance Description<br>"
        +"<div id=\"" + div + i + "instanceDescription\" style=\"border-width: 1px; border-style: groove;\">"
        //issue

        +"<div><br><b>Overview Documents</b> - These are typically URLs to web pages that describe this tModel's details and usage scenarios.<br>"
        +"<a href=\"javascript:AddOverviewDocumentSpecific('" + div + i + "instanceoverviewDoc');\"><i class=\"icon-plus-sign\"></i></a> Add an Overview Document<br>"
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
        //  dataType: "html", 
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
        //  dataType: "html", 
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