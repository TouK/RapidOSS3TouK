package com.ifountain.rcmdb.auth
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 12, 2009
 * Time: 11:40:16 AM
 */
class GroupBean {
    private String name
    private String role
    public GroupBean(String name, String role) {
        this.name = name;
        this.role = role;
    }

    public String getName() {
        return name;
    }
    public String getRole() {
        return role;
    }
}