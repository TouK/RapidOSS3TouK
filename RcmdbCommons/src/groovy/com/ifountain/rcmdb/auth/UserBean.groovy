package com.ifountain.rcmdb.auth

import com.ifountain.comp.utils.CaseInsensitiveMap

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 12, 2009
* Time: 11:27:28 AM
*/
class UserBean {
    private String username
    private boolean changed = false;
    private Map groupsMap = new CaseInsensitiveMap();
    private Map rolesMap = new CaseInsensitiveMap();
    public UserBean(String name) {
        username = name;
    }
    public Map getGroups(){
        return groupsMap;
    }
    protected void groupAdded(GroupBean group) {
        groupsMap[group.name] = group;
        changed = true;
    }
    protected void groupRemoved(String groupName) {
        if (groupsMap.containsKey(groupName)) {
            groupsMap.remove(groupName);
            changed = true;
        }
    }
    protected void calculateGroupsAndRoles() {
        if (changed) {
            rolesMap = new CaseInsensitiveMap();
            groupsMap.each {String groupName, GroupBean group ->
                rolesMap[group.role] = group.role
            }
            changed = false;
        }
    }

    public boolean hasGroup(String groupName) {
        return groupsMap.containsKey(groupName);
    }
    public boolean hasAllGroups(List groupNames) {
        def hasAll = true;
        groupNames.each {
            if (!groupsMap.containsKey(it.toString())) {
                hasAll = false;
                return;
            }
        }
        return hasAll;
    }
    public boolean hasRole(String roleName) {
        return rolesMap.containsKey(roleName);
    }
    public boolean hasAllRoles(List roleNames) {
        def hasAll = true;
        roleNames.each {
            if (!rolesMap.containsKey(it.toString())) {
                hasAll = false;
                return;
            }
        }
        return hasAll;
    }
}