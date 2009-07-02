package com.ifountain.rcmdb.auth

import auth.Group
import auth.Role
import auth.RsUser
import auth.SegmentFilter
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

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
        initialize([SegmentFilter, Group], []);
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
        def classes = [SegmentQueryHelper.class, SegmentQueryHelperTest.class]
        def groupName = "group1"
        SegmentQueryHelper.getInstance().initialize(classes);
        assertNull(SegmentQueryHelper.getInstance().getGroupFilters(groupName));

        def segmentFilter = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.GLOBAL_FILTER, segmentFilter: segmentFilter);
        def filter1 = SegmentFilter.add(className: SegmentQueryHelper.class.name, groupId: group.id, filter: "alias:*", group: group)
        assertFalse(filter1.hasErrors());
        def filter2 = SegmentFilter.add(className: SegmentQueryHelperTest.class.name, groupId: group.id, filter: "alias:*", group: group)
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
        def filter1 = SegmentFilter.add(className: SegmentQueryHelper.class.name, groupId: group.id, filter: "alias:*", group: group)
        assertFalse(filter1.hasErrors());
        def filter2 = SegmentFilter.add(className: SegmentQueryHelperTest.class.name, groupId: group.id, filter: "alias:*", group: group)
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
        def classes = [SegmentQueryHelper.class, Object.class, SegmentQueryHelperTest.class]
        SegmentQueryHelper.getInstance().initialize(classes);

        def groupName = "group1"
        def query = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: SegmentQueryHelper.class.name, groupId: group.id, filter: query, group: group)
        assertFalse(filter1.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertEquals("", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(2, classesFilters.size());
        assertEquals(query, classesFilters[SegmentQueryHelper.class.name])
        assertEquals("(alias:${SegmentQueryHelper.class.name} AND (${query})) OR (alias:* NOT alias:${SegmentQueryHelper.class.name})", classesFilters[Object.class.name])
    }

    public void testParentClassHavingSegmentFilter(){
        def classes = [SegmentQueryHelper.class, Object.class, SegmentQueryHelperTest.class]
        SegmentQueryHelper.getInstance().initialize(classes);

        def groupName = "group1"
        def query = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: Object.class.name, groupId: group.id, filter: query, group: group)
        assertFalse(filter1.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);
        
        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertEquals("", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(3, classesFilters.size());
        assertEquals(query, classesFilters[SegmentQueryHelper.class.name])
        assertEquals(query, classesFilters[SegmentQueryHelperTest.class.name])
        assertEquals(query, classesFilters[Object.class.name])
    }

    public void testBothChildAndParentClassHavingSegmentFilter(){
        def classes = [SegmentQueryHelper.class, Object.class, SegmentQueryHelperTest.class]
        SegmentQueryHelper.getInstance().initialize(classes);

        def groupName = "group1"
        def parentQuery = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: Object.class.name, groupId: group.id, filter: parentQuery, group: group)
        assertFalse(filter1.hasErrors());

        def childQuery = "displayName:b*";
        def filter2 = SegmentFilter.add(className: SegmentQueryHelper.class.name, groupId: group.id, filter: childQuery, group: group)
        assertFalse(filter2.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        assertEquals("", groupFilters[SegmentQueryHelper.SEGMENT_FILTER]);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(3, classesFilters.size());
        def objectClassQuery = "${parentQuery} AND ((alias:${SegmentQueryHelper.class.name} AND (${childQuery})) OR (alias:* NOT alias:${SegmentQueryHelper.class.name}))"; 
        assertEquals(objectClassQuery, classesFilters[Object.class.name])
        assertEquals("${childQuery} AND (${parentQuery})", classesFilters[SegmentQueryHelper.class.name])
        assertEquals(parentQuery, classesFilters[SegmentQueryHelperTest.class.name])
    }

    public void testThreeLevelHierarchyAllHavingFilters(){
        def classes = [Exception.class, IOException.class, RuntimeException.class, ConcurrentModificationException.class, IllegalArgumentException.class, InvalidPropertiesFormatException.class]
        SegmentQueryHelper.getInstance().initialize(classes)
        def groupName = "group1"
        def rootQuery = "name:a*";
        def group = Group.add(name: groupName, segmentFilterType: Group.CLASS_BASED_FILTER);
        def filter1 = SegmentFilter.add(className: Exception.class.name, groupId: group.id, filter: rootQuery, group: group)
        assertFalse(filter1.hasErrors());

        def parentQuery = "displayName:b*";
        def filter2 = SegmentFilter.add(className: RuntimeException.class.name, groupId: group.id, filter: parentQuery, group: group)
        assertFalse(filter2.hasErrors());

        def childQuery = "description:b*";
        def filter3 = SegmentFilter.add(className: ConcurrentModificationException.class.name, groupId: group.id, filter: childQuery, group: group)
        assertFalse(filter3.hasErrors());

        SegmentQueryHelper.getInstance().calculateGroupFilters(groupName);

        def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName);
        def classesFilters = groupFilters[SegmentQueryHelper.CLASSES];

        assertEquals(6, classesFilters.size());
        def exClassQuery = rootQuery;
        exClassQuery += " AND ((alias:${RuntimeException.class.name} AND (${parentQuery})) OR (alias:${ConcurrentModificationException.class.name} AND (${childQuery}))"
        exClassQuery += " OR (alias:* NOT alias:${RuntimeException.class.name} NOT alias:${ConcurrentModificationException.class.name}))"

        assertEquals(exClassQuery, classesFilters[Exception.class.name]);

        def runtimeExClassQuery = parentQuery
        runtimeExClassQuery += " AND (${rootQuery})"
        runtimeExClassQuery += " AND ((alias:${ConcurrentModificationException.class.name} AND (${childQuery})) OR (alias:* NOT alias:${ConcurrentModificationException.class.name}))"

        assertEquals(runtimeExClassQuery, classesFilters[RuntimeException.class.name]);

        def concExClassQuery = "${childQuery} AND ((${rootQuery}) AND (${parentQuery}))";
        assertEquals(concExClassQuery, classesFilters[ConcurrentModificationException.class.name]);

        def illArgExQuery = "(${rootQuery}) AND (${parentQuery})"
        assertEquals(illArgExQuery, classesFilters[IllegalArgumentException.class.name])
        
        assertEquals(rootQuery, classesFilters[IOException.class.name])
        assertEquals(rootQuery, classesFilters[InvalidPropertiesFormatException.class.name])

    }

    public void testInitialize(){
        def groupName = "group"
        for(i in 0..3){
            def group = Group.add(name: groupName + i, segmentFilterType: Group.CLASS_BASED_FILTER);
            assertFalse(group.hasErrors())
        }

        SegmentQueryHelper.getInstance().initialize([]);

        for(i in 0..3){
            def groupFilters = SegmentQueryHelper.getInstance().getGroupFilters(groupName + i);
            assertNotNull(groupFilters)
            assertEquals("", groupFilters[SegmentQueryHelper.SEGMENT_FILTER])
            def classFilters = groupFilters[SegmentQueryHelper.CLASSES];
            assertNotNull(classFilters);
            assertEquals(0, classFilters.size());
        }

    }

}