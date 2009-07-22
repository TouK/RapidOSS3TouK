package com.ifountain.rcmdb.auth

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import auth.RsUser
import auth.Group
import auth.Role
import auth.SegmentFilter
import com.ifountain.rcmdb.test.util.CompassForTests
import auth.RsUserOperations
import auth.GroupOperations
import auth.SegmentFilterOperations
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.session.SessionManager
import com.ifountain.session.Session
import auth.ChannelUserInformation
import com.ifountain.compass.search.FilterSessionListener

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 2, 2009
* Time: 2:46:21 PM
* To change this template use File | Settings | File Templates.
*/
class SegmentationAcceptanceTests extends RapidCmdbWithCompassTestCase {
    Class rootClass;
    Class parentClass1;
    Class parentClass2;
    Class leafClass1;
    Class leafClass2;
    Class leafClass3;
    Group userGroup;
    RsUser user;
    Role userRole;
    public void setUp() {
        super.setUp();
        SessionManager.destroyInstance();
        SessionManager.getInstance().addSessionListener(new FilterSessionListener());
        createModels();
        initializeForAddAndUpdate();
        SegmentQueryHelper.getInstance().initialize([rootClass, parentClass1, parentClass2, leafClass1, leafClass2, leafClass3])

        userRole=Role.add(name:Role.USER)
        assertFalse(userRole.hasErrors())

        userGroup = Group.addGroup(name: "group1", segmentFilterType: Group.GLOBAL_FILTER,role:userRole)
        user = RsUser.addUser(username: "user1", password: "changeme", groups: [userGroup]);


    }

    public void tearDown() {
        SessionManager.destroyInstance();
        super.tearDown();
    }



    def initializeForAddAndUpdate() {
        initialize([RsUser, Group, Role, SegmentFilter, ChannelUserInformation, rootClass, parentClass1, parentClass2, leafClass1, leafClass2, leafClass3], [])
        CompassForTests.addOperationSupport(RsUser, RsUserOperations)
        CompassForTests.addOperationSupport(Group, GroupOperations)
        CompassForTests.addOperationSupport(SegmentFilter, SegmentFilterOperations)


    }
    def createModels() {
        def rootModelName = "RootModel";
        def parentModelName1 = "ParentModel1";
        def parentModelName2 = "ParentModel2";
        def leafModelName1 = "LeafModel1";
        def leafModelName2 = "LeafModel2";
        def leafModelName3 = "LeafModel3";
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE]
        def rootProp = [name: "rootProp", type: ModelGenerator.STRING_TYPE]
        def parentProp1 = [name: "parentProp1", type: ModelGenerator.STRING_TYPE]
        def parentProp2 = [name: "parentProp2", type: ModelGenerator.STRING_TYPE]
        def leafProp1 = [name: "leafProp1", type: ModelGenerator.STRING_TYPE]
        def leafProp2 = [name: "leafProp2", type: ModelGenerator.STRING_TYPE]
        def leafProp3 = [name: "leafProp3", type: ModelGenerator.STRING_TYPE]

        def rel = [name: "rel", reverseName: "revrel", toModel: parentModelName2, cardinality: ModelGenerator.RELATION_TYPE_ONE, reverseCardinality: ModelGenerator.RELATION_TYPE_MANY, isOwner: true];
        def revrel = [name: "revrel", reverseName: "rel", toModel: parentModelName1, cardinality: ModelGenerator.RELATION_TYPE_MANY, reverseCardinality: ModelGenerator.RELATION_TYPE_ONE, isOwner: false];

        def rootMetaProps = [name: rootModelName]
        def parent1MetaProps = [name: parentModelName1, parentModel: rootModelName]
        def parent2MetaProps = [name: parentModelName2, parentModel: rootModelName]
        def leaf1MetaProps = [name: leafModelName1, parentModel: parentModelName1]
        def leaf2MetaProps = [name: leafModelName2, parentModel: parentModelName1]
        def leaf3MetaProps = [name: leafModelName3, parentModel: parentModelName2]

        def rootText = ModelGenerationTestUtils.getModelText(rootMetaProps, [rootProp, keyProp], [keyProp], []);
        def parentText1 = ModelGenerationTestUtils.getModelText(parent1MetaProps, [parentProp1], [], [rel]);
        def parentText2 = ModelGenerationTestUtils.getModelText(parent2MetaProps, [parentProp2], [], [revrel]);
        def leafText1 = ModelGenerationTestUtils.getModelText(leaf1MetaProps, [leafProp1], [], []);
        def leafText2 = ModelGenerationTestUtils.getModelText(leaf2MetaProps, [leafProp2], [], []);
        def leafText3 = ModelGenerationTestUtils.getModelText(leaf3MetaProps, [leafProp3], [], []);

