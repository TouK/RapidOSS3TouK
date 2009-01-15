package com.ifountain.compass.search;

import com.ifountain.session.SessionListener;
import com.ifountain.session.Session
import auth.RsUser
import auth.Group;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 15, 2009
 * Time: 2:35:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilterSessionListener implements SessionListener{
    public static final String DEFAULT_FILTER = "rsOwner:p";
    public void sessionEnded(Session session)
    {
        session.get (FilterManager.SESSION_FILTER_KEY)?.clear();        
    }

    public void sessionStarted(Session session)
    {
        if(session.username != null)
        {
            RsUser user = RsUser.get(username:session.username);
            if(user)
            {
                def groups = user.groups;
                if(!groups.isEmpty())
                {
                    def filters = [];
                    def willAddRsOwner = false;
                    groups.each{Group group->
                        if(group.segmentFilter != null && group.segmentFilter != "")
                        {
                            filters.add(group.segmentFilter);
                            willAddRsOwner = true;
                        }
                    }
                    if(willAddRsOwner)
                    {
                        filters.add(DEFAULT_FILTER);
                    }
                    session.put (FilterManager.SESSION_FILTER_KEY, filters);
                }
            }
        }
    }
}
