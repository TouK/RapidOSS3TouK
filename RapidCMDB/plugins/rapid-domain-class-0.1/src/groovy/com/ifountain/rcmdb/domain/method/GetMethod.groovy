package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.util.DatasourceConfigurationCache
import com.ifountain.rcmdb.domain.util.DomainClassUtils

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
 * Time: 2:31:28 PM
 * To change this template use File | Settings | File Templates.
 */
class GetMethod extends AbstractRapidDomainStaticMethod{

    def datasourceMetaData;
    def getMethodName;
    def getMethodParams;
    public GetMethod(MetaClass mc, GrailsDomainClass domainClass) {
        super(mc, domainClass); //To change body of overridden methods use File | Settings | File Templates.
        datasourceMetaData = new DatasourceConfigurationCache(domainClass)
        if(datasourceMetaData.masterName)
        {
            getMethodName = "findBy"
            def masterDsKeyMetaData = datasourceMetaData.datasources[datasourceMetaData.masterName].keys;
            int keyCount = 0;
            masterDsKeyMetaData.each{keyName, keyProps->
                if(keyCount == masterDsKeyMetaData.size() -1)
                {
                    getMethodName += DomainClassUtils.getUppercasedPropertyName(keyName);
                }
                else
                {
                    getMethodName += DomainClassUtils.getUppercasedPropertyName(keyName) + "And";
                }
                keyCount++;
                getMethodParams += keyName;
            }
        }
        else
        {
            getMethodName = "find";
        }
    }

    public Object invoke(Class clazz, Object[] arguments) {
        def searchParams = arguments[0];
        if(datasourceMetaData.masterName)
        {
            def params = [];
            getMethodParams.each{key->
                params += searchParams[key];
            }
            return mc.invokeStaticMethod(clazz, getMethodName, params as Object[])
        }
        else
        {
            def sampleBean = mc.getTheClass().newInstance();
            searchParams.each{key,value->
                sampleBean.setProperty (key, value);
            }
            return mc.invokeStaticMethod(clazz, getMethodName, sampleBean)
        }
    }

}