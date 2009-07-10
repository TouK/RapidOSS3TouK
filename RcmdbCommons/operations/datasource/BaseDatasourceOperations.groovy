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
package datasource

import com.ifountain.comp.converter.ConverterRegistry
import com.ifountain.core.datasource.BaseAdapter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 5:52:01 PM
 * To change this template use File | Settings | File Templates.
 */
class BaseDatasourceOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    public static Object convert(Object value)
    {
        return ConverterRegistry.getInstance().convert(value)
    }
    public static Object convertWithDefault(Object value,Object nullValue)
    {
        def result= ConverterRegistry.getInstance().convert(value);
        if(result == null)
        {
            result = nullValue;
        }
        return result;
    }
    static def getOnDemand(params) {
        def ds = BaseDatasource.get(params);
        if (ds) {
            ds.getAdapters().each{adapter->
                if(adapter instanceof BaseAdapter)
                {
                    adapter.setReconnectInterval(0);
                }
            }
        }
        return ds;
    }

    def getAdapters()
    {
        null;
    }

    def getProperty(Map keys, String propName)
    {
        return null;
    }

    def getProperties(Map keys, List properties)
    {
        return null;
    }
    
}