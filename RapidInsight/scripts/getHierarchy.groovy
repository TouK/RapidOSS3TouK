import groovy.xml.MarkupBuilder

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
def CONTAINER_PROPERTY = "rsDatasource";
def containerMap = [:]

def summaryMap = RsComputerSystem.propertySummary("alias:*", [CONTAINER_PROPERTY]);
def containers = summaryMap[CONTAINER_PROPERTY].keySet();
def sw = new StringWriter();
def builder = new MarkupBuilder(sw);

def searchParams = [max: "1000",sort:"name",order:"asc"];
def searchProps = ["name","displayName",CONTAINER_PROPERTY];

builder.Objects() {
    containers.each {containerName ->
        builder.Object(id: containerName, name: containerName, displayName: containerName, nodeType: 'Container') {
            //getPropertyValues is faster than search for limited number of properties
            def results = RsComputerSystem.getPropertyValues("${CONTAINER_PROPERTY}:${containerName.exactQuery()}",searchProps,searchParams);
            results.each {topoObj ->
                builder.Object(id: topoObj.id, name: topoObj.name, displayName: topoObj.displayName, nodeType: 'Object',
                        "${CONTAINER_PROPERTY}": topoObj[CONTAINER_PROPERTY])
            }
        }
    }
}
return sw.toString(); 