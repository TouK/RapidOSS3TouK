package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants
import org.apache.commons.beanutils.Converter

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
class GetMethod extends AbstractRapidDomainReadMethod{
    List propKeys;
    List relationKeys;
    Map relations
    public GetMethod(MetaClass mcp, List keys, Map relations) {
        super(mcp); //To change body of overridden methods use File | Settings | File Templates.
        propKeys = [];

        relationKeys = [];
        keys.each{
            if(relations.containsKey(it))
            {
                relationKeys += it;
            }
            else
            {
                propKeys += it;
            }
        }
        this.relations = relations;
    }

    protected Object _invoke(Object clazz, Object[] arguments) {


        def searchParams = arguments[0];
        def willTriggerOnLoad = true;
        if(arguments.size() == 2 && arguments[1] == false)
        {
            willTriggerOnLoad = false;
        }
        if(searchParams instanceof Map)
        {
            def tempSearchParamMap  = new HashMap();
            searchParams.each{key, value->
                tempSearchParamMap[key.toString()] = value;                
            }
            searchParams = tempSearchParamMap;
            Map keyMap = [:];
            if(searchParams.containsKey(RapidCMDBConstants.ID_PROPERTY_STRING))
            {
                def idValue = searchParams[RapidCMDBConstants.ID_PROPERTY_STRING];
                keyMap["id"] = idValue;
                def result = CompassMethodInvoker.search (clazz.metaClass, keyMap, willTriggerOnLoad)
                return result.results[0];
            }
            else
            {
                def stringConverter = RapidConvertUtils.getInstance().lookup(String.class);
                propKeys.each{
                    keyMap[it] = stringConverter.convert(String, searchParams[it]);
                }
                def result = null;
                if(propKeys.isEmpty() && relationKeys.isEmpty())
                {
                    return null;    
                }
                else if(keyMap.isEmpty())
                {
                    result = CompassMethodInvoker.search (clazz.metaClass, "alias:*", willTriggerOnLoad);
                }
                else
                {
                    result = CompassMethodInvoker.search (clazz.metaClass, keyMap, willTriggerOnLoad)
                }
                if(relationKeys.isEmpty())
                {
                    return result.results[0];
                }
                else
                {
                    def res = null;
                    result.results.each{foundObject->
                        boolean isMatched = true;
                        for(int i=0; i < relationKeys.size(); i++){
                            def relName = relationKeys[i];
                            if(searchParams[relName] == null || foundObject[relName] == null || searchParams[relName].id != foundObject[relName].id)
                            {
                                isMatched = false;
                                break;
                            }
                        }
                        if(isMatched)
                        {
                            res = foundObject;
                            return;
                        }
                    }
                    return res;
                }
            }
        }
        else if(searchParams instanceof String || searchParams  instanceof GString)
        {
            searchParams = searchParams.toString();
            def result = CompassMethodInvoker.search (clazz.metaClass, searchParams, willTriggerOnLoad)
            return result;
        }
        else if(searchParams instanceof Number)
        {
            Map keyMap = [id:searchParams];
            def result = CompassMethodInvoker.search (clazz.metaClass, keyMap, willTriggerOnLoad)
            return result.results[0];
        }
    }

}