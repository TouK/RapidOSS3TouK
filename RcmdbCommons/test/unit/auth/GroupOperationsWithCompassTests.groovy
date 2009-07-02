package auth

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 2, 2009
* Time: 10:48:47 AM
* To change this template use File | Settings | File Templates.
*/
class GroupOperationsWithCompassTests extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        initialize([Group, SegmentFilter], []);
        CompassForTests.addOperationSupport(Group, GroupOperations);
        SegmentQueryHelper.getInstance().initialize([]);
    }

    public void testSegmentGroupFiltersCreation() {
        String groupName = "group1";
        assertNull(SegmentQueryHelper.getInstance().getGroupFilters(groupName))

        Group group = Group.createGroup(name: groupName, segmentFilter: "name:*", segmentFilterType: Group.GLOBAL_FILTER);
        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);
        assertEquals("name:*", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
    }

    public void testSegmentGroupFiltersRemovalAfterDelete() {
        String groupName = "group1";
        Group group = Group.createGroup(name: groupName, segmentFilter: "name:*", segmentFilterType: Group.GLOBAL_FILTER);
        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);

        group.remove();
        groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNull(groupFilters);

    }

}