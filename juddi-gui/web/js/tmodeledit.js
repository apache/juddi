/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


function AddOverviewDocument()
{
    currentOverviewDocs++;
    var i = currentOverviewDocs;
    $("<div id=\"overviewDoc" + i + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('overviewDoc" + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">Value: &nbsp;</div>"
        +"<div class=\"edit\" id=\"overviewDoc" + i + "Value\"></div>"
        +"<div style=\"float:left\">Use Type: &nbsp;</div>"
        +"<div class=\"edit\" id=\"overviewDoc" + i + "Type\"></div>"
        //descriptions
        +"<a href=\"javascript:AddDescriptionSpecific('overviewDoc" + i + "Description');\">"
        +"<i class=\"icon-plus-sign\"></i></a> Add a description"
        + ("<div id=\"overviewDoc" + i + "Description\" style=\"border-width:1px; border-style:dotted\"></div>")
    
        +"</div>").prependTo("#overviewDoc");
    Reedit();
}


function AddDescriptionOverviewSpecific(div)
{
    //javascript:Remove('bindingTemplate0Description0'); 
    currentDescriptionSpecific++;
    var i = currentDescriptionSpecific;
    $("<div id=\""+ div + i + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('" + div + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div style=\"float:left\">Value: &nbsp;</div>"
        +"<div class=\"edit\" id=\"" + div + "Description" + i + "Value\"></div>"
        +"<div style=\"float:left\">Language: &nbsp;</div>"
        +"<div class=\"edit\" id=\"" + div + "Description" + i + "Lang\"></div>"
        +"</div>").appendTo("#" + div);
    Reedit();
}


function savetModel()
{
    var url='ajax/savetmodel.jsp';
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

function deletetModel()
{
    //businessKey
    var bizid=$("#serviceKey").text();
    var url='ajax/deletetmodel.jsp?id=' + bizid;
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