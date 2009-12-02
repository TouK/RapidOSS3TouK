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
 * Time: 2:47:12 PM
 * To change this template use File | Settings | File Templates.
 */
class RelationMethodDomainObject1 {
    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["rel1", "rel2", "rel3", "rel4", "errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static cascaded = ["rel2":true]
    static datasources = [:]
    Long id ;
    Long version ;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;
    RelationMethodDomainObject2 rel1;
    List rel2 = [];
    RelationMethodDomainObject2 rel3;
    List rel4 = [];

    RelationMethodDomainObject2 noOtherSideRel1;
    List noOtherSideRel2 = [];
    RelationMethodDomainObject2 noOtherSideRel3;
    List noOtherSideRel4 = [];

    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;
    static constraints={
     __operation_class__(nullable:true)
     __dynamic_property_storage__(nullable:true)
     errors(nullable:true)
     rel1(nullable:true)
     rel2(nullable:true)
     rel3(nullable:true)
     rel4(nullable:true)   
     noOtherSideRel1(nullable:true)
     noOtherSideRel2(nullable:true)
     noOtherSideRel3(nullable:true)
     noOtherSideRel4(nullable:true)   
    }
    static relations = [
            rel1:[type:RelationMethodDomainObject2, reverseName:"revRel1", isMany:false],
            rel2:[isMany:true, reverseName:"revRel2", type:RelationMethodDomainObject2],
            rel3:[isMany:false, reverseName:"revRel3", type:RelationMethodDomainObject2],
            rel4:[isMany:true, reverseName:"revRel4", type:RelationMethodDomainObject2],
            noOtherSideRel1:[isMany:false, type:RelationMethodDomainObject2],
            noOtherSideRel2:[isMany:true, type:RelationMethodDomainObject2],
            noOtherSideRel3:[isMany:false, type:RelationMethodDomainObject2],
            noOtherSideRel4:[isMany:true, type:RelationMethodDomainObject2]
    ]
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    //AUTO_GENERATED_CODE

    public boolean equals(Object obj) {
        if(obj instanceof RelationMethodDomainObject1)
        {
            return obj.id == id;
        }
        return super.equals(obj);
    }
}