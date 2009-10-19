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
package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 1, 2008
 * Time: 2:47:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class RelationMethodDomainObject2 {
     //AUTO_GENERATED_CODE
    static searchable = {
        except = ["revRel1", "revRel2", "revRel3", "revRel4", "errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = [:]
    String prop1 ="1";
    Long id ;
    Long version ;
    Date rsInsertedAt = new Date(0);
    Date rsUpdatedAt  = new Date(0);
    RelationMethodDomainObject1 revRel1;
    RelationMethodDomainObject1 revRel2;
    List revRel4 = [];
    List revRel3 = [];
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;
    static constraints={
    prop1(blank:true,nullable:true)
     __operation_class__(nullable:true)
     __dynamic_property_storage__(nullable:true)
     errors(nullable:true)
     revRel1(nullable:true)
     revRel2(nullable:true)
     revRel3(nullable:true)
     revRel4(nullable:true)
    }

    static relations = [revRel1:[type:RelationMethodDomainObject1, reverseName:"rel1", isMany:false],
            revRel2:[isMany:false, reverseName:"rel2", type:RelationMethodDomainObject1],
            revRel3:[isMany:true, reverseName:"rel3", type:RelationMethodDomainObject1],
            revRel4:[isMany:true, reverseName:"rel4", type:RelationMethodDomainObject1]
    ];
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    //AUTO_GENERATED_CODE

    public boolean equals(Object obj) {
        if(obj instanceof RelationMethodDomainObject2)
        {
            return obj.id == id;
        }
        return super.equals(obj);
    }
}