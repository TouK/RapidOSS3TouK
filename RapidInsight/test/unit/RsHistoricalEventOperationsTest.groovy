import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import java.text.SimpleDateFormat

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Sep 11, 2009
* Time: 2:47:30 PM
* To change this template use File | Settings | File Templates.
*/
class RsHistoricalEventOperationsTest extends RapidCmdbWithCompassTestCase {
    public void setUp() {
        super.setUp();

        initialize([RsHistoricalEvent], []);
        CompassForTests.addOperationSupport(RsHistoricalEvent, RsHistoricalEventOperations);
    }

    public void tearDown() {

        super.tearDown();
    }

    public void testBeforeInsertCalculations()
    {
        def createdAt = new Date().getTime();
        def clearedAt = createdAt + 200000;
        def historicalEvent = RsHistoricalEvent.add(name: "event1", createdAt: createdAt, clearedAt: clearedAt);
        assertFalse(historicalEvent.hasErrors());
        RsHistoricalEvent historicalEventFromrepository = RsHistoricalEvent.get(id: historicalEvent.id);
        assertEquals(clearedAt - createdAt, historicalEventFromrepository.duration);

        RsHistoricalEventOperations.formatters.each {propName, SimpleDateFormat formatter ->
            assertEquals(historicalEventFromrepository[propName], formatter.format(new Date(createdAt)));
            assertEquals(historicalEvent[propName], formatter.format(new Date(createdAt)));
        }
    }

    public void testCalculateDuration()
    {
        def createdAt = new Date().getTime();
        def clearedAt = createdAt + 200000;
        def historicalEvent = RsHistoricalEvent.add(name: "event1", createdAt: createdAt, clearedAt: clearedAt);
        assertFalse(historicalEvent.hasErrors());

        assertEquals(clearedAt - createdAt, historicalEvent.calculateDuration());
    }

}