package com.ifountain.compass

import org.codehaus.groovy.grails.plugins.searchable.compass.test.AbstractSearchableCompassTests
import org.codehaus.groovy.grails.plugins.searchable.test.compass.TestCompassFactory
import org.compass.core.CompassQuery
import org.compass.core.CompassQueryFilter
import org.apache.commons.io.FileUtils
import org.compass.core.lucene.engine.transaction.readcommitted.BitSetByAliasFilter

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 27, 2008
* Time: 6:03:44 AM
* To change this template use File | Settings | File Templates.
*/
class SimpleCompassMangerBitSetException extends AbstractSearchableCompassTests{
    def compass

    void setUp() {
        FileUtils.deleteDirectory (new File("../testIndex"));
    }

    protected void tearDown() {
        compass.close();
    }
    public void testWorkaroundForAllBitSetException()
    {
        compass = TestCompassFactory.getPersistedCompass([CompassTestObject], [])
        int batchSize = 2;
        long maxWaitTime = 0;
        SingleCompassSessionManager.initialize(compass, batchSize, maxWaitTime)


        for(int i=0; i <200; i++)
        {
            def tx = SingleCompassSessionManager.beginTransaction()
            def obj = new CompassTestObject(id:0, prop1:"prop1val");
            tx.getSession().save(obj);
            tx.commit()
            while(true)
            {
                tx = SingleCompassSessionManager.beginTransaction()
                try {
                    CompassQuery q = tx.getSession().queryBuilder().queryString ("id:0").toQuery();
                    CompassQueryFilter instanceFilter = tx.getSession().queryFilterBuilder().query(q);
                    CompassQuery q2 = tx.getSession().queryBuilder().queryString ("prop1:prop1val").toQuery();
                    q2.setFilter (instanceFilter);
                    println q2.hits().length()
                    break;
                }
                catch(java.lang.UnsupportedOperationException op)
                {
                    if(op.getStackTrace()[0].getClassName().equals(BitSetByAliasFilter.AllSetBitSet.class.name))
                    {
                        println "deneme"
                    }
                    else
                    {
                        throw op;
                    }
                }
                finally {
                    println "HEYHEY"
                    tx.commit()
                }
            }
        }

    }

}