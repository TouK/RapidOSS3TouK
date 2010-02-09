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
package com.ifountain.rcmdb.domain.method

import org.compass.core.Resource
import org.compass.core.CompassHit
import org.apache.commons.collections.MapUtils
import com.ifountain.compass.converter.CompassStringConverter
import com.ifountain.rcmdb.domain.statistics.OperationStatisticResult
import com.ifountain.rcmdb.domain.statistics.OperationStatistics

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 14, 2008
 * Time: 9:46:22 AM
 * To change this template use File | Settings | File Templates.
 */
class GetPropertyValuesMethod extends AbstractRapidDomainReadMethod {

    Map relations;
    public GetPropertyValuesMethod(MetaClass mcp, Map relations) {
        super(mcp); //To change body of overridden methods use File | Settings | File Templates.
        this.relations = relations;
    }

    protected Object _invoke(Object clazz, Object[] arguments) {
        OperationStatisticResult statistics = new OperationStatisticResult(model:clazz.name);
        statistics.start();

        String query = arguments[0]
        List propertyList = arguments[1]
        Map options = arguments[2];
        def results = [];
        def raw = {compassHits, session ->
            def hitIteratorClosure = {CompassHit hit ->
                Resource res = hit.getResource();
                def propMap = [alias: res.getAlias(), id: res.getObject("id")];
                results.add(propMap);
                propertyList.each {String propName ->
                    def prop = res.getProperty(propName);
                    if (prop != null)
                    {
                        Object value = prop.getObjectValue();
                        if (value == CompassStringConverter.EMPTY_VALUE)
                        {
                            value = "";
                        }
                        propMap[propName] = value;
                    }
                }
            }
            MethodUtils.getCompassHitsSubset(compassHits, options, hitIteratorClosure);
        }
        options["raw"] = raw;
        clazz.'searchEvery'(query, options);

        statistics.stop();
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.GET_PROPERTY_VALUES_OPERATION_NAME, statistics);
        OperationStatistics.getInstance().addStatisticResult (OperationStatistics.GET_PROPERTY_VALUES_OPERATION_NAME, statistics.getSubStatisticsWithObjectCount(results?.size()));
        return results; //To change body of implemented methods use File | Settings | File Templates.
    }

}