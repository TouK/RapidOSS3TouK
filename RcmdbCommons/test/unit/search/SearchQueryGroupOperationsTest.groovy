package search

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 24, 2009
* Time: 2:49:26 PM
* To change this template use File | Settings | File Templates.
*/
class SearchQueryGroupOperationsTest  extends RapidCmdbWithCompassTestCase{
    public void setUp() {
        super.setUp();
    }

    public void tearDown() {
        super.tearDown();
    }

    public void testBeforeDeleteGeneratesExceptionIfGroupHaveQueries()
    {
        initialize([SearchQuery, SearchQueryGroup], []);
        CompassForTests.addOperationSupport (SearchQueryGroup,SearchQueryGroupOperations);

        assertEquals(0,SearchQuery.count());
        assertEquals(0,SearchQueryGroup.count());

        def group=SearchQueryGroup.add(name:"testgroup",username:"user1",type:"fixtype");
        assertFalse(group.hasErrors())

        def query1=SearchQuery.add(name:"testquery1",username:"user1",group:group,query:"abc", type:"test");
        assertFalse(query1.hasErrors());
        def query2=SearchQuery.add(name:"testquery2",username:"user1",group:group,query:"abc", type:"test");
        assertFalse(query2.hasErrors());

        assertEquals(2,SearchQuery.count());
        assertEquals(1,SearchQueryGroup.count());



        group=SearchQueryGroup.list()[0];
        assertEquals(2,group.queries.size());

        try {
            group.remove();
            fail("Should throw exception")
        }
        catch(e)
        {
             println e;
        }

        group.removeRelation([queries:query1]);
        group=SearchQueryGroup.list()[0];
        assertEquals(1,group.queries.size());

        try {
            group.remove();
            fail("Should throw exception")
        }
        catch(e)
        {
             println e;
        }

        group.removeRelation([queries:query2]);
        group=SearchQueryGroup.list()[0];
        assertEquals(0,group.queries.size());

         try {
            group.remove();

        }
        catch(e)
        {
             println e;
             fail("Should not throw exception")
        }

        assertEquals(0,SearchQueryGroup.count());
    }
}