package com.ifountain.compass.search;

import com.ifountain.session.SessionListener;
import com.ifountain.session.Session
import auth.RsUser
import auth.Group
import com.ifountain.rcmdb.auth.SegmentQueryHelper;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 15, 2009
 * Time: 2:35:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilterSessionListener implements SessionListener {
    public void sessionEnded(Session session)
    {
        session.remove(FilterManager.SESSION_FILTER_KEY);
    }

    public void sessionStarted(Session session)
    {
        if (session.username != null)
        {
            RsUser user = RsUser.get(username: session.username);
            if (user)
            {
                def groups = user.groups;
                if (!groups.isEmpty())
                {
                    def filters = [:];
                    filters[FilterManager.GROUP_FILTERS] = [];
                    filters[FilterManager.CLASS_FILTERS] = [:]
                    groups.each {Group group ->
                        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(group.name)
                        if (groupFilters != null) {
                            def classesMap = groupFilters[SegmentQueryHelper.CLASSES];
                            if (classesMap != null) {
                                classesMap.each {className, classQuery ->
                                    def classQueries = filters[FilterManager.CLASS_FILTERS][className];
                                    if (classQueries == null) {
                                        classQueries = [];
                                        filters[FilterManager.CLASS_FILTERS][className] = classQueries
                                    }
                                    classQueries.add(classQuery);
                                }
                            }
                            else if (groupFilters[SegmentQueryHelper.SEGMENT_FILTER] != "") {
                                filters[FilterManager.GROUP_FILTERS].add(groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
                            }
                        }
                    }
                    session.put(FilterManager.SESSION_FILTER_KEY, filters);
                }
            }
        }
    }
}
