package search

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 16, 2010
* Time: 5:07:51 PM
*/
class SearchQueryOperationsTests extends RapidCmdbWithCompassTestCase {
    public void setUp() {
        super.setUp();
        initialize([SearchQuery, SearchQueryGroup], []);
        CompassForTests.addOperationSupport(SearchQuery, SearchQueryOperations);
    }

    public void testBeforeDeleteGeneratesExceptionIfQueryHaveSubQueries()
    {
        def group = SearchQueryGroup.add(name: "testgroup", username: "user1", type: "fixtype");
        assertFalse(group.hasErrors())

        def query1 = SearchQuery.add(name: "testquery1", username: "user1", group: group, query: "abc", type: "test");
        assertFalse(query1.hasErrors());
        def query2 = SearchQuery.add(name: "testquery2", username: "user1", group: group, query: "abc", type: "test", parentQueryId: query1.id);
        assertFalse(query2.hasErrors());

        try {
            query1.remove();
            fail("Should throw exception")
        }
        catch (e) {}

        query2.update(parentQueryId: 0);

        try {
            query1.remove();
        }
        catch (e)
        {
            fail("Should not throw exception")
        }
        assertNull(SearchQuery.get(name: query1.name, username: query1.username, type: query1.type))

    }
    
}