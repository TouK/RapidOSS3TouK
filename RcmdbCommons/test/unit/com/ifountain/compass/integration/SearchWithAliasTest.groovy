package com.ifountain.compass.integration

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.compass.utils.QueryParserUtils


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 21, 2009
* Time: 8:17:14 PM
* To change this template use File | Settings | File Templates.
*/
class SearchWithAliasTest extends RapidCmdbWithCompassTestCase {
    static def classes = [:];
    public void setUp() {
        super.setUp();
        gcl.addClasspath(getWorkspaceDirectory().path+"/RapidModules/RapidInsight/grails-app/domain");
        classes.RsTopologyObject=gcl.loadClass("RsTopologyObject")
        classes.RsGroup=gcl.loadClass("RsGroup")
        classes.RsCustomer=gcl.loadClass("RsCustomer")
        initialize([classes.RsTopologyObject,classes.RsGroup,classes.RsCustomer], []);
    }

    public void tearDown() {
        super.tearDown();
    }




    public static void testSearchWithAlias()
    {

        def object2=classes.RsTopologyObject.add(name: "ev1")
        assertFalse(object2.hasErrors());


        def object = classes.RsGroup.add(name: "testobject");
        assertFalse(object.hasErrors());

        def customer=classes.RsCustomer.add(name:"cust");
        assertFalse(customer.hasErrors());

        assertEquals(3,classes.RsTopologyObject.countHits("alias:*"));
        assertEquals(3,classes.RsTopologyObject.countHits("alias:RsTopologyObject"));
        assertEquals(2,classes.RsTopologyObject.countHits("alias:RsGroup"));
        assertEquals(1,classes.RsTopologyObject.countHits("alias:RsCustomer"));

        assertEquals(3,classes.RsTopologyObject.countHits("alias:RsTopologyObject AND (name:* OR id:*)"));
        assertEquals(2,classes.RsTopologyObject.countHits("alias:RsGroup AND (name:* OR id:*)"));
        assertEquals(1,classes.RsTopologyObject.countHits("alias:RsCustomer AND (name:* OR id:*)"));

        assertEquals(2,classes.RsGroup.countHits("alias:*"));
        assertEquals(2,classes.RsGroup.countHits("alias:RsTopologyObject"));
        assertEquals(2,classes.RsGroup.countHits("alias:RsGroup"));
        assertEquals(1,classes.RsGroup.countHits("alias:RsCustomer"));

        assertEquals(1,classes.RsCustomer.countHits("alias:*"));
        assertEquals(1,classes.RsCustomer.countHits("alias:RsTopologyObject"));
        assertEquals(1,classes.RsCustomer.countHits("alias:RsGroup"));
        assertEquals(1,classes.RsCustomer.countHits("alias:RsCustomer"));


        assertEquals(1,classes.RsTopologyObject.countHits("alias:${QueryParserUtils.toExactQuery ("RsTopologyObject")}"));
        assertEquals(1,classes.RsTopologyObject.countHits("alias:${QueryParserUtils.toExactQuery ("RsGroup")}"));
        assertEquals(1,classes.RsTopologyObject.countHits("alias:${QueryParserUtils.toExactQuery ("RsCustomer")}"));

        assertEquals(1,classes.RsTopologyObject.countHits("alias:${QueryParserUtils.toExactQuery ("RsTopologyObject")} AND name:*"));
        assertEquals(1,classes.RsTopologyObject.countHits("alias:${QueryParserUtils.toExactQuery ("RsGroup")}  AND name:*"));
        assertEquals(1,classes.RsTopologyObject.countHits("alias:${QueryParserUtils.toExactQuery ("RsCustomer")} AND name:*"));

        assertEquals(1,classes.RsTopologyObject.countHits("alias:${QueryParserUtils.toExactQuery ("RsTopologyObject")} AND (name:* OR id:*)"));
        assertEquals(1,classes.RsTopologyObject.countHits("alias:${QueryParserUtils.toExactQuery ("RsGroup")}  AND (name:* OR id:*)"));
        assertEquals(1,classes.RsTopologyObject.countHits("alias:${QueryParserUtils.toExactQuery ("RsCustomer")} AND (name:* OR id:*)"));
    }



}
