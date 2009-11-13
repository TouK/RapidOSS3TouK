package com.ifountain.rcmdb.auth

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import auth.Role
import auth.Group
import auth.RsUser

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 12, 2009
* Time: 10:46:16 AM
*/
class UserConfigurationSpaceTests extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        initialize([Group, Role, RsUser], []);
    }

    public void tearDown() {
        super.tearDown();
    }

    public void testUserConfigurationSpaceIsSingleton() {
        UserConfigurationSpace space1 = UserConfigurationSpace.getInstance();
        UserConfigurationSpace space2 = UserConfigurationSpace.getInstance();
        assertSame(space1, space2);
    }

    public void testInitialize() {
        Role role1 = Role.add(name: "role1");
        Role role2 = Role.add(name: "role2");
        Role role3 = Role.add(name: "role3");

        Group group1 = Group.add(name: "group1", role: role1)
        Group group2 = Group.add(name: "group2", role: role2)
        Group group3 = Group.add(name: "group3", role: role3)

        RsUser user1 = RsUser.add(username: "user1", passwordHash: "pass", groups: [group1, group2])
        RsUser user2 = RsUser.add(username: "user2", passwordHash: "pass", groups: [group1, group3])
        RsUser user3 = RsUser.add(username: "user3", passwordHash: "pass", groups: [group3, group2])

        UserConfigurationSpace.getInstance().initialize();
        assertTrue(UserConfigurationSpace.getInstance().hasAllRoles("user1", ["role2", "role1"]))
        assertTrue(UserConfigurationSpace.getInstance().hasAllGroups("user1", ["group1", "group2"]))

        assertTrue(UserConfigurationSpace.getInstance().hasAllRoles("user2", ["role3", "role1"]))
        assertTrue(UserConfigurationSpace.getInstance().hasAllGroups("user2", ["group1", "group3"]))

        assertTrue(UserConfigurationSpace.getInstance().hasAllRoles("user3", ["role2", "role3"]))
        assertTrue(UserConfigurationSpace.getInstance().hasAllGroups("user3", ["group3", "group2"]))
    }

    public void testUserAdded() {
        Role role1 = Role.add(name: "role1");
        Role role2 = Role.add(name: "role2");

        Group group1 = Group.add(name: "group1", role: role1)
        Group group2 = Group.add(name: "group2", role: role2)

        RsUser user1 = RsUser.add(username: "user1", passwordHash: "pass", groups: [group1])

        UserConfigurationSpace.getInstance().initialize();
        assertTrue(UserConfigurationSpace.getInstance().hasRole("user1", "role1"))
        assertTrue(UserConfigurationSpace.getInstance().hasGroup("user1", "group1"))

        user1 = RsUser.add(username: "user1", passwordHash: "pass", groups: [group2]);

        UserConfigurationSpace.getInstance().userAdded(user1);

        assertTrue(UserConfigurationSpace.getInstance().hasRole("user1", "role2"))
        assertTrue(UserConfigurationSpace.getInstance().hasGroup("user1", "group2"))
        assertFalse(UserConfigurationSpace.getInstance().hasRole("user1", "role1"))
        assertFalse(UserConfigurationSpace.getInstance().hasGroup("user1", "group1"))
    }

    public void testUserRemoved() {
        Role role1 = Role.add(name: "role1");
        Group group1 = Group.add(name: "group1", role: role1)
        RsUser user1 = RsUser.add(username: "user1", passwordHash: "pass", groups: [group1])

        UserConfigurationSpace.getInstance().initialize();
        assertTrue(UserConfigurationSpace.getInstance().hasUser("user1"));
        assertTrue(UserConfigurationSpace.getInstance().hasRole("user1", "role1"))
        assertTrue(UserConfigurationSpace.getInstance().hasGroup("user1", "group1"))

        UserConfigurationSpace.getInstance().userRemoved("user1");

        assertFalse(UserConfigurationSpace.getInstance().hasUser("user1"))
        assertFalse(UserConfigurationSpace.getInstance().hasRole("user1", "role1"))
        assertFalse(UserConfigurationSpace.getInstance().hasGroup("user1", "group1"))
    }

    public void testGroupAdded() {
        Role role1 = Role.add(name: "role1");
        Role role2 = Role.add(name: "role2");
        Group group1 = Group.add(name: "group1", role: role1)
        RsUser user1 = RsUser.add(username: "user1", passwordHash: "pass", groups: [group1])
        RsUser user2 = RsUser.add(username: "user2", passwordHash: "pass", groups: [group1])

        UserConfigurationSpace.getInstance().initialize();
        assertTrue(UserConfigurationSpace.getInstance().hasUser("user1"));
        assertTrue(UserConfigurationSpace.getInstance().hasRole("user1", "role1"))
        assertTrue(UserConfigurationSpace.getInstance().hasGroup("user1", "group1"))

        assertTrue(UserConfigurationSpace.getInstance().hasUser("user2"));
        assertTrue(UserConfigurationSpace.getInstance().hasRole("user2", "role1"))
        assertTrue(UserConfigurationSpace.getInstance().hasGroup("user2", "group1"))

        RsUser user3 = RsUser.add(username: "user3", passwordHash: "pass", groups: [group1]);
        group1.update(role: role2, users: [user1, user3]);

        UserConfigurationSpace.getInstance().groupAdded(group1);

        assertTrue(UserConfigurationSpace.getInstance().hasUser("user1"));
        assertTrue(UserConfigurationSpace.getInstance().hasUser("user2"));
        assertTrue(UserConfigurationSpace.getInstance().hasUser("user3"));

        assertTrue(UserConfigurationSpace.getInstance().hasRole("user1", "role2"))
        assertTrue(UserConfigurationSpace.getInstance().hasGroup("user1", "group1"))
        assertFalse(UserConfigurationSpace.getInstance().hasRole("user1", "role1"))

        assertFalse(UserConfigurationSpace.getInstance().hasRole("user2", "role2"))
        assertFalse(UserConfigurationSpace.getInstance().hasRole("user2", "role1"))
        assertFalse(UserConfigurationSpace.getInstance().hasGroup("user2", "group1"))

        assertTrue(UserConfigurationSpace.getInstance().hasRole("user3", "role2"))
        assertTrue(UserConfigurationSpace.getInstance().hasGroup("user3", "group1"))
    }

    public void testGroupRemoved() {
        Role role1 = Role.add(name: "role1");
        Group group1 = Group.add(name: "group1", role: role1)
        RsUser user1 = RsUser.add(username: "user1", passwordHash: "pass", groups: [group1])
        RsUser user2 = RsUser.add(username: "user2", passwordHash: "pass", groups: [group1])

        UserConfigurationSpace.getInstance().initialize();
        assertTrue(UserConfigurationSpace.getInstance().hasUser("user1"));
        assertTrue(UserConfigurationSpace.getInstance().hasRole("user1", "role1"))
        assertTrue(UserConfigurationSpace.getInstance().hasGroup("user1", "group1"))

        assertTrue(UserConfigurationSpace.getInstance().hasUser("user2"));
        assertTrue(UserConfigurationSpace.getInstance().hasRole("user2", "role1"))
        assertTrue(UserConfigurationSpace.getInstance().hasGroup("user2", "group1"))

        UserConfigurationSpace.getInstance().groupRemoved("group1");

        assertTrue(UserConfigurationSpace.getInstance().hasUser("user1"));
        assertFalse(UserConfigurationSpace.getInstance().hasRole("user1", "role1"))
        assertFalse(UserConfigurationSpace.getInstance().hasGroup("user1", "group1"))

        assertTrue(UserConfigurationSpace.getInstance().hasUser("user2"));
        assertFalse(UserConfigurationSpace.getInstance().hasRole("user1", "role2"))
        assertFalse(UserConfigurationSpace.getInstance().hasGroup("user1", "group2"))
    }
}