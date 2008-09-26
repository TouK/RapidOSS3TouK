import auth.RsUser
import auth.Group
import com.ifountain.compass.search.FilterManager

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 26, 2008
 * Time: 5:10:18 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchFilterFilters {
    def filters = {
        all(controller: "*", action: "*") {
            before = {
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
            after = {
             	FilterManager.clearFilters();
            }   
        }
    }

}