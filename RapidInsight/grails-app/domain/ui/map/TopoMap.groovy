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

class TopoMap
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "group"];
    };
    static datasources = ["RCMDB":["keys":["mapName":["nameInDs":"mapName"], "username":["nameInDs":"username"]]]]
    static cascaded =[:]

    String mapName ="";

    String username ="";
    String nodePropertyList="";
    String nodes="";
    String mapProperties="";
    String mapPropertyList="";

    Boolean isPublic = false;
    Long layout;
    String rsOwner = "p"
    Long id ;

    Long version ;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;

    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __dynamic_property_storage__ ;



    MapGroup group ;

    static relations = [
            group:[reverseName:"maps", isMany:false, type:MapGroup]
    ]
    static constraints={
    mapName(blank:false,nullable:false)
    mapProperties(blank:true,nullable:false)
    mapPropertyList(blank:true,nullable:false)
    nodePropertyList(blank:false,nullable:false)
    nodes(blank:false,nullable:false)


     username(blank:false,nullable:false,key:["mapName"])
      isPublic(nullable:true)
     __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)

     errors(nullable:true)

     group(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    public String toString()
    {
    	return "${getClass().getName()}[mapName:$mapName, username:$username]";
    }

    //AUTO_GENERATED_CODE
}