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
package com.ifountain.rcmdb.domain.generation

import model.Model
import groovy.xml.MarkupBuilder
import model.ModelProperty
import model.ModelDatasource
import model.ModelDatasourceKeyMapping
import model.ModelRelation

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 27, 2008
 * Time: 4:55:12 PM
 * To change this template use File | Settings | File Templates.
 */
class ModelGeneratorAdapter {
    def static generateModels(Collection models)
    {
        def modelXmls = [];
        models.each{Model model->
            StringWriter writer = new StringWriter();
            def modelBuilder = new MarkupBuilder(writer);
            modelBuilder.Model(name:model.name, parentModel:model.parentModel?model.parentModel.name:null, indexName:model.indexName, storageType:model.storageType)
            {
                modelBuilder.Properties(){
                    model.modelProperties.each{ModelProperty property->
                            def datasourceName = property.propertyDatasource?property.propertyDatasource.datasource.name:"";
                            def datasourcePropertyName =  property.propertySpecifyingDatasource?property.propertySpecifyingDatasource.name:"";
                            if(datasourceName == "" && datasourcePropertyName == "")
                            {
                                datasourceName = "RCMDB";
                            }
                            if(datasourcePropertyName == "")
                            {
                                modelBuilder.Property(name:property.name, type:property.type, defaultValue:property.defaultValue, datasource:datasourceName, lazy:property.lazy, nameInDatasource:property.nameInDatasource);
                            }
                            else
                            {
                                modelBuilder.Property(name:property.name, type:property.type, defaultValue:property.defaultValue, datasourceProperty:property.propertySpecifyingDatasource?property.propertySpecifyingDatasource.name:"", lazy:property.lazy, nameInDatasource:property.nameInDatasource);    
                            }
                    }
                }

                modelBuilder.Datasources(){
                    model.datasources.each{ModelDatasource datasource->
                            def dsConf = [name:datasource.datasource.name]
                            if(datasource.datasource.mappedName != null)
                            {
                                dsConf.mappedName = datasource.datasource.mappedName
                            }
                            else if(datasource.datasource.mappedNameProperty != null)
                            {
                                dsConf.mappedNameProperty = datasource.datasource.mappedNameProperty    
                            }
                            modelBuilder.Datasource(dsConf){
                                datasource.keyMappings.each{ModelDatasourceKeyMapping key->
                                    modelBuilder.Key(propertyName:key.property.name, nameInDatasource:key.nameInDatasource);                          
                                }
                            }
                    }
                }

                modelBuilder.Relations(){
                    model.fromRelations.each{ModelRelation relation->
                            modelBuilder.Relation(name:relation.firstName, reverseName:relation.secondName, toModel:relation.secondModel.name, cardinality:relation.firstCardinality, reverseCardinality:relation.secondCardinality, isOwner:true);
                    }

                    model.toRelations.each{ModelRelation relation->
                            modelBuilder.Relation(name:relation.secondName, reverseName:relation.firstName, toModel:relation.firstModel.name, cardinality:relation.secondCardinality, reverseCardinality:relation.firstCardinality, isOwner:false);
                    }
                }

            }
            modelXmls += writer.toString()
        }
        ModelGenerator.getInstance().generateModels(modelXmls)
    }
}