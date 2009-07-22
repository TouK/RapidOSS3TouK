package auth

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.auth.SegmentQueryHelper

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 2, 2009
* Time: 10:56:35 AM
* To change this template use File | Settings | File Templates.
*/
class SegmentFilterOperationsTests extends RapidCmdbWithCompassTestCase {

    def userRole;

    public void setUp() {
        super.setUp();
        initialize([Group, SegmentFilter,Role], []);
        CompassForTests.addOperationSupport(Group, GroupOperations);
        CompassForTests.addOperationSupport(SegmentFilter, SegmentFilterOperations);
        SegmentQueryHelper.getInstance().initialize([]);

        userRole=Role.add(name:Role.USER)
        assertFalse(userRole.hasErrors())
    }


    public void testSegmentFilterCalculationAfterCrudOperations(){
        def groupName = "group1"
        def group = Group.addGroup(name:groupName, segmentFilter:"name*", segmentFilterType:Group.GLOBAL_FILTER,role:userRole);
        assertFalse(group.hasErrors())

        SegmentQueryHelper.getInstance().removeGroupFilters(groupName);

        def segmentFilter = SegmentFilter.add(className:"RsEvent", filter:"alias:*", group:group, groupId:group.id);

        assertNotNull(SegmentQueryHelper.getInstance().getGroupFilters(groupName))

        SegmentQueryHelper.getInstance().removeGroupFilters(groupName);

        segmentFilter.update(filter:"name:*")
        assertNotNull(SegmentQueryHelper.getInstance().getGroupFilters(groupName))

        SegmentQueryHelper.getInstance().removeGroupFilters(groupName);
        segmentFilter.remove();
        assertNotNull(SegmentQueryHelper.getInstance().getGroupFilters(groupName))
    }
}