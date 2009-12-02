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
package ui
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 22, 2008
 * Time: 2:39:39 PM
 */
class GridView {
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = ["RCMDB": ["master": true, "keys": ["name": ["nameInDs": "name"]]]]

    String rsOwner = "p";

    String name = "";
    String defaultSortColumn = "";
    String sortOrder = "asc";
    String username = "";
    List gridColumns = [];
    Boolean isPublic = false;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;

    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;

    static constraints = {
        name(blank: false, nullable: false, key: ["username"], validator:{val, obj ->
           if(val.toLowerCase().trim() == "default"){
               return ['default.not.equal.message', val]
           }
        })
        username(blank: false, nullable: false)
        defaultSortColumn(blank:true, nullable: true)
        sortOrder(inList:["asc","desc"]);
        isPublic(nullable:false)
        __operation_class__(nullable:true)
        __dynamic_property_storage__(nullable:true)
        errors(nullable:true)
    };
    static relations = [
            gridColumns:[isMany:true, reverseName:"gridView", type:GridColumn]
    ]

    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    String toString() {
        return "$name";
    }
}