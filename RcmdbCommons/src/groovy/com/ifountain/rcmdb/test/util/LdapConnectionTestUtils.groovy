package com.ifountain.rcmdb.test.util

import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by IntelliJ IDEA.
* User: deneme
* Date: Oct 4, 2008
* Time: 11:05:37 AM
* To change this template use File | Settings | File Templates.
*/
class LdapConnectionTestUtils {
    public static Map getConnectionParams(){
        def url = CommonTestUtils.getTestProperty("Ldap.URL");
        def username = CommonTestUtils.getTestProperty("Ldap.Username");
        def password = CommonTestUtils.getTestProperty("Ldap.Password");
        return ["url":url,"username":username,"userPassword":password];
    }
    public static Map getAuthorizationParams(){
        def url = CommonTestUtils.getTestProperty("Ldap.URL");
        def username = CommonTestUtils.getTestProperty("Ldap.AuthUsername");
        def password = CommonTestUtils.getTestProperty("Ldap.AuthPassword");
        return ["url":url,"username":username,"userPassword":password];
    }
    public static Map getQueryParams(){
        def searchBase = CommonTestUtils.getTestProperty("Ldap.SearchBase");
        def searchFilter = CommonTestUtils.getTestProperty("Ldap.SearchFilter");
        def searchSubDirectories = CommonTestUtils.getTestProperty("Ldap.SearchSubDirectories")=="1";
        return ["searchBase":searchBase,"searchFilter":searchFilter,"searchSubDirectories":searchSubDirectories];
    }
}