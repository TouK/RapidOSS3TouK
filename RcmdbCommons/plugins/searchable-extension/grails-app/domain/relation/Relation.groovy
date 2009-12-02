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
package relation
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 1, 2008
 * Time: 2:07:08 PM
 * To change this template use File | Settings | File Templates.
 */
class Relation {
    static searchable = {
        except=["errors", "__operation_class__", "__dynamic_property_storage__"]
    };
    Long id;
    Long version;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;
    String rsOwner = "p"
    Long objectId;
    Long reverseObjectId;
    String name = "";
    String source;
    String reverseName = "";
    org.springframework.validation.Errors errors ;
    Object __operation_class__;
    Object __dynamic_property_storage__;
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"]
    static relations = [:]
    static constraints = {
        objectId(key:["name", "reverseObjectId", "reverseName"]);
        errors(nullable:true)
        __operation_class__(nullable:true)
        __dynamic_property_storage__(nullable:true)
    }

    public String toString() {
        return "objectId:${objectId} reverseObjectId:${reverseObjectId} name:${name} reverseName:${reverseName}";
    }

}