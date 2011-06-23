/*
 * Copyright 2001-2009 The Apache Software Foundation.
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

var clear="clear.gif" //path to clear.gif

pngfix=function(){
	var els=document.getElementsByTagName('*');
	var ip=/\.png/i;
	var i=els.length;
	while(i-- >0){
		var el=els[i];
		var es=el.style;
		if(el.src&&el.src.match(ip)&&!es.filter){
			es.height=el.height;
			es.width=el.width;
			es.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+el.src+"',sizingMethod='crop')";
			el.src=clear;
		}
		else{
			var elb=el.currentStyle.backgroundImage;
			if(elb.match(ip)){
				var path=elb.split('"');
				var rep=(el.currentStyle.backgroundRepeat=='no-repeat')?'crop':'scale';
				es.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+path[1]+"',sizingMethod='"+rep+"')";
				es.height=el.clientHeight+'px';
				es.backgroundImage='none';
				var elkids=el.getElementsByTagName('*');
				if (elkids){
					var j=elkids.length;
					if(el.currentStyle.position!="absolute")es.position='static';
					while (j-- >0)
					if(!elkids[j].style.position)elkids[j].style.position="relative";
				}
			}
		}
	}
}
window.attachEvent('onload',pngfix);
