package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events

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
 * User: Administrator
 * Date: Apr 24, 2008
 * Time: 2:10:27 PM
 * To change this template use File | Settings | File Templates.
 */
class AsMapMethod extends AbstractRapidDomainMethod{
    def allProperties = [];
    public AsMapMethod(MetaClass mc, GrailsDomainClass domainClass) {
        super(mc, domainClass); //To change body of overridden methods use File | Settings | File Templates.
         def excludedProps = ['version',
                                     'id',
                                       Events.ONLOAD_EVENT,
                                       Events.BEFORE_DELETE_EVENT,
                                       Events.BEFORE_INSERT_EVENT,
                                       Events.BEFORE_UPDATE_EVENT]
        def props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
        for(prop in props){
            if(!prop.oneToMany && !prop.manyToMany && !prop.oneToOne && !prop.manyToOne){
                allProperties += prop.name;
            }
        }
    }

    public Object invoke(Object domainObject, Object[] arguments) {
        def propertyMap = [:];
        def colList;
        if(arguments.length == 0)
        {
            colList = allProperties;
        }
        else
        {
            colList = arguments[0];
        }
        for(prop in properties){
           try{
                propertyMap.put(prop, domainObject.getProperty(prop));
           }
           catch(e){
                logger.debug("An exception occurred while converting object to map while getting value of property ${prop}.", e);
           }
        }
        return propertyMap;
    }

}