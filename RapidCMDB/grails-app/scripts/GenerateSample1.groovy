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
 * Date: Mar 28, 2008
 * Time: 2:28:03 PM
 * To change this template use File | Settings | File Templates.
 */
import model.*;
import connection.*;
import datasource.*;
import com.ifountain.domain.ModelGenerator;

def dsConn = DatabaseConnection.findByName("mysql");
if(dsConn == null){
    dsConn = new DatabaseConnection(name:"mysql", driver:"com.mysql.jdbc.Driver",
            url:"jdbc:mysql://192.168.1.100/test", username:"root", password:"root").save();
}

// Generate Service model without relations
def serviceDs = SingleTableDatabaseDatasource.findByName("ServiceDs");
if (serviceDs == null){
    serviceDs = SingleTableDatabaseDatasource.add(connection:dsConn, name:"ServiceDs", tableName:"services", tableKeys:"name").save();
}

def serviceModel = Model.findByName("Service");
if (serviceModel != null){
    serviceModel.delete(flush:true);
}
serviceModel = Model.add(name:"Service");

def rcmdbDs = RCMDBDatasource.findByName("RCMDB");
def rcmdbDatasource = ModelDatasource.add(datasource:rcmdbDs, model:serviceModel, master:true);
def keyprop1 = ModelProperty.add(name:"name", type:ModelProperty.stringType, model:serviceModel, propertyDatasource:rcmdbDatasource, blank:false, lazy:false)
rcmdbDatasource.addToKeyMappings(new ModelDatasourceKeyMapping(property:keyprop1, nameInDatasource:"name"));
rcmdbDatasource.save(flush:true);

serviceModel.addToModelProperties(new ModelProperty(name:"manager", type:ModelProperty.stringType, propertyDatasource:rcmdbDatasource, blank:false, lazy:false));
serviceModel.addToModelProperties(new ModelProperty(name:"status", type:ModelProperty.stringType, propertyDatasource:rcmdbDatasource, blank:false, lazy:false));
serviceModel = serviceModel.save(flush:true);
serviceModel.refresh();

/*
// Generate Customer model without relations
def customerDs = SingleTableDatabaseDatasource.findByName("CustomerDs");
if (customerDs == null){
    customerDs = SingleTableDatabaseDatasource.add(connection:dsConn, name:"customerDs", tableName:"customers", tableKeys:"name").save();
}

def customerModel = Model.findByName("Customer");
if (customerModel != null){
    customerModel.delete(flush:true);
}
customerModel = Model.add(name:"Customer");
customerModel.addToDatasources(rcmdbDatasource);

customerDatasource = ModelDatasource.add(datasource:customerDs, master:false);
customerDatasource.addToKeyMappings(keyMapping1);
customerDatasource.save(flush:true);

customerModel.addToDatasources(customerDatasource);
customerModel.addToModelProperties(keyprop1);
customerModel.addToModelProperties(ModelProperty.create(name:"accountmanager", type:ModelProperty.stringType, propertyDatasource:customerDatasource, blank:false, lazy:false), nameInDs:"manager");
customerModel.save(flush:true);

ModelGenerator.getInstance().generateModel(serviceModel);
ModelGenerator.getInstance().generateModel(customerModel);
*/
//ModelGenerator.getInstance().generateModel(serviceModel);
return "success";