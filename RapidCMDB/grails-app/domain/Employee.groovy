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

class Employee  extends Person {  

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["manages", "prevEmp", "nextEmp"];
    };
    static datasources = [:]

    
    String dept ="";
    
    Long salary =1000;
    
    Employee prevEmp ;
    
    Employee nextEmp ;
    List manages = [];
    org.springframework.validation.Errors errors ;

    static relations = [manages:[isMany:true, type:Team, reverseName:"managedBy"],
            prevEmp:[isMany:false, type:Employee, reverseName:"nextEmp"],
            nextEmp:[isMany:false, type:Employee, reverseName:"prevEmp"],

    ]
    static constraints={
    dept(blank:true,nullable:true)
        
     salary(blank:true,nullable:true)
        
     prevEmp(nullable:true)
        
     nextEmp(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE
    
}
