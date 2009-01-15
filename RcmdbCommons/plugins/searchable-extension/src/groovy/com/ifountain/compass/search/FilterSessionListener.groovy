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
    public void sessionEnded(Session session)
    {
        FilterManager.clearFilters();
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
                    def willAddRsOwner = false;
                    groups.each{Group group->
                        if(group.segmentFilter != null && group.segmentFilter != "")
                        {
                            FilterManager.addFilter (group.segmentFilter);
                            willAddRsOwner = true;
                        }
                    }
                    if(willAddRsOwner)
                    {
                        FilterManager.addFilter ("rsOwner:p");
                    }
                }
            }
        }
    }
}
