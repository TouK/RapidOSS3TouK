/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 25, 2008
 * Time: 3:04:11 PM
 */
/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/

def CONTAINER_PROPERTY = "rsDatasource"
def nodeType = params.nodeType;
def name = params.name;

def severityMap = ["0":0, "1":0, "2":0, "3":0, "4":0, "5":0]
def severitySummary = null
if(nodeType == "Container"){
    severitySummary = RsEvent.propertySummary("${CONTAINER_PROPERTY}:\"${name}\"", ["severity"]);
}
else{
   severitySummary = RsEvent.propertySummary("elementName:\"${name}\"", ["severity"]);
}
def isAllZero = true;
 severitySummary.severity.each{propValue, numberOfObjects ->
     if(numberOfObjects > 0){
        isAllZero = false; 
     }
     severityMap.put("" + propValue, numberOfObjects);
 }
 if(isAllZero){
     severityMap.put("0", 1);
 }
web.render(contentType: 'text/xml'){
   chart(){
      set(label:"Critical", value:severityMap.get("5"), color:"0xff0000")
      set(label:"Major", value:severityMap.get("4"), color:"0xff7514")
      set(label:"Minor", value:severityMap.get("3"), color:"0xddc700")
      set(label:"Warning", value:severityMap.get("2"), color:"0x2dbfcd")
      set(label:"Indeterminate", value:severityMap.get("1"), color:"0xac6bac")
      set(label:"Normal", value:severityMap.get("0"), color:"0x00ff00")
   }
}