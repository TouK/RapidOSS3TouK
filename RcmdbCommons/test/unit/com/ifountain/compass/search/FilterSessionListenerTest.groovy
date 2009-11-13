package com.ifountain.compass.search

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.session.Session
import auth.RsUser
import auth.Group
import com.ifountain.session.SessionManager
import auth.SegmentFilter
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import com.ifountain.rcmdb.auth.UserConfigurationSpace
import com.ifountain.rcmdb.test.util.CompassForTests
import auth.RsUserOperations
import auth.GroupOperations
import auth.Role
import auth.SegmentFilterOperations

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 15, 2009
* Time: 4:49:53 PM
* To change this template use File | Settings | File Templates.
*/
class FilterSessionListenerTest extends RapidCmdbWithCompassTestCase{
    public void testFilterManager()
    {
        initialize ([RsUser, Group, SegmentFilter, Role],[]);
        CompassForTests.addOperationSupport(RsUser, RsUserOperations)
        CompassForTests.addOperationSupport(Group, GroupOperations)
        CompassForTests.addOperationSupport(SegmentFilter, SegmentFilterOperations)
        SegmentQueryHelper.getInstance().initialize([Object.class]);
        UserConfigurationSpace.getInstance().initialize();
        Role userRole = Role.add(name:Role.USER);
        Group gr1 = Group.addGroup(name:"gr1", segmentFilter:"name:script1", segmentFilterType:Group.GLOBAL_FILTER, role:userRole);
        Group gr2 = Group.addGroup(name:"gr2", segmentFilterType:Group.GLOBAL_FILTER, role:userRole);
        Group gr3 = Group.addGroup(name:"gr3", segmentFilterType:Group.CLASS_BASED_FILTER, role:userRole);
        Group gr4 = Group.addGroup(name:"gr4", segmentFilterType:Group.CLASS_BASED_FILTER, role:userRole);
        assertFalse (gr1.hasErrors());
        assertFalse (gr2.hasErrors());
        assertFalse (gr3.hasErrors());
        assertFalse (gr4.hasErrors());

        RsUser user1 = RsUser.addUser(username:"user1", password:"password", groups:[gr1]);
        RsUser user3 = RsUser.addUser(username:"user3", password:"password", groups:[gr2]);
        RsUser user4 = RsUser.addUser(username:"user4", password:"password", groups:[gr1, gr3]);
        RsUser user5 = RsUser.addUser(username:"user5", password:"password", groups:[gr4, gr3]);
        assertFalse (user1.hasErrors());
        assertFalse (user3.hasErrors());
        assertFalse (user4.hasErrors());
        assertFalse (user5.hasErrors());


        def segmentFilter1 = "name:a*"
        def segmentFilter2 = "name:b*"
        SegmentFilter filter1 = SegmentFilter.add(className:Object.class.name, filter:segmentFilter1, group:[gr3], groupId:gr3.id)
        SegmentFilter filter2 = SegmentFilter.add(className:Object.class.name, filter:segmentFilter2, group:[gr4], groupId:gr4.id)

        assertFalse (filter1.hasErrors());
        assertFalse (filter2.hasErrors());

        Session session = new Session();
        session.put (SessionManager.SESSION_USERNAME_KEY, user1.username);
        FilterSessionListener listener = new FilterSessionListener();
        listener.sessionStarted (session);

        Map filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertEquals (2, filters.size());
        def groupFilters = filters.get(FilterManager.GROUP_FILTERS);
        assertEquals(1, groupFilters.size());
        assertEquals("name:script1", groupFilters[0])
        def classFilters = filters.get(FilterManager.CLASS_FILTERS);
        assertEquals(0, classFilters.size());

        listener.sessionEnded (session);
        filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertNull(filters);

        //Test with a nonexisting user
        session = new Session();
        session.put (SessionManager.SESSION_USERNAME_KEY, "not existing user");
        listener.sessionStarted (session);
        filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertNull (filters);

        //Test with a user with group not having a segmentation filter
        session = new Session();
        session.put (SessionManager.SESSION_USERNAME_KEY, user3.username);
        listener.sessionStarted (session);
        filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertEquals (2, filters.size());
        groupFilters = filters.get(FilterManager.GROUP_FILTERS);
        assertEquals(0, groupFilters.size());
        classFilters = filters.get(FilterManager.CLASS_FILTERS);
        assertEquals(0, classFilters.size());

        // Test with a user with group having class based filters
        session = new Session();
        session.put (SessionManager.SESSION_USERNAME_KEY, user5.username);
        listener.sessionStarted (session);
        
        filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertEquals (2, filters.size());
        groupFilters = filters.get(FilterManager.GROUP_FILTERS);
        assertEquals(0, groupFilters.size());
        classFilters = filters.get(FilterManager.CLASS_FILTERS);
        assertEquals(1, classFilters.size());
        def objectClassQueries = classFilters[Object.class.name];

        assertEquals(2, objectClassQueries.size());
        assertTrue(objectClassQueries.contains(segmentFilter1))
        assertTrue(objectClassQueries.contains(segmentFilter2))

        //Test with a user with groups having both global segment filter and class based filters
        session = new Session();
        session.put (SessionManager.SESSION_USERNAME_KEY, user4.username);
        listener.sessionStarted (session);

        filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertEquals (2, filters.size());
        groupFilters = filters.get(FilterManager.GROUP_FILTERS);
        assertEquals(1, groupFilters.size());
        assertEquals("name:script1", groupFilters[0])
        classFilters = filters.get(FilterManager.CLASS_FILTERS);
        assertEquals(1, classFilters.size());
        objectClassQueries = classFilters[Object.class.name];

        assertEquals(1, objectClassQueries.size());
        assertTrue(objectClassQueries.contains(segmentFilter1))
    }
}