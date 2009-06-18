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

import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass

def rootClass = params.rootClass != null ? params.rootClass : "RsEvent"
def extraFilteredProps = ["rsDatasource", "id"];
def allProps = [];
GrailsDomainClass domainClass = web.grailsApplication.getDomainClass(rootClass)
if(domainClass == null){
    throw new Exception("Could not find class ${rootClass}");
}
domainClass.getSubClasses().each {
    allProps.addAll(DomainClassUtils.getFilteredProperties(it.name, extraFilteredProps));
}
allProps.addAll(DomainClassUtils.getFilteredProperties(rootClass, extraFilteredProps));
def sortedProps = allProps.sort {it.name}
def propertyMap = [:]
def writer = new StringWriter();
def builder = new MarkupBuilder(writer);
builder.Fields() {
    sortedProps.each {
        def propertyName = it.name;
        if (!propertyMap.containsKey(propertyName)) {
            builder.Field(Name: propertyName)
            propertyMap.put(propertyName, propertyName);
        }
    }
}
return writer.toString();