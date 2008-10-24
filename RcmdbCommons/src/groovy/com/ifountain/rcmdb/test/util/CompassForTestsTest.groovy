package com.ifountain.rcmdb.test.util
import datasource.*;
import script.*;
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 23, 2008
 * Time: 3:54:54 PM
 * To change this template use File | Settings | File Templates.
 */
class CompassForTestsTest extends RapidCmdbTestCase{
    def test1()
    {
        CompassForTests.initialize ([datasource.BaseListeningDatasource]);
        assertNull (datasource.BaseListeningDatasource.add(prop1:"asdasdasd"));
        CompassForTests.setAddObjects([new BaseListeningDatasource(id:0)]);
        assertEquals (0, datasource.BaseListeningDatasource.add(prop1:"asdasdasd").id);
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
        CmdbScript.startListening("asd");
    }

}