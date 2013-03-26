
var offset=0; //start at the begining
var maxrecords=20;  //record 20 at a time
var totalrecords=0;

function refreshServiceList()
{
    RenderServiceListBySearch('%', offset, maxrecords);    
}
RenderServiceListBySearch('%', offset, maxrecords);
//offset += maxrecords;
function pagedown()
{
    offset = $("#offset").text();
    //alert(offset);
    var newoffset = offset - maxrecords;
    if (newoffset < 0)
        return;
    //alert(newoffset);
    if (newoffset != offset)
        RenderServiceListBySearch('%', newoffset, maxrecords);
}
function pageup()
{
    offset = $("#offset").text();
    //alert(offset);
    var fetch = maxrecords;
    if ((parseInt(offset) + parseInt(maxrecords))  > totalrecords)
        //fetch = maxrecords - offset;
        return;
    else 
        fetch = (parseInt(offset) + parseInt(maxrecords));    
    //alert(fetch);
    offset = fetch;
    RenderServiceListBySearch('%', fetch, maxrecords);
}

function RenderServiceListBySearch(keyword, offset, maxrecords)
{
    var lang = $("#lang").text();
    $("#serviceBrowserListing").html("<img src=\"img/bigrollergreen.gif\" title=\"Loading\"/>");
    var request=   $.ajax({
        url: 'ajax/servicesearch.jsp?keyword=' + keyword + "&offset=" + offset + "&maxrecords=" + maxrecords + "&lang=" + lang,
        type:"GET",
        //  dataType: "html", 
        cache: false
    //  processData: false,f
    //data: d
    });
                  
    request.done(function(msg) {
        window.console && console.log('postback done ');                
        $("#serviceBrowserListings").html(msg);
    //refresh();
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('postback failed ');                                
        $("#serviceBrowserListings").html("An error occured! " + textStatus + jqXHR);
    //refresh();
    });
/*
    $.get('ajax/servicesearch.jsp?keyword=' + keyword + "&offset=" + offset + "&maxrecords=" + maxrecords, function(data) {
        $("#serviceBrowserListings").html(data);
        refresh();
    });*/
}

function refresh()
{
    var displayrecords = $("#displayrecords").text();
    if (displayrecords == totalrecords)
    {
        $("#pageup").addClass("disabled");
        $("#pagedown").addClass("disabled");
    }
    else if (offset + maxrecords > totalrecords)
    {
        $("#pageup").addClass("disabled");    
    }
    else if (offset ==0)
    {
        $("#pagedown").removeClass("disabled");        
    }
    else
    {
        $("#pagedown").removeClass("disabled");        
        $("#pageup").removeClass("disabled");        
    }
}