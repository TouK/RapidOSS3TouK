import com.ifountain.smarts.datasource.BaseSmartsListeningAdapter

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 6, 2008
 * Time: 3:46:38 PM
 */

def getParameters(){
   return [
           "subscribeParameters":[
               ["CreationClassName":"UnitaryComputerSystem", "Name":".*", "Attributes":["CreationClassName", "Name", "DiscoveredLastAt"]],
               ["CreationClassName":"Interface", "Name":".*", "Attributes":["CreationClassName", "Name"]],
               ["CreationClassName":"Card", "Name":".*", "Attributes":["CreationClassName", "Name"]],
               ["CreationClassName":"IP", "Name":".*", "Attributes":["CreationClassName", "Name"]]
               ]
           ]
}

def init(){

}

def cleanUp(){

}

def update(topologyObject){
    def eventType = topologyObject["ICEventType"];
    def creationClassName = topologyObject["CreationClassName"];
    if(eventType == "CREATE" && !(creationClassName == "Interface" || creationClassName == "Card" || creationClassName == "IP")){
           println "device create: ${topologyObject}";
    }
    else if(eventType == "CHANGE" && !(creationClassName == "Interface" || creationClassName == "Card" || creationClassName == "IP")){
           println "device change: ${topologyObject}";
    }
    else if(eventType == "DELETE"){
        println "delete: ${topologyObject}";        
    }
}