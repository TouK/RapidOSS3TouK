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
package search

import auth.RsUser

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 10, 2008
 * Time: 5:26:22 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchQueryGroup {
   public static String MY_QUERIES = "My Queries"
   public static String DEFAULT_TYPE = "default"
   static searchable = {
        except = ["queries","errors", "__operation_class__"];
    };
    
    Long id;
    Long version;
    Date rsInsertedAt = new Date(0);
    Date rsUpdatedAt  = new Date(0);
    String username;
    Boolean expanded = false;
    String rsOwner = "p"
    String name;
    List queries = [];
    boolean isPublic = false;
    String type = "";

    Object __operation_class__ ;
    org.springframework.validation.Errors errors ;
    
    static relations = [
            queries:[type:SearchQuery, reverseName:"group", isMany:true]
    ]
    static constraints = {
        name(key:["username", "type"]);
        __operation_class__(nullable:true)
        errors(nullable:true)
    }
    static transients = ["errors", "__operation_class__"];

}