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
package ui.map

import com.ifountain.core.domain.annotations.*;

class MapGroup {    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["maps","errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = ["RCMDB":["keys":["groupName":["nameInDs":"groupName"], "username":["nameInDs":"username"]]]]

    String rsOwner = "p"
    String username ="";

    String groupName ="";
    Boolean isPublic = false;
    Boolean expanded = false;

    Long id ;

    Long version ;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;

    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __dynamic_property_storage__ ;

    List maps =[];

    static relations = [maps:[reverseName:"group", isMany:true, type:TopoMap]]
    static constraints={
    username(blank:false,nullable:false)
    isPublic(nullable:true)
    expanded(nullable:true)

     groupName(blank:false,nullable:false,key:["username"])

     __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)

     errors(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    public String toString()
    {
    	return "${getClass().getName()}[groupName:$groupName, username:$username]";
    }

    //AUTO_GENERATED_CODE



}
