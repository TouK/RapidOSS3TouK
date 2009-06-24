package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor
import org.apache.log4j.Logger

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
    Logger logger;
    public AsMapMethod(MetaClass mc, Class domainClass, Logger logger) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
        this.logger = logger;
        def props = domainClass.'getPropertiesList'();
        for(prop in props){
            if(!prop.isRelation && !prop.isOperationProperty){
                allProperties += prop.name;
            }
        }
    }


    public Object invoke(Object domainObject, Object[] arguments) {
        def propertyMap = [:];
        def colList;
        if(arguments == null || arguments.length == 0)
        {
            colList = allProperties;
        }
        else
        {
            colList = arguments[0];
        }
        for(prop in colList){
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