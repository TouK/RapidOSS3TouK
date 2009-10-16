import connection.DatabaseConnection
import datasource.SingleTableDatabaseDatasource

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Pinar Kinikoglu
 * Date: Apr 3, 2008
 * Time: 9:10:41 AM
 * To change this template use File | Settings | File Templates.
 */
def conn1 = DatabaseConnection.get(name:"mysql");
if(conn1 == null){
    conn1 = DatabaseConnection.add(name:"mysql", driver:"com.mysql.jdbc.Driver",
            url:"jdbc:mysql://192.168.1.100/test", username:"root", userPassword:"root");
}

def ds1 = SingleTableDatabaseDatasource.get(name:"ds1");
if (ds1 == null){
    ds1 = SingleTableDatabaseDatasource.add(connection:conn1, name:"ds1", tableName:"table1", tableKeys:"prop0");
}
