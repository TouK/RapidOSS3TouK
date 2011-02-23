package message

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import auth.RsUser
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.auth.UserConfigurationSpace
import auth.RsUserOperations
import auth.Role
import auth.Group
import auth.GroupOperations
import com.ifountain.rcmdb.auth.SegmentQueryHelper

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Mar 16, 2010
* Time: 4:41:45 PM
*/
class RsMessageRuleCalendarTests extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        initialize([RsUser, RsMessageRuleCalendar, Role, Group, RsMessageRule], []);
        CompassForTests.addOperationSupport(RsMessageRuleCalendar, RsMessageRuleCalendarOperations);
        CompassForTests.addOperationSupport(RsUser, RsUserOperations);
        CompassForTests.addOperationSupport(Group, GroupOperations);
        UserConfigurationSpace.getInstance().initialize();
        SegmentQueryHelper.getInstance().initialize([]);
        def adminRole = Role.add(name: Role.ADMINISTRATOR);
        def adminGroup = Group.addGroup(name: RsUser.RSADMIN, role: adminRole);
        RsUser.addUser(username: RsUser.RSADMIN, password: "changeme", groups: [adminGroup])
    }

    public void tearDown() {
        super.tearDown();
    }

    public void testAddCalendar() throws Exception {

        def props = [name: "cal1", days: "1,3,5", exceptions: "3/5/2010,4/5/2010", username: RsUser.RSADMIN,
                startingHour: '9', startingMinute: '0', endingHour: '18', endingMinute: '59']
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertFalse(cal.hasErrors());

        assertEquals(props.name, cal.name);
        assertEquals(false, cal.isPublic)
        assertEquals(RsUser.RSADMIN, cal.username)
        assertEquals(props.days, cal.days)
        assertEquals("Sun,Tue,Thu", cal.daysString)
        assertEquals(props.exceptions, cal.exceptions)

        Calendar cal1 = new GregorianCalendar()
        cal1.setTime(new Date(0));
        cal1.set(Calendar.HOUR_OF_DAY, props.startingHour.toInteger())
        cal1.set(Calendar.MINUTE, props.startingMinute.toInteger())

        assertEquals(cal1.getTime().getTime(), cal.starting);

        cal1 = new GregorianCalendar()
        cal1.setTime(new Date(0));
        cal1.set(Calendar.HOUR_OF_DAY, props.endingHour.toInteger())
        cal1.set(Calendar.MINUTE, props.endingMinute.toInteger())

        assertEquals(cal1.getTime().getTime(), cal.ending);
    }

    public void testAddCalendarThrowsExceptionIfUserDoesNotExist() {
        def props = [name: "cal1", days: "1,3,5", exceptions: "3/5/2010,4/5/2010", username: "nonExistingUser",
                startingHour: '9', startingMinute: '0', endingHour: '18', endingMinute: '59']

        try {
            RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("No user defined with username 'nonExistingUser'", e.getMessage());
        }
    }
    public void testAddPublicCalendar() throws Exception {
        def props = [name: "cal1", days: "1,3,5", exceptions: "3/5/2010,4/5/2010", username: "someUser",
                startingHour: '9', startingMinute: '0', endingHour: '18', endingMinute: '59', isPublic: true]
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertFalse(cal.hasErrors());

        assertEquals(props.name, cal.name);
        assertEquals(true, cal.isPublic)
        assertEquals(RsUser.RSADMIN, cal.username)
    }
    public void testAddCalendarThrowsExceptionIfDaysIsNotGiven() {
        def props = [name: "cal1", days: "", exceptions: "3/5/2010,4/5/2010", username: RsUser.RSADMIN,
                startingHour: '9', startingMinute: '0', endingHour: '18', endingMinute: '59']
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertTrue(cal.hasErrors());
        def error = cal.errors.allErrors[0]
        assertEquals('blank', error.code)
    }

    public void testAddCalendarPopulatesErrorsIfEndingNotBiggerThanStarting() {
        def props = [name: "cal1", days: "1", exceptions: "3/5/2010,4/5/2010", username: RsUser.RSADMIN,
                startingHour: '10', startingMinute: '0', endingHour: '9', endingMinute: '59']
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertTrue(cal.hasErrors());
        def error = cal.errors.allErrors[0]
        assertEquals('default.not.greater.than', error.code)
    }

    public void testAddCalendarPopulatesErrorsIfExceptionsNotFormated() {
        def props = [name: "cal1", days: "1", exceptions: "asdf", username: RsUser.RSADMIN,
                startingHour: '0', startingMinute: '0', endingHour: '9', endingMinute: '59']
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertTrue(cal.hasErrors());
        def error = cal.errors.allErrors[0]
        assertEquals('default.doesnt.match.message', error.code)
    }

    public void testUpdateCalendar() throws Exception {

        def props = [name: "cal1", days: "1,3,5", exceptions: "3/5/2010,4/5/2010", username: RsUser.RSADMIN,
                startingHour: '9', startingMinute: '0', endingHour: '18', endingMinute: '59']
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertFalse(cal.hasErrors());

        assertEquals(props.name, cal.name);
        assertEquals(false, cal.isPublic)
        assertEquals(RsUser.RSADMIN, cal.username)
        assertEquals(props.days, cal.days)
        assertEquals("Sun,Tue,Thu", cal.daysString)
        assertEquals(props.exceptions, cal.exceptions)

        Calendar cal1 = new GregorianCalendar()
        cal1.setTime(new Date(0));
        cal1.set(Calendar.HOUR_OF_DAY, props.startingHour.toInteger())
        cal1.set(Calendar.MINUTE, props.startingMinute.toInteger())

        assertEquals(cal1.getTime().getTime(), cal.starting);

        cal1 = new GregorianCalendar()
        cal1.setTime(new Date(0));
        cal1.set(Calendar.HOUR_OF_DAY, props.endingHour.toInteger())
        cal1.set(Calendar.MINUTE, props.endingMinute.toInteger())

        assertEquals(cal1.getTime().getTime(), cal.ending);

        def updateProps = [name: "cal2", days: "2,4,6", exceptions: "", startingHour: '8', startingMinute: '0']
        cal = RsMessageRuleCalendar.updateCalendar(cal, updateProps)
        assertFalse(cal.hasErrors());

        assertEquals(updateProps.name, cal.name);
        assertEquals(false, cal.isPublic)
        assertEquals(RsUser.RSADMIN, cal.username)
        assertEquals(updateProps.days, cal.days)
        assertEquals("Mon,Wed,Fri", cal.daysString)
        assertEquals(updateProps.exceptions, cal.exceptions)

        cal1 = new GregorianCalendar()
        cal1.setTime(new Date(0));
        cal1.set(Calendar.HOUR_OF_DAY, updateProps.startingHour.toInteger())
        cal1.set(Calendar.MINUTE, updateProps.startingMinute.toInteger())

        assertEquals(cal1.getTime().getTime(), cal.starting);

        cal1 = new GregorianCalendar()
        cal1.setTime(new Date(0));
        cal1.set(Calendar.HOUR_OF_DAY, props.endingHour.toInteger())
        cal1.set(Calendar.MINUTE, props.endingMinute.toInteger())

        assertEquals(cal1.getTime().getTime(), cal.ending);
    }

    public void testUpdateCalendarThrowsExceptionIfUserDoesNotExist() {
        def props = [name: "cal1", days: "1,3,5", exceptions: "3/5/2010,4/5/2010", username: RsUser.RSADMIN,
                startingHour: '9', startingMinute: '0', endingHour: '18', endingMinute: '59']
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertFalse(cal.hasErrors());

        def updateProps = [username: "nonExistingUser"]

        try {
            RsMessageRuleCalendar.updateCalendar(cal, updateProps)
            fail("should throw exception");
        }
        catch (e) {
            assertEquals("No user defined with username 'nonExistingUser'", e.getMessage());
        }
    }
    public void testUpdatingACalenderAndSettingAsPublic() throws Exception {
        def props = [name: "cal1", days: "1,3,5", exceptions: "3/5/2010,4/5/2010", username: RsUser.RSADMIN,
                startingHour: '9', startingMinute: '0', endingHour: '18', endingMinute: '59']
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertFalse(cal.hasErrors());
        assertEquals(false, cal.isPublic)

        def updateProps = [isPublic: true]
        cal = RsMessageRuleCalendar.updateCalendar(cal, updateProps)
        assertFalse(cal.hasErrors());

        assertEquals(true, cal.isPublic)
        assertEquals(RsUser.RSADMIN, cal.username)
    }

    public void testUpdateCalendarPopulatesErrorsIfEndingNotBiggerThanStarting() {
        def props = [name: "cal1", days: "1", exceptions: "3/5/2010,4/5/2010", username: RsUser.RSADMIN,
                startingHour: '0', startingMinute: '0', endingHour: '9', endingMinute: '59']
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertFalse(cal.hasErrors());
        def updateProps = [startingHour: '10', startingMinute: '0', endingHour: '9', endingMinute: '59']

        cal = RsMessageRuleCalendar.updateCalendar(cal, updateProps)
        assertTrue(cal.hasErrors());
        def error = cal.errors.allErrors[0]
        assertEquals('default.not.greater.than', error.code)
    }

    public void testRelationWithRsMessageRuleIsDeletedAfterDelete() {
        def props = [name: "cal1", days: "1,3,5", exceptions: "3/5/2010,4/5/2010", username: RsUser.RSADMIN,
                startingHour: '9', startingMinute: '0', endingHour: '18', endingMinute: '59']
        RsMessageRuleCalendar cal = RsMessageRuleCalendar.addCalendar(props)
        assertFalse(cal.hasErrors());

        def messageRule = RsMessageRule.add(searchQueryId: 1, users: "testuser", destinationType: "email", calendarId: cal.id)
        assertFalse(messageRule.hasErrors())
        assertEquals(cal.id, messageRule.calendarId);

        cal.remove();

        messageRule = RsMessageRule.get(searchQueryId: 1, users: "testuser", destinationType: "email", groups:"_");
        assertEquals(0, messageRule.calendarId);

    }

}