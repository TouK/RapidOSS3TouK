package com.ifountain.rcmdb.auth

import auth.RsUser
import auth.Group
import com.ifountain.comp.utils.CaseInsensitiveMap

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 12, 2009
* Time: 10:45:18 AM
*/
class UserConfigurationSpace {
    private static UserConfigurationSpace space = null;
    public static UserConfigurationSpace getInstance() {
        if (space == null) {
            space = new UserConfigurationSpace();
        }
        return space;
    }
    private UserConfigurationSpace() {};
    private Map users;

    public synchronized void initialize() {
        users = new CaseInsensitiveMap();
        RsUser.list().each {RsUser user ->
            userAdded(user);
        }
    }

    public boolean hasAllRoles(String username, List roleNames) {
        UserBean userBean = users[username];
        if (userBean) {
            return userBean.hasAllRoles(roleNames);
        }
        return false;
    }
    public boolean hasRole(String username, String roleName) {
        UserBean userBean = users[username];
        if (userBean) {
            return userBean.hasRole(roleName);
        }
        return false;
    }

    public boolean hasAllGroups(String username, List groupNames) {
        UserBean userBean = users[username];
        if (userBean) {
            return userBean.hasAllGroups(groupNames);
        }
        return false;
    }

    public boolean hasGroup(String username, String groupName) {
        UserBean userBean = users[username];
        if (userBean) {
            return userBean.hasGroup(groupName);
        }
        return false;
    }

    public boolean hasUser(String username) {
        return users.containsKey(username);
    }

    public void userAdded(RsUser user) {
        def username = user.username;
        UserBean userBean = new UserBean(username);
        users[username] = userBean;
        user.groups.each {Group group ->
            def groupName = group.name;
            GroupBean groupBean = new GroupBean(groupName, group.role?.name)
            userBean.groupAdded(groupBean)
        }
        userBean.calculateGroupsAndRoles();
    }

    public void userRemoved(String username) {
        users.remove(username);
    }

    public void groupAdded(Group group) {
        def groupUsers = group.users;
        def usernames = groupUsers.username;
        GroupBean groupBean = new GroupBean(group.name, group.role?.name);
        users.each {String username, UserBean userBean ->
            if (!usernames.contains(username)) {
                userBean.groupRemoved(group.name);
            }
            else {
                userBean.groupAdded(groupBean);
            }
            userBean.calculateGroupsAndRoles();
        }
        groupUsers.each {
            if (!users.containsKey(it.username)) {
                userAdded(it);
            }
        }
    }

    public void groupRemoved(String groupName) {
        users.each {String username, UserBean userBean ->
            userBean.groupRemoved(groupName);
            userBean.calculateGroupsAndRoles();
        }
    }
}