/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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
    public static Map getAuthenticationParams(){
        def url = CommonTestUtils.getTestProperty("Ldap.URL");
        def username = CommonTestUtils.getTestProperty("Ldap.AuthUsername");
        def password = CommonTestUtils.getTestProperty("Ldap.AuthPassword");
        return ["url":url,"username":username,"userPassword":password];
    }
    public static Map getQueryParams(){
        def searchBase = CommonTestUtils.getTestProperty("Ldap.SearchBase");
        return ["searchBase":searchBase];
    }
}