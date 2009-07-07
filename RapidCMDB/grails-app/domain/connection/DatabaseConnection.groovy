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

import datasource.DatabaseDatasource
import datasource.SingleTableDatabaseDatasource;
public class DatabaseConnection extends Connection{

    static searchable = {
        except = ["singleTableDatabaseDatasources", "databaseDatasources"];
    };
    static cascaded = ["singleTableDatabaseDatasources":true, "databaseDatasources":true]
    static datasources = [:]

    String connectionClass = "connection.DatabaseConnectionImpl";
    String url ="";
    
    String userPassword ="";
    
    String username ="";
    
    String driver ="";
    List singleTableDatabaseDatasources = [];
    List databaseDatasources = [];
    org.springframework.validation.Errors errors ;
    static relations = [
            singleTableDatabaseDatasources:[isMany:true, reverseName:"connection", type:SingleTableDatabaseDatasource],
            databaseDatasources:[isMany:true, reverseName:"connection", type:DatabaseDatasource]
    ]
    static constraints={
        url(blank:false)
        userPassword(blank:true,  nullable:true)
        username(blank:false)
        driver(blank:false,nullable:false, validator:{val, obj ->            
            if(val!=null )
            {
                try
                {
                    Class.forName(val)
                }
                catch(ClassNotFoundException e)
                {
                    org.apache.log4j.Logger.getRootLogger().warn("[DatabaseConnection]: Error in  domain constraint, database driver does not exist");
                    return 'database.driver.does.not.exist';
                }
            }
        })
    }

    static transients = [];
}
