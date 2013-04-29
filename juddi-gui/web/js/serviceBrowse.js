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

var offset=0; //start at the begining
var maxrecords=20;  //record 20 at a time
var totalrecords=0;

function refreshServiceList()
{
    RenderServiceListBySearch('%', offset, maxrecords);    
}
RenderServiceListBySearch('%', offset, maxrecords);
//offset += maxrecords;
function pagedownService()
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
function pageupService()
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
        $("#serviceBrowserListings").html("An error occured! " + jqXHR.responseText + textStatus);
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