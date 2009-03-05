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
package connection

import datasource.RapidInsightDatasource;

public class RapidInsightConnection extends Connection{
   
    
    
    static searchable = {
        except = ["rapidInsightDatasources"];

    };
    static cascaded = ["rapidInsightDatasources":true]
    static datasources = [:]


    String baseUrl ="";

    String userPassword ="";

    String username ="";
    String connectionClass = "connection.RapidInsightConnectionImpl";
    List rapidInsightDatasources = [];
    org.springframework.validation.Errors errors ;
    static relations = [
            rapidInsightDatasources:[isMany:true, reverseName:"connection", type:RapidInsightDatasource]
    ]
    static constraints={
    baseUrl(blank:true,nullable:true)

     userPassword(blank:true,nullable:true)

     username(blank:true,nullable:true)


    }

    static transients = [];
}
