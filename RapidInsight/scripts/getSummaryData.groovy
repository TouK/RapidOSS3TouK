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
 severitySummary.severity.each{propValue, numberOfObjects ->
     severityMap.put("" + propValue, numberOfObjects);
 }

web.render(contentType: 'text/xml'){
   Items(){
      Item(severity:"Critical", count:severityMap.get("5"))
      Item(severity:"Major", count:severityMap.get("4"))
      Item(severity:"Minor", count:severityMap.get("3"))
      Item(severity:"Warning", count:severityMap.get("2"))
      Item(severity:"Indeterminate", count:severityMap.get("1"))
      Item(severity:"Normal", count:severityMap.get("0"))
   }
}