package com.ifountain.rcmdb.domain.method

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
    List propKeys;
    List relationKeys;
    Map relations
    public GetMethod(MetaClass mc, List keys, Map relations) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
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

    public boolean isWriteOperation() {
        return false;
    }

    protected Object _invoke(Class clazz, Object[] arguments) {
        def searchParams = arguments[0];
        def willTriggerOnLoad = true;
        if(arguments.size() == 2 && arguments[1] == false)
        {
            willTriggerOnLoad = false;
        }
        if(searchParams instanceof Map)
        {
            Map keyMap = [:];
            if(searchParams.containsKey("id"))
            {
                keyMap["id"] = searchParams["id"];
                def result = CompassMethodInvoker.search (mc, keyMap, willTriggerOnLoad)
                return result.results[0];
            }
            else
            {
                propKeys.each{
                    keyMap[it] = searchParams[it];
                }
                def result = null;
                if(propKeys.isEmpty() && relationKeys.isEmpty())
                {
                    return null;    
                }
                else if(keyMap.isEmpty())
                {
                    result = CompassMethodInvoker.search (mc, "id:[0 TO *]", willTriggerOnLoad);
                }
                else
                {
                    result = CompassMethodInvoker.search (mc, keyMap, willTriggerOnLoad)
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
                        relationKeys.each{relName->
                            if(searchParams[relName] == null || foundObject[relName] == null || searchParams[relName].id != foundObject[relName].id)
                            {
                                isMatched = false;
                                return;
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
            def result = CompassMethodInvoker.search (mc, searchParams, willTriggerOnLoad)
            return result;
        }
        else if(searchParams instanceof Number)
        {
            searchParams = "id:${searchParams}".toString();
            def result = CompassMethodInvoker.search (mc, searchParams, willTriggerOnLoad)
            return result.results[0];
        }
    }

}