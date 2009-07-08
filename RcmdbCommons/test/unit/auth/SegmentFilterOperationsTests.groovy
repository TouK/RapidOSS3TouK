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

    public void setUp() {
        super.setUp();
        initialize([Group, SegmentFilter], []);
        CompassForTests.addOperationSupport(Group, GroupOperations);
        CompassForTests.addOperationSupport(SegmentFilter, SegmentFilterOperations);
        SegmentQueryHelper.getInstance().initialize([]);
    }


    public void testSegmentFilterCalculationAfterCrudOperations(){
        def groupName = "group1"
        def group = Group.createGroup(name:groupName, segmentFilter:"name*", segmentFilterType:Group.GLOBAL_FILTER);
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