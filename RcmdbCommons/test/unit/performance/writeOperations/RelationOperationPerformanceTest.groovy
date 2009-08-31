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
package performance.writeOperations

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.domain.method.RelationMethodDomainObject1
import com.ifountain.rcmdb.domain.method.RelationMethodDomainObject2
import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 3, 2008
 * Time: 3:58:23 PM
 * To change this template use File | Settings | File Templates.
 */
class RelationOperationPerformanceTest extends RapidCmdbWithCompassTestCase
{
    //Time1:0.86694538 Time2:0.280884582 Time3:82.982436315  Time4:2.505567048
    //Time1:1.015306415 Time2:0.332571039 Time3:5.226030403  Time4:5.907035671
    public void testPerformance()
    {
        String domainClassName1 = "DomainClass1"
        String domainClassName2 = "DomainClass2"
        def loadedDomainClass1 = gcl.parseClass("""
            class ${domainClassName1}{
                Object ${RapidCMDBConstants.OPERATION_PROPERTY_NAME}
                static searchable = {
                    except=["rel1"]
                }
                Long id;
                Long version;
                Date rsInsertedAt = new Date(0);
                Date rsUpdatedAt  = new Date(0);
                String prop1;
                List rel1 = [];
                static relations = [rel1:[type:${domainClassName2}, isMany:true, reverseName:"revRel1"]]
            }
            class ${domainClassName2}{
                Object ${RapidCMDBConstants.OPERATION_PROPERTY_NAME}
                static searchable = {
                    except=["revRel1"]
                }
                Long id;
                Long version;
                Date rsInsertedAt = new Date(0);
                Date rsUpdatedAt  = new Date(0);
                String prop1;
                List revRel1 = [];
                static relations = [revRel1:[type:${domainClassName1}, isMany:true, reverseName:"rel1"]]
            }
        """)
        Class loadedDomainClass2 = gcl.loadClass(domainClassName2);
        initialize([loadedDomainClass1, loadedDomainClass2, relation.Relation], [])
        def t = System.nanoTime();
        for(int i=0; i < 100; i++)
        {
            loadedDomainClass1.metaClass.invokeStaticMethod(loadedDomainClass1, "add", [[prop1:"prop1Val"]] as Object[]).errors;
        }

        def time1 = System.nanoTime() - t;

        def rel2 = [];
        t = System.nanoTime();
        for(int i=0; i < 100; i++)
        {
            def rs = loadedDomainClass2.metaClass.invokeStaticMethod(loadedDomainClass2, "add", [[prop1:"prop1Val"]] as Object[]);
            rel2.add(rs);
        }

        def time2 = System.nanoTime() - t;

        t = System.nanoTime();
        for(int i=0; i < 100; i++)
        {
            loadedDomainClass1.metaClass.invokeStaticMethod(loadedDomainClass1, "add", [[prop1:"prop1Val", rel1:rel2]] as Object[]);
        }

        def time3 = System.nanoTime() - t;
        t = System.nanoTime();
        def correctnumberOfRelations = 0;
        loadedDomainClass1.metaClass.invokeStaticMethod(loadedDomainClass1, "list", [] as Object[]).each{
            if(it.rel1.size() == 100)
            {
            correctnumberOfRelations++;
            }
        }
        assertEquals (100, correctnumberOfRelations)
        def time4 = System.nanoTime() - t;

        println "Time1:${time1/Math.pow(10,9)} Time2:${time2/Math.pow(10,9)} Time3:${time3/Math.pow(10,9)}  Time4:${time4/Math.pow(10,9)}"
    }
}