        gcl.parseClass(rootText + parentText1 + parentText2 + leafText1 + leafText2 + leafText3);
        rootClass = gcl.loadClass(rootModelName)
        parentClass1 = gcl.loadClass(parentModelName1)
        parentClass2 = gcl.loadClass(parentModelName2)
        leafClass1 = gcl.loadClass(leafModelName1)
        leafClass2 = gcl.loadClass(leafModelName2)
        leafClass3 = gcl.loadClass(leafModelName3)
    }

    public void testSegmentationWithGlobalFilter() {
        def segmentFilter = "rootProp:a*";
        userGroup.update(segmentFilter: segmentFilter);

        def rootObject1 = rootClass.add(keyProp: "key1", rootProp: "aa")
        def rootObject2 = rootClass.add(keyProp: "key2", rootProp: "bb")

        def parent1Object1 = parentClass1.add(keyProp: "key3", rootProp: "aa")
        def parent1Object2 = parentClass1.add(keyProp: "key4", rootProp: "bb")

        def parent2Object1 = parentClass2.add(keyProp: "key5", rootProp: "aa")
        def parent2Object2 = parentClass2.add(keyProp: "key6", rootProp: "bb")
        def parent2Object3 = parentClass2.add(keyProp: "key7", rootProp: "aaa")

        parent1Object1.addRelation(rel: [parent2Object1, parent2Object2]);
        parent1Object2.addRelation(rel: [parent2Object3]);

        assertEquals(7, rootClass.count())
        assertEquals(2, parent1Object1.rel.size())
        assertTrue(parent1Object1.rel.contains(parent2Object1))
        assertTrue(parent1Object1.rel.contains(parent2Object2))
        assertEquals(parent1Object2, parent2Object3.revrel)

        SessionManager.getInstance().startSession(user.username);

        def objects = rootClass.list();
        assertEquals(4, objects.size())

        assertTrue(objects.contains(rootObject1))
        assertTrue(objects.contains(parent1Object1))
        assertTrue(objects.contains(parent2Object1))
        assertTrue(objects.contains(parent2Object3))

        assertEquals(1, parent1Object1.rel.size())
        assertEquals(parent2Object1, parent1Object1.rel[0])
        assertNull(parent2Object3.revrel)
    }

    public void testClassBasedSegmentationWithNoParent() {
        userGroup.update(segmentFilterType: Group.CLASS_BASED_FILTER);
        def classFilter = "rootProp:a*"
        SegmentFilter.add(className: rootClass.name, filter: classFilter, group: [userGroup], groupId: userGroup.id);

        def rootObject1 = rootClass.add(keyProp: "key1", rootProp: "aa")
        def rootObject2 = rootClass.add(keyProp: "key2", rootProp: "bb")

        assertEquals(2, rootClass.count())

        SessionManager.getInstance().startSession(user.username);
        assertEquals(1, rootClass.count())
        assertNotNull(rootClass.get(keyProp: "key1"));
    }

    public void testChildClassHavingSegmentFilter() {
        userGroup.update(segmentFilterType: Group.CLASS_BASED_FILTER);
        def classFilter = "parentProp1:a*"
        SegmentFilter.add(className: parentClass1.name, filter: classFilter, group: [userGroup], groupId: userGroup.id);

        def rootObject1 = rootClass.add(keyProp: "key1", rootProp: "aa")
        def parent1Object1 = parentClass1.add(keyProp: "key2", parentProp1: "aa")
        def parent1Object2 = parentClass1.add(keyProp: "key3", parentProp1: "bb")
        def parent2Object1 = parentClass2.add(keyProp: "key4", rootProp: "aa");

        def leaf1Object1 = leafClass1.add(keyProp: "key5", parentProp1: "aa")
        def leaf1Object2 = leafClass1.add(keyProp: "key6", parentProp1: "bbb")

        def leaf2Object1 = leafClass2.add(keyProp: "key7", parentProp1: "aa")
        def leaf2Object2 = leafClass2.add(keyProp: "key8", parentProp1: "bbb")

        def leaf3Object1 = leafClass3.add(keyProp: "key9", rootProp: "aa")

        assertEquals(9, rootClass.count());
        SessionManager.getInstance().startSession(user.username);

        def objects = rootClass.list();
        assertEquals(6, objects.size())
        assertTrue(objects.contains(rootObject1))
        assertTrue(objects.contains(parent1Object1))
        assertTrue(objects.contains(parent2Object1))
        assertTrue(objects.contains(leaf1Object1))
        assertTrue(objects.contains(leaf2Object1))
        assertTrue(objects.contains(leaf3Object1))

        def parent1Objects = parentClass1.list();
        assertEquals(3, parent1Objects.size())
        assertTrue(parent1Objects.contains(parent1Object1))
        assertTrue(parent1Objects.contains(leaf1Object1))
        assertTrue(parent1Objects.contains(leaf2Object1))

        assertEquals(2, parentClass2.count())
        assertEquals(1, leafClass3.count())
    }

    public void testParentClassHavingSegmentFilter() {
        userGroup.update(segmentFilterType: Group.CLASS_BASED_FILTER);
        def classFilter = "rootProp:a*"
        SegmentFilter.add(className: rootClass.name, filter: classFilter, group: [userGroup], groupId: userGroup.id);

        def rootObject1 = rootClass.add(keyProp: "key1", rootProp: "aa")
        def rootObject2 = rootClass.add(keyProp: "key2", rootProp: "bb")

        def parent1Object1 = parentClass1.add(keyProp: "key3", rootProp: "aa")
        def parent1Object2 = parentClass1.add(keyProp: "key4", rootProp: "bb")

        def parent2Object1 = parentClass2.add(keyProp: "key5", rootProp: "aa")
        def parent2Object2 = parentClass2.add(keyProp: "key6", rootProp: "bb")
        def parent2Object3 = parentClass2.add(keyProp: "key7", rootProp: "aaa")

        parent1Object1.addRelation(rel: [parent2Object1, parent2Object2]);
        parent1Object2.addRelation(rel: [parent2Object3]);

        assertEquals(7, rootClass.count())
        assertEquals(2, parent1Object1.rel.size())
        assertTrue(parent1Object1.rel.contains(parent2Object1))
        assertTrue(parent1Object1.rel.contains(parent2Object2))
        assertEquals(parent1Object2, parent2Object3.revrel)

        SessionManager.getInstance().startSession(user.username);

        def objects = rootClass.list();
        assertEquals(4, objects.size())

        assertTrue(objects.contains(rootObject1))
        assertTrue(objects.contains(parent1Object1))
        assertTrue(objects.contains(parent2Object1))
        assertTrue(objects.contains(parent2Object3))

        assertEquals(1, parent1Object1.rel.size())
        assertEquals(parent2Object1, parent1Object1.rel[0])
        assertNull(parent2Object3.revrel)
    }

    public void testBothChildAndParentClassHavingSegmentFilter() {
        userGroup.update(segmentFilterType: Group.CLASS_BASED_FILTER);
        def parentClassFilter = "parentProp1:a*"
        def leafClassFilter = "rootProp:a*"
        SegmentFilter.add(className: parentClass1.name, filter: parentClassFilter, group: [userGroup], groupId: userGroup.id);
        SegmentFilter.add(className: leafClass1.name, filter: leafClassFilter, group: [userGroup], groupId: userGroup.id);

        def rootObject1 = rootClass.add(keyProp: "key1", rootProp: "aa")
        def rootObject2 = rootClass.add(keyProp: "key2", rootProp: "bb")

        def parent1Object1 = parentClass1.add(keyProp: "key3", rootProp: "aa", parentProp1: "bb")
        def parent1Object2 = parentClass1.add(keyProp: "key4", rootProp: "bb", parentProp1: "aa")

        def parent2Object1 = parentClass2.add(keyProp: "key5", rootProp: "aa")
        def parent2Object2 = parentClass2.add(keyProp: "key6", rootProp: "bb")

        def leaf1Obj1 = leafClass1.add(keyProp: "key7", rootProp: "aa", parentProp1: "aa")
        def leaf1Obj2 = leafClass1.add(keyProp: "key8", rootProp: "aa", parentProp1: "bb")
        def leaf1Obj3 = leafClass1.add(keyProp: "key9", rootProp: "bb", parentProp1: "aa")

        def leaf2Obj1 = leafClass2.add(keyProp: "key10", rootProp: "aa", parentProp1: "aa")
        def leaf2Obj2 = leafClass2.add(keyProp: "key11", rootProp: "aa", parentProp1: "bb")
        def leaf2Obj3 = leafClass2.add(keyProp: "key12", rootProp: "bb", parentProp1: "aa")

        assertEquals(12, rootClass.count());

        SessionManager.getInstance().startSession(user.username);

        assertEquals(2, parentClass2.count());
        def objects = rootClass.list();
        assertEquals(8, objects.size())

        assertTrue(objects.contains(rootObject1))
        assertTrue(objects.contains(rootObject2))
        assertTrue(objects.contains(parent1Object2))
        assertTrue(objects.contains(parent2Object1))
        assertTrue(objects.contains(parent2Object2))
        assertTrue(objects.contains(leaf1Obj1))
        assertTrue(objects.contains(leaf2Obj1))
        assertTrue(objects.contains(leaf2Obj3))

        objects = parentClass1.list();
        assertEquals(4, objects.size())
        assertTrue(objects.contains(parent1Object2))
        assertTrue(objects.contains(leaf1Obj1))
        assertTrue(objects.contains(leaf2Obj1))
        assertTrue(objects.contains(leaf2Obj3))

        objects = leafClass1.list();
        assertEquals(1, objects.size())
        assertTrue(objects.contains(leaf1Obj1))

        objects = leafClass2.list();
        assertEquals(2, objects.size())
        assertTrue(objects.contains(leaf2Obj1))
        assertTrue(objects.contains(leaf2Obj3))
    }

    public void testThreeLevelHierarchyAllHavingFilters() {
        userGroup.update(segmentFilterType: Group.CLASS_BASED_FILTER);
        def rootClassFilter = "rootProp:a*"
        def parentClassFilter = "rootProp:aa*"
        def leafClassFilter = "rootProp:aaa*"

        SegmentFilter.add(className: rootClass.name, filter: rootClassFilter, group: [userGroup], groupId: userGroup.id);
        SegmentFilter.add(className: parentClass1.name, filter: parentClassFilter, group: [userGroup], groupId: userGroup.id);
        SegmentFilter.add(className: leafClass1.name, filter: leafClassFilter, group: [userGroup], groupId: userGroup.id);

        def rootObject1 = rootClass.add(keyProp: "key1", rootProp: "aa")
        def rootObject2 = rootClass.add(keyProp: "key2", rootProp: "bb")

        def parent1Object1 = parentClass1.add(keyProp: "key3", rootProp: "a")
        def parent1Object2 = parentClass1.add(keyProp: "key4", rootProp: "aa")
        def parent1Object3 = parentClass1.add(keyProp: "key5", rootProp: "bb")

        def parent2Object1 = parentClass2.add(keyProp: "key6", rootProp: "aa")
        def parent2Object2 = parentClass2.add(keyProp: "key7", rootProp: "ab")
        def parent2Object3 = parentClass2.add(keyProp: "key18", rootProp: "bb")

        def leaf3Object1 = leafClass3.add(keyProp: "key12", rootProp: "aa")
        def leaf3Object2 = leafClass3.add(keyProp: "key13", rootProp: "ab")
        def leaf3Object3 = leafClass3.add(keyProp: "key19", rootProp: "bb")

        def leaf1Obj1 = leafClass1.add(keyProp: "key8", rootProp: "a")
        def leaf1Obj2 = leafClass1.add(keyProp: "key9", rootProp: "aa")
        def leaf1Obj3 = leafClass1.add(keyProp: "key10", rootProp: "aaa")
        def leaf1Obj4 = leafClass1.add(keyProp: "key11", rootProp: "bb")

        def leaf2Obj1 = leafClass2.add(keyProp: "key14", rootProp: "a")
        def leaf2Obj2 = leafClass2.add(keyProp: "key15", rootProp: "aa")
        def leaf2Obj3 = leafClass2.add(keyProp: "key16", rootProp: "aab")
        def leaf2Obj4 = leafClass2.add(keyProp: "key17", rootProp: "bb")


        assertEquals(19, rootClass.count());

        SessionManager.getInstance().startSession(user.username);

        assertEquals(4, parentClass2.count());
        def objects = rootClass.list();
        assertEquals(9, objects.size())

        def objectsReceived = [rootObject1, parent1Object2, parent2Object1, parent2Object2, leaf3Object1,
                leaf3Object2, leaf1Obj3, leaf2Obj2, leaf2Obj3]
        objectsReceived.each {
            assertTrue(objects.contains(it))
        }

        objects = parentClass2.list();
        assertEquals(4, objects.size())
        objectsReceived = [parent2Object1, parent2Object2, leaf3Object1, leaf3Object2]
        objectsReceived.each {
            assertTrue(objects.contains(it))
        }

        objects = leafClass3.list();
        assertEquals(2, objects.size())
        objectsReceived = [leaf3Object1, leaf3Object2]
        objectsReceived.each {
            assertTrue(objects.contains(it))
        }

        objects = parentClass1.list();
        assertEquals(4, objects.size())
        objectsReceived = [parent1Object2, leaf1Obj3, leaf2Obj2, leaf2Obj3]
        objectsReceived.each {
            assertTrue(objects.contains(it))
        }

        objects = leafClass1.list();
        assertEquals(1, objects.size())
        assertTrue(objects.contains(leaf1Obj3))

        objects = leafClass2.list();
        assertEquals(2, objects.size())
        assertTrue(objects.contains(leaf2Obj2))
        assertTrue(objects.contains(leaf2Obj3))
    }

    public void testThreeLevelHieararchFirstAndSecondLevelHavingFilters() {
        userGroup.update(segmentFilterType: Group.CLASS_BASED_FILTER);
        def rootClassFilter = "rootProp:a*"
        def parentClassFilter1 = "rootProp:aa*"
        def parentClassFilter2 = "rootProp:ab*"

        SegmentFilter.add(className: rootClass.name, filter: rootClassFilter, group: [userGroup], groupId: userGroup.id);
        SegmentFilter.add(className: parentClass1.name, filter: parentClassFilter1, group: [userGroup], groupId: userGroup.id);
        SegmentFilter.add(className: parentClass2.name, filter: parentClassFilter2, group: [userGroup], groupId: userGroup.id);

        def rootObject1 = rootClass.add(keyProp: "key1", rootProp: "aa")
        def rootObject2 = rootClass.add(keyProp: "key2", rootProp: "bb")

        def parent1Object1 = parentClass1.add(keyProp: "key3", rootProp: "a")
        def parent1Object2 = parentClass1.add(keyProp: "key4", rootProp: "aa")
        def parent1Object3 = parentClass1.add(keyProp: "key5", rootProp: "ab")

        def parent2Object1 = parentClass2.add(keyProp: "key6", rootProp: "a")
        def parent2Object2 = parentClass2.add(keyProp: "key7", rootProp: "aa")
        def parent2Object3 = parentClass2.add(keyProp: "key8", rootProp: "ab")

        def leaf1Obj1 = leafClass1.add(keyProp: "key9", rootProp: "a")
        def leaf1Obj2 = leafClass1.add(keyProp: "key10", rootProp: "aa")
        def leaf1Obj3 = leafClass1.add(keyProp: "key11", rootProp: "ab")

        def leaf3Object1 = leafClass3.add(keyProp: "key12", rootProp: "a")
        def leaf3Object2 = leafClass3.add(keyProp: "key13", rootProp: "aa")
        def leaf3Object3 = leafClass3.add(keyProp: "key14", rootProp: "ab")

        def leaf2Obj1 = leafClass2.add(keyProp: "key15", rootProp: "a")
        def leaf2Obj2 = leafClass2.add(keyProp: "key16", rootProp: "aa")
        def leaf2Obj3 = leafClass2.add(keyProp: "key17", rootProp: "ab")

        assertEquals(17, rootClass.count());

        SessionManager.getInstance().startSession(user.username);

        def objects = rootClass.list();
        assertEquals(6, objects.size())
        def objectsReceived = [rootObject1, parent1Object2, parent2Object3, leaf1Obj2, leaf2Obj2, leaf3Object3]
        objectsReceived.each {
            assertTrue(objects.contains(it))
        }

        objects = parentClass1.list();
        assertEquals(3, objects.size())
        objectsReceived = [parent1Object2, leaf1Obj2, leaf2Obj2]
        objectsReceived.each {
            assertTrue(objects.contains(it))
        }

        objects = leafClass1.list();
        assertEquals(1, objects.size())
        assertTrue(objects.contains(leaf1Obj2))

        objects = leafClass2.list();
        assertEquals(1, objects.size())
        assertTrue(objects.contains(leaf2Obj2))

        objects = parentClass2.list();
        assertEquals(2, objects.size())
        assertTrue(objects.contains(parent2Object3))
        assertTrue(objects.contains(leaf3Object3))

        objects = leafClass3.list();
        assertEquals(1, objects.size())
        assertTrue(objects.contains(leaf3Object3))
    }

    public void testThreeLevelHieararchSecondAndThirdLevelHavingFilters() {
        userGroup.update(segmentFilterType: Group.CLASS_BASED_FILTER);
        def parentClassFilter = "rootProp:a*"
        def leafClassFilter1 = "rootProp:aa*"
        def leafClassFilter2 = "rootProp:ab*"

        SegmentFilter.add(className: parentClass1.name, filter: parentClassFilter, group: [userGroup], groupId: userGroup.id);
        SegmentFilter.add(className: leafClass1.name, filter: leafClassFilter1, group: [userGroup], groupId: userGroup.id);
        SegmentFilter.add(className: leafClass2.name, filter: leafClassFilter2, group: [userGroup], groupId: userGroup.id);

        def rootObject1 = rootClass.add(keyProp: "key1", rootProp: "b")

        def parent1Object1 = parentClass1.add(keyProp: "key2", rootProp: "a")
        def parent1Object2 = parentClass1.add(keyProp: "key3", rootProp: "b")

        def parent2Object1 = parentClass2.add(keyProp: "key4", rootProp: "b")
        def leaf3Object1 = leafClass3.add(keyProp: "key5", rootProp: "b")

        def leaf1Obj1 = leafClass1.add(keyProp: "key9", rootProp: "a")
        def leaf1Obj2 = leafClass1.add(keyProp: "key10", rootProp: "aa")
        def leaf1Obj3 = leafClass1.add(keyProp: "key11", rootProp: "ab")

        def leaf2Obj1 = leafClass2.add(keyProp: "key15", rootProp: "a")
        def leaf2Obj2 = leafClass2.add(keyProp: "key16", rootProp: "aa")
        def leaf2Obj3 = leafClass2.add(keyProp: "key17", rootProp: "ab")

        assertEquals(11, rootClass.count());

        SessionManager.getInstance().startSession(user.username);

        def objects = rootClass.list();
        assertEquals(6, objects.size())
        def objectsReceived = [rootObject1, parent1Object1, parent2Object1, leaf1Obj2, leaf2Obj3, leaf3Object1]
        objectsReceived.each {
            assertTrue(objects.contains(it))
        }

        objects = parentClass1.list();
        assertEquals(3, objects.size())
        objectsReceived = [parent1Object1, leaf1Obj2, leaf2Obj3]
        objectsReceived.each {
            assertTrue(objects.contains(it))
        }

        objects = leafClass1.list();
        assertEquals(1, objects.size())
        assertTrue(objects.contains(leaf1Obj2))

        objects = leafClass2.list();
        assertEquals(1, objects.size())
        assertTrue(objects.contains(leaf2Obj3))

        assertEquals(2, parentClass2.count())
        assertEquals(1, leafClass3.count())
    }

    public void testSegmentationForAUserBelongingToMultipleGroups(){
        def segmentFilter1 = "rootProp:a*"
        def segmentFilter2 = "rootProp:b*"
        def segmentFilter3 = "rootProp:c*"
        def group2 = Group.addGroup(name:"group2", segmentFilterType:Group.CLASS_BASED_FILTER,role:userRole)
        def group3 = Group.addGroup(name:"group3", segmentFilterType:Group.CLASS_BASED_FILTER,role:userRole)
        user.addToGroups([group2, group3]);

        userGroup.update(segmentFilter:segmentFilter1);
        SegmentFilter.add(className: rootClass.name, filter: segmentFilter2, group: [group2], groupId: group2.id);
        SegmentFilter.add(className: rootClass.name, filter: segmentFilter3, group: [group3], groupId: group3.id);

        def rootObject1 = rootClass.add(keyProp: "key1", rootProp: "a")
        def rootObject2 = rootClass.add(keyProp: "key2", rootProp: "b")
        def rootObject3 = rootClass.add(keyProp: "key3", rootProp: "c")
        def rootObject4 = rootClass.add(keyProp: "key4", rootProp: "d")

        assertEquals(4, rootClass.count())

        SessionManager.getInstance().startSession(user.username);

        def objects = rootClass.list();
        assertEquals(3, objects.size())
        assertTrue(objects.contains(rootObject1))
        assertTrue(objects.contains(rootObject2))
        assertTrue(objects.contains(rootObject3))

    }

}