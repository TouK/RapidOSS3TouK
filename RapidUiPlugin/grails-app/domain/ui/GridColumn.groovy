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
 * Time: 2:44:33 PM
 */
class GridColumn {
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = ["RCMDB": ["master": true, "keys": ["attributeName": ["nameInDs": "attributeName"]]]]

    Long id;
    Long version;

    String rsOwner = "p";

    Long columnIndex = 0;
    String attributeName = "";
    String header = "";
    Long width = 0;
    GridView gridView;
    Long gridViewId = 0;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;

    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;

    static constraints = {
        attributeName(blank: false, nullable: false, key: ["gridViewId"])
        header(blank:true, nullable: true)
        columnIndex(nullable: true)
        width(nullable: true)
        __operation_class__(nullable:true)
        __dynamic_property_storage__(nullable:true)
        errors(nullable:true)
    };
    static relations = [
            gridView:[isMany:false, reverseName:"gridColumns", type:GridView]
    ]

    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    String toString() {
        return "$attributeName";
    }
}