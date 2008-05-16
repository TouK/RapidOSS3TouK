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
    List keys;
    public GetMethod(MetaClass mc, List keys) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
        this.keys = keys;
    }

    public Object invoke(Class clazz, Object[] arguments) {
        def searchParams = arguments[0];
        if(searchParams instanceof Map)
        {
            Map keyMap = [:];
            keys.each{
                keyMap[it] = searchParams[it];
            }
            def result = CompassMethodInvoker.search (mc, keyMap)
            return result.results[0];
        }
        else if(searchParams instanceof String || searchParams  instanceof GString)
        {
            searchParams = searchParams.toString();
            def result = CompassMethodInvoker.search (mc, searchParams)
            return result;
        }
    }

}