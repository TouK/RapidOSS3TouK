package com.ifountain.compass.search

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.session.Session
import auth.RsUser
import auth.Group
import com.ifountain.session.SessionManager

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
        initialize ([RsUser, Group],[]);

        RsUser user1 = RsUser.add(username:"user1", passwordHash:"password");
        RsUser user2 = RsUser.add(username:"user2", passwordHash:"password");
        RsUser user3 = RsUser.add(username:"user3", passwordHash:"password");
        assertFalse (user1.hasErrors());
        assertFalse (user2.hasErrors());
        assertFalse (user3.hasErrors());
        Group gr1 = Group.add(name:"gr1", segmentFilter:"name:script1", users:user1);
        Group gr2 = Group.add(name:"gr2", users:user3);
        assertFalse (gr1.hasErrors());
        assertFalse (gr2.hasErrors());


        Session session = new Session();
        session.put (SessionManager.SESSION_USERNAME_KEY, user1.username);
        FilterSessionListener listener = new FilterSessionListener();
        listener.sessionStarted (session);

        List filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertEquals (2, filters.size());
        assertTrue (filters.contains(gr1.segmentFilter));
        assertTrue (filters.contains(FilterSessionListener.DEFAULT_FILTER));

        listener.sessionEnded (session);
        filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertEquals (0, filters.size());

        //Test with a user does not have a group
        session = new Session();
        session.put (SessionManager.SESSION_USERNAME_KEY, user2.username);
        listener.sessionStarted (session);
        filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertNull (filters);

        //Test with a nonexisting user
        session = new Session();
        session.put (SessionManager.SESSION_USERNAME_KEY, "not existing user");
        listener.sessionStarted (session);
        filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertNull (filters);

        //Test with a user with group not having a segmentation filter
        session = new Session();
        session.put (SessionManager.SESSION_USERNAME_KEY, user2.username);
        listener.sessionStarted (session);
        filters = session.get (FilterManager.SESSION_FILTER_KEY);
        assertNull (filters);

    }
}