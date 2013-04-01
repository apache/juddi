/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var loggedin=false;

var tagsToReplace = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;'
};

function replaceTag(tag) {
    return tagsToReplace[tag] || tag;
}

function safe_tags_replace(str) {
    return str.replace(/[&<>]/g, replaceTag);
}

  
function Reedit()
{
   window.console && console.log('Reedit');                
    $('.edit').editable(function(value, settings) { 
                
        window.console && console.log(this);
        window.console && console.log(value);
        window.console && console.log(settings);
        //replace tags with escaped characters to prevent XSS
        return(safe_tags_replace(value));
    }, { 
        type    : 'text',
        submit  : 'OK'
    });
 
}

Reedit();


function Login()
{
    
    $("#loginbutton").addClass("disabled");
    $("#loginbutton").text("Please wait");
    
    var form = $("#uddiform");
    var d = form.serializeArray();
    var request=   $.ajax({
        url: 'ajax/loginpost.jsp',
        type:"POST",
        //  dataType: "html", 
        cache: false, 
        //  processData: false,f
        data: d
    });
                  
    request.done(function(msg) {
        window.console && console.log('postback done ');                
        $("#loginbutton").text("Login");
        RefreshLoginPage();
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('postback failed ');                                
        //TODO handle expired nonce values?
        RefreshLoginPage();
        $("#loginbutton").text("Login");
    });
}

function logout()
{
    
    $.get('logout.jsp', function(data) {
        window.location = "index.jsp";
    });
}

function RefreshLoginPage()
{
    $.get('login.jsp', function(data) {
        $('#loginfield').html(data);
    });
}

// Escapes special characters and returns a valid jQuery selector
//source http://totaldev.com/content/escaping-characters-get-valid-jquery-id
function jqSelector(str)
{
	return str.replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1');
}

function ShowServicesByBusinessKey(bizid)
{
    window.console && console.log('fetching service list for business ' + bizid);                
    var request=   $.ajax({
        url: 'ajax/servicelist.jsp?id=' + bizid,
        type:"GET",
        cache: false
    });
                  
    request.done(function(msg) {
        window.console && console.log(msg);                
        window.console && console.log('postback done to div ' + bizid);                
        $("#" + jqSelector(bizid)).html(msg);
    //refresh();
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('postback failed ');                                
        $("#" + jqSelector(bizid)).html("An error occured! " + textStatus + jqXHR);
    //refresh();
    });

//    $.get('ajax/servicelist.jsp?id=' + bizid, function(data) {
//        $("#" + bizid).html(data);
//    });
}

function ShowBusinssDetails(bizid)
{
    $.get('ajax/businessdetails.jsp?id=' + bizid, function(data) {
        $('#servicelist').html(data);
    });
}

function GetServiceDetails(svcid)
{
    $.get('ajax/servicedetails.jsp?id=' + svcid, function(data) {
        $('#servicelist').html(data);
        $('.editable').editable('ajax/saveservicedetails.jsp');
    });
}


function hideAlert()
{
    $("#resultBar").hide();
}