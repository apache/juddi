/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//init the editable fields
Reedit();



function AddBindingTemplate()
{
    

    currentbindingtemplates++;
    var i =  currentbindingtemplates;         
    $("<div id=\"bindingTemplate" + i + "\" style=\"border-width:1px; border-style:solid\" >" 
        +"<div style=\"float:left;height:100%\"><a href=\"javascript:Remove('bindingTemplate" + i 
        +"');\"><i class=\"icon-remove-sign\"></i></a></div>"
        +"<div class=\"editrableSelectAccessPoint\"></div>"
        +"<div style=\"float:left\">Value: &nbsp;</div>"
        +"<div class=\"edit\" id=\"Name" + i + "Value\"></div>"
        +"<div style=\"float:left\">Language: &nbsp;</div>"
        +"<div class=\"edit\" id=\"Name" + i + "Lang\"></div>"
        +"</div>").appendTo("#bindingTemplatesContainer");
    Reedit();
    reselect();

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
    
}


function saveService()
{
    
}
function deleteService()
{
    
}