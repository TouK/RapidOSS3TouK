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

import com.ifountain.core.domain.annotations.*;

class RsEventJournal {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    
    Long eventId =0;
    
    Date rsTime =new Date(0);
    
    String eventName ="";
    
    String details ="";
    
    org.springframework.validation.Errors errors ;
    
    Long id ;
    
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    
    Object __operation_class__ ;
    
    Object __dynamic_property_storage__ ;
    
    
    static relations = [:]    
    
    static constraints={
    eventId(nullable:true)
        
     rsTime(nullable:true)
        
     eventName(blank:true,nullable:true)
        
     details(blank:true,nullable:true)
        
     errors(nullable:true)
        
     __operation_class__(nullable:true)
        
     __dynamic_property_storage__(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[id:${getProperty("id")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE

    public static String MESSAGE_CREATE = "Created event";
    public static String MESSAGE_UPDATE = "Updated event";






}
