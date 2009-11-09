package com.ifountain.rcmdb.auth

import auth.Group
import auth.SegmentFilter
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.compass.search.FilterSessionListener
import com.ifountain.session.SessionManager
import com.ifountain.rcmdb.test.util.ClosureRunnerThread
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.domain.property.RelationUtils
import auth.Role
import auth.RsUser
import auth.RsUserOperations
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 1, 2009
* Time: 10:40:33 AM
* To change this template use File | Settings | File Templates.
*/
class SegmentQueryHelperTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        initialize([SegmentFilter, Group, Role, RsUser], []);
        CompassForTests.addOperationSupport(RsUser, RsUserOperations)
        SessionManager.destroyInstance();
        SessionManager.getInstance().addSessionListener(new FilterSessionListener());
    }

    public void tearDown() {
        super.tearDown();
    }

    public void testQueryHelperIsSingleton() {
        SegmentQueryHelper helper1 = SegmentQueryHelper.getInstance();
        SegmentQueryHelper helper2 = SegmentQueryHelper.getInstance();
        assertSame(helper1, helper2);
    }

    public void testGroupWithGlobalSegmentFilter() {
        def classes = [SegmentQueryHelperTest.class, RapidCmdbWithCompassTestCase.class]
        def groupName = "group1"
        SegmentQueryHelper.getInstance().initialize(classes);
        assertNull(SegmentQueryHelper.getInstance().getGroupFilters(groupName));

        def segmentFilter = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.GLOBAL_FILTER, segmentFilter: segmentFilter);
        def filter1 = SegmentFilter.add(className: RapidCmdbWithCompassTestCase.class.name, groupId: group.id, filter: "alias:*", group: [group])
        assertFalse(filter1.hasErrors());
        def filter2 = SegmentFilter.add(className: SegmentQueryHelperTest.class.name, groupId: group.id, filter: "alias:*", group: [group])
        assertFalse(filter2.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);

        assertEquals(segmentFilter, groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
        assertNull(groupFilters[SegmentQueryHelper.CLASSES]);
    }

    public void testSegmentFiltersWithNoParentClasses() {
        def classes = [SegmentQueryHelper.class, SegmentQueryHelperTest.class]
        SegmentQueryHelper.getInstance().initialize(classes);

        def groupName = "group1"

        def segmentFilter = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER, segmentFilter: segmentFilter);
        def filter1 = SegmentFilter.add(className: SegmentQueryHelper.class.name, groupId: group.id, filter: "alias:*", group: [group])
        assertFalse(filter1.hasErrors());
        def filter2 = SegmentFilter.add(className: SegmentQueryHelperTest.class.name, groupId: group.id, filter: "alias:*", group: [group])
        assertFalse(filter2.hasErrors());

        def filter3 = SegmentFilter.add(className: Date.class.name, groupId: group.id, filter: "alias:*", group: group)
        assertFalse(filter3.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertNotNull(groupFilters);

        assertEquals("", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];
        assertNotNull(classesFilters);
        assertEquals(2, classesFilters.size());

        def query1 = classesFilters[SegmentQueryHelper.class.name];
        assertEquals("alias:*", query1)

        def query2 = classesFilters[SegmentQueryHelper.class.name];
        assertEquals("alias:*", query2)
    }

    public void testOnlyChildClassHasASegmentFilter() {
        def classes = [Exception.class, RuntimeException.class, ClassNotFoundException.class]
        SegmentQueryHelper.getInstance().initialize(classes);

        def groupName = "group1"
        def query = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: RuntimeException.class.name, groupId: group.id, filter: query, group: [group])
        assertFalse(filter1.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertEquals("", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(2, classesFilters.size());
        String parentQuery = "(alias:* NOT alias:${RuntimeException.class.name}) OR (alias:${RuntimeException.class.name} AND (${query}))";
        String childQuery = "alias:${RuntimeException.class.name} AND (${parentQuery})"
        assertEquals(parentQuery, classesFilters[Exception.class.name])
        assertEquals(childQuery, classesFilters[RuntimeException.class.name])
    }

    public void testParentClassHavingSegmentFilter() {
        def classes = [Exception.class, RuntimeException.class, ClassNotFoundException.class]
        SegmentQueryHelper.getInstance().initialize(classes);

        def groupName = "group1"
        def query = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: Exception.class.name, groupId: group.id, filter: query, group: [group])
        assertFalse(filter1.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertEquals("", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(3, classesFilters.size());
        assertEquals(query, classesFilters[Exception.class.name])
        assertEquals("alias:${RuntimeException.class.name} AND (${query})", classesFilters[RuntimeException.class.name])
        assertEquals("alias:${ClassNotFoundException.class.name} AND (${query})", classesFilters[ClassNotFoundException.class.name])
    }

    public void testBothChildAndParentClassHavingSegmentFilter() {
        def classes = [Exception.class, RuntimeException.class, ClassNotFoundException.class]
        SegmentQueryHelper.getInstance().initialize(classes);

        def groupName = "group1"
        def parentQuery = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: Exception.class.name, groupId: group.id, filter: parentQuery, group: [group])
        assertFalse(filter1.hasErrors());

        def childQuery = "displayName:b*";
        def filter2 = SegmentFilter.add(className: RuntimeException.class.name, groupId: group.id, filter: childQuery, group: [group])
        assertFalse(filter2.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertEquals("", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(3, classesFilters.size());
        def exClassQuery = "${parentQuery} AND ((alias:* NOT alias:${RuntimeException.class.name}) OR (alias:${RuntimeException.class.name} AND (${childQuery})))";
        assertEquals(exClassQuery, classesFilters[Exception.class.name])
        assertEquals("alias:${RuntimeException.class.name} AND (${exClassQuery})", classesFilters[RuntimeException.class.name])
        assertEquals("alias:${ClassNotFoundException.class.name} AND (${parentQuery})", classesFilters[ClassNotFoundException.class.name])
    }

    public void testThreeLevelHierarchyAllHavingFilters() {
        def classes = [Exception.class, IOException.class, RuntimeException.class, ConcurrentModificationException.class, IllegalArgumentException.class, InvalidPropertiesFormatException.class]
        SegmentQueryHelper.getInstance().initialize(classes)
        def groupName = "group1"
        def rootQuery = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: Exception.class.name, groupId: group.id, filter: rootQuery, group: [group])
        assertFalse(filter1.hasErrors());

        def parentQuery = "displayName:b*";
        def filter2 = SegmentFilter.add(className: RuntimeException.class.name, groupId: group.id, filter: parentQuery, group: [group])
        assertFalse(filter2.hasErrors());

        def childQuery = "description:b*";
        def filter3 = SegmentFilter.add(className: ConcurrentModificationException.class.name, groupId: group.id, filter: childQuery, group: [group])
        assertFalse(filter3.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(6, classesFilters.size());
        def concurrentQuery = "alias:${ConcurrentModificationException.class.name} AND (${childQuery})"
        def runtimeQuery = "alias:${RuntimeException.class.name} AND (${parentQuery}) AND ((alias:* NOT alias:${ConcurrentModificationException.class.name}) OR (${concurrentQuery}))"
        def exClassQuery = rootQuery;
        exClassQuery += " AND ((alias:* NOT alias:${RuntimeException.class.name} NOT alias:${ConcurrentModificationException.class.name}) OR (${runtimeQuery}))"

        assertEquals(exClassQuery, classesFilters[Exception.class.name]);
        assertEquals("alias:${IOException.class.name} AND (${rootQuery})", classesFilters[IOException.class.name])
        assertEquals("alias:${InvalidPropertiesFormatException.class.name} AND (${rootQuery})", classesFilters[InvalidPropertiesFormatException.class.name])

        assertEquals("alias:${RuntimeException.class.name} AND (${exClassQuery})", classesFilters[RuntimeException.class.name]);
        assertEquals("alias:${ConcurrentModificationException.class.name} AND (${exClassQuery})", classesFilters[ConcurrentModificationException.class.name]);

        runtimeQuery = "alias:${RuntimeException.class.name} AND (${parentQuery})"
        exClassQuery = "${rootQuery} AND ((alias:* NOT alias:${RuntimeException.class.name}) OR (${runtimeQuery}))"
        assertEquals("alias:${IllegalArgumentException.class.name} AND (${exClassQuery})", classesFilters[IllegalArgumentException.class.name])
    }

    public void testThreeLevelHieararchFirstAndSecondLevelHavingFilters() {
        def classes = [Exception.class, IOException.class, RuntimeException.class, ConcurrentModificationException.class, IllegalArgumentException.class, InvalidPropertiesFormatException.class]
        SegmentQueryHelper.getInstance().initialize(classes)
        def groupName = "group1"
        def rootQuery = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: Exception.class.name, groupId: group.id, filter: rootQuery, group: [group])
        assertFalse(filter1.hasErrors());

        def parentQuery1 = "displayName:b*";
        def filter2 = SegmentFilter.add(className: RuntimeException.class.name, groupId: group.id, filter: parentQuery1, group: [group])
        assertFalse(filter2.hasErrors());

        def parentQuery2 = "description:b*";
        def filter3 = SegmentFilter.add(className: IOException.class.name, groupId: group.id, filter: parentQuery2, group: [group])
        assertFalse(filter3.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(6, classesFilters.size());

        def runtimeQuery = "alias:${RuntimeException.class.name} AND (${parentQuery1})"
        def ioQuery = "alias:${IOException.class.name} AND (${parentQuery2})"
        def exQuery = "${rootQuery} AND ((alias:* NOT alias:${RuntimeException.class.name} NOT alias:${IOException.class.name}) OR (${ioQuery}) OR (${runtimeQuery}))"

        assertEquals(exQuery, classesFilters[Exception.class.name])

        exQuery = "${rootQuery} AND ((alias:* NOT alias:${RuntimeException.class.name}) OR (${runtimeQuery}))"
        assertEquals("alias:${RuntimeException.class.name} AND (${exQuery})", classesFilters[RuntimeException.class.name])
        assertEquals("alias:${ConcurrentModificationException.class.name} AND (${exQuery})", classesFilters[ConcurrentModificationException.class.name])
        assertEquals("alias:${IllegalArgumentException.class.name} AND (${exQuery})", classesFilters[IllegalArgumentException.class.name])

        exQuery = "${rootQuery} AND ((alias:* NOT alias:${IOException.class.name}) OR (${ioQuery}))"
        assertEquals("alias:${IOException.class.name} AND (${exQuery})", classesFilters[IOException.class.name])
        assertEquals("alias:${InvalidPropertiesFormatException.class.name} AND (${exQuery})", classesFilters[InvalidPropertiesFormatException.class.name])
    }

    public void testThreeLevelHieararchSecondAndThirdLevelHavingFilters() {
        def classes = [Exception.class, IOException.class, RuntimeException.class, ConcurrentModificationException.class, IllegalArgumentException.class, InvalidPropertiesFormatException.class]
        SegmentQueryHelper.getInstance().initialize(classes)
        def groupName = "group1"
        def parentQuery = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: RuntimeException.class.name, groupId: group.id, filter: parentQuery, group: [group])
        assertFalse(filter1.hasErrors());

        def leafQuery1 = "displayName:b*";
        def filter2 = SegmentFilter.add(className: ConcurrentModificationException.class.name, groupId: group.id, filter: leafQuery1, group: [group])
        assertFalse(filter2.hasErrors());

        def leafQuery2 = "description:b*";
        def filter3 = SegmentFilter.add(className: IllegalArgumentException.class.name, groupId: group.id, filter: leafQuery2, group: [group])
        assertFalse(filter3.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(4, classesFilters.size());

        def illQuery = "alias:${IllegalArgumentException.class.name} AND (${leafQuery2})"
        def concurrentQuery = "alias:${ConcurrentModificationException.class.name} AND (${leafQuery1})"
        def runtimeQuery = "alias:${RuntimeException.class.name} AND (${parentQuery}) AND ((alias:* NOT alias:${ConcurrentModificationException.class.name} NOT alias:${IllegalArgumentException.class.name}) OR (${concurrentQuery}) OR (${illQuery}))"
        def exQuery = "(alias:* NOT alias:${RuntimeException.class.name} NOT alias:${ConcurrentModificationException.class.name} NOT alias:${IllegalArgumentException.class.name}) OR (${runtimeQuery})"

        assertEquals(exQuery, classesFilters[Exception.class.name])
        assertEquals("alias:${RuntimeException.class.name} AND (${exQuery})", classesFilters[RuntimeException.class.name])

        runtimeQuery = "alias:${RuntimeException.class.name} AND (${parentQuery}) AND ((alias:* NOT alias:${ConcurrentModificationException.class.name}) OR (${concurrentQuery}))"
        exQuery = "(alias:* NOT alias:${RuntimeException.class.name} NOT alias:${ConcurrentModificationException.class.name}) OR (${runtimeQuery})"
        assertEquals("alias:${ConcurrentModificationException.class.name} AND (${exQuery})", classesFilters[ConcurrentModificationException.class.name])

        runtimeQuery = "alias:${RuntimeException.class.name} AND (${parentQuery}) AND ((alias:* NOT alias:${IllegalArgumentException.class.name}) OR (${illQuery}))"
        exQuery = "(alias:* NOT alias:${RuntimeException.class.name} NOT alias:${IllegalArgumentException.class.name}) OR (${runtimeQuery})"
        assertEquals("alias:${IllegalArgumentException.class.name} AND (${exQuery})", classesFilters[IllegalArgumentException.class.name])
    }

    public void testCalculateGroupFiltersDoesNotCauseDeadlockWithSessionMechanism() {
        def userRole=Role.add(name:Role.USER)
        assertFalse(userRole.hasErrors())


        def classes = [Exception.class, RuntimeException.class, ClassNotFoundException.class]
        SegmentQueryHelper.getInstance().initialize(classes);

        def groupName = "group1"
        def parentQuery = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER, role:userRole);
        def user = RsUser.addUser(username: "user1", password: "changeme", groups: [group]);
        def filter1 = SegmentFilter.add(className: Exception.class.name, groupId: group.id, filter: parentQuery, group: [group])
        assertFalse(filter1.hasErrors());

        def childQuery = "displayName:b*";
        def filter2 = SegmentFilter.add(className: RuntimeException.class.name, groupId: group.id, filter: childQuery, group: [group])
        assertFalse(filter2.hasErrors());
        
        def threads = []
        Object waitLock = new Object();
        Object filtersWaitLock = new Object();
        def relMetadata = DomainClassUtils.getRelations(ApplicationHolder.application.getDomainClass(Group.class.name)).get("filters")
        def isGetFiltersExecuted = false;
        Group.metaClass.getFilters = {->
            synchronized (filtersWaitLock) {
                if (!isGetFiltersExecuted) {
                    isGetFiltersExecuted = true;
                    filtersWaitLock.wait();
                }
            }
            return RelationUtils.getRelatedObjects(delegate, relMetadata)
        }
        Thread t1 = new ClosureRunnerThread(closure: {
            SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);
        })
        t1.start();
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertTrue(isGetFiltersExecuted);
        }))
        Thread t2 = new ClosureRunnerThread(closure: {
            SessionManager.getInstance().startSession(user.username);
        })
        t2.start();
        Thread.sleep(1000);
        synchronized (filtersWaitLock) {
            filtersWaitLock.notifyAll();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertTrue(t1.isFinished);
            assertTrue(t2.isFinished);
        }))

    }

    public void testInitialize() {
        def groupName = "group"
        for (i in 0..3) {
            def group = Group.add(name: groupName + i, segmentFilterType: Group.CLASS_BASED_FILTER);
            assertFalse(group.hasErrors())
        }

        SegmentQueryHelper.getInstance().initialize([]);

        for (i in 0..3) {
            def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName + i);
            assertNotNull(groupFilters)
            assertEquals("", groupFilters[SegmentQueryHelper.SEGMENT_FILTER])
            def classFilters = groupFilters[SegmentQueryHelper.CLASSES];
            assertNotNull(classFilters);
            assertEquals(0, classFilters.size());
        }

    }

}