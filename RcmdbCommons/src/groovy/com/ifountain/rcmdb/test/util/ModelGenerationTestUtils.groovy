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
package com.ifountain.rcmdb.test.util

import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.domain.generation.ModelGenerator

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 3, 2008
 * Time: 11:40:42 PM
 * To change this template use File | Settings | File Templates.
 */
class ModelGenerationTestUtils {
    public static String base_Dir = "../testOutput/base"
    public static String temp_Dir = "../testOutput/temp"
    public static boolean isInitialized = false;
    //replacement paremeter is a list of list. It will replace the specified regular expression with specified text
    //for example replacement = [["constraints\\s{", "constraints{prop1(nullable:false)"]] 
    public static String getModelText(Map modelDefinitionProperties, List datasources, List modelProperties, List keyProperties, List relations, List replacements)
    {
        String modelXml = createModel(modelDefinitionProperties, datasources, modelProperties, keyProperties, relations);
        String modelString = getModelGenerator().getModelText(modelXml)
        replacements.each{List replacementInfo->
            String replacedText = replacementInfo[0];
            String replacement = replacementInfo[1];
            modelString = modelString.replaceAll (replacedText, replacement);
        }
        return modelString;
    }
    public static String getModelText(Map modelDefinitionProperties, List modelProperties, List keyProperties, List relations)
    {
        return getModelText(modelDefinitionProperties, [], modelProperties, keyProperties, relations);
    }
    public static String getModelText(Map modelDefinitionProperties, List datsources, List modelProperties, List keyProperties, List relations)
    {
        return getModelText(modelDefinitionProperties, datsources, modelProperties, keyProperties, relations, null);
    }
    private static ModelGenerator getModelGenerator()
    {
        if(!isInitialized)
        {
            if(new File(".").getCanonicalPath().endsWith("RapidModules"))
            {
                ModelGenerator.getInstance().initialize(base_Dir, temp_Dir, "RcmdbCommons")
            }
            else
            {
                ModelGenerator.getInstance().initialize(base_Dir, temp_Dir, ".")
            }
            isInitialized = true;
        }
        return ModelGenerator.getInstance();

    }
    def static createModel(Map modelDefinitionProperties, List datasources, List modelProperties, List keyProperties, List relations)
    {
        def model = new StringWriter();
        def modelbuilder = new MarkupBuilder(model);
        modelbuilder.Model(modelDefinitionProperties){
            modelbuilder.Datasources(){
                modelbuilder.Datasource(name:"RCMDB"){
                    keyProperties.each{Map keyPropConfig->
                        modelbuilder.Key(propertyName:keyPropConfig.name)
                    }
                }
                datasources.each{Map datasource->
                    def dsProps = new HashMap(datasource);
                    def keys = dsProps.remove("keys")
                    modelbuilder.Datasource(dsProps){
                        keys.each{keyPropConfig->
                            modelbuilder.Key(propertyName:keyPropConfig.propertyName, nameInDatasource:keyPropConfig.nameInDatasource)
                        }
                    }
                }
            }

            modelbuilder.Properties(){
                boolean isIdAdded = false;
                boolean isVersionAdded = false;
                modelProperties.each{Map propConfig->
                    modelbuilder.Property(propConfig)
                }
            }

            modelbuilder.Relations(){
                relations.each{
                    modelbuilder.Relation(it);
                }
            }

        }
        return model.toString();
    }

}