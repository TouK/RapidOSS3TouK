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
    public static String getModelText(Map modelDefinitionProperties, List modelProperties, List keyProperties, List relations)
    {
        String modelXml = createModel(modelDefinitionProperties, modelProperties, keyProperties, relations);
        return getModelGenerator().getModelText(modelXml)
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
    def static createModel(Map modelDefinitionProperties, List modelProperties, List keyProperties, List relations)
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