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

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import org.apache.log4j.Logger
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 25, 2008
 * Time: 9:17:31 AM
 * To change this template use File | Settings | File Templates.
 */
class AsMapMethodTest extends RapidCmdbTestCase{
    public void testAsMap()
    {
        def modelName = "BookModel";
        def keyProp = [name:"name", type:ModelGenerator.STRING_TYPE, blank:false];

        def modelMetaProps = [name:modelName]
        def keyPropList = [keyProp];
        def modelProps = [keyProp];
        modelProps.add([name:"pagecount", type:ModelGenerator.NUMBER_TYPE, blank:false]);
        modelProps.add([name:"isForChildren", type:ModelGenerator.BOOLEAN_TYPE, blank:false]);



        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(modelString);
        Class modelClass = gcl.loadClass(modelName);

        def propList=[]
        propList.add(new RapidDomainClassProperty(name:"name",isRelation:false,isKey:true,isOperationProperty:false))
        propList.add(new RapidDomainClassProperty(name:"pagecount",isRelation:false,isKey:false,isOperationProperty:false))
        propList.add(new RapidDomainClassProperty(name:"isForChildren",isRelation:false,isKey:false,isOperationProperty:false))

        modelClass.metaClass.'static'.getPropertiesList = {->
            return propList
        };

        def instance = modelClass.newInstance();
        instance.name="testcraft"
        instance.pagecount=888
        instance.isForChildren=true

        

        AsMapMethod asMap = new AsMapMethod(modelClass.metaClass,modelClass, Logger.getRootLogger());
        def instanceAsMap=asMap.invoke(instance,null);
        

        assertEquals(propList.size(),instanceAsMap.size())
        for(prop in propList){
            assertTrue(instanceAsMap.containsKey(prop.name))
        }
        assertEquals(instanceAsMap.name,"testcraft")
        assertEquals(instanceAsMap.pagecount,888)
        assertEquals(instanceAsMap.isForChildren,true)
    }

    public void testAsMapExcludesRelationAndOperationProperties()
    {
        def modelName = "BookModel";
        def keyProp = [name:"name", type:ModelGenerator.STRING_TYPE, blank:false];

        def modelMetaProps = [name:modelName]
        def keyPropList = [keyProp];
        def modelProps = [keyProp];

        def excludeProps=[]
        excludeProps.add([name:"pagecount", type:ModelGenerator.NUMBER_TYPE, blank:false]);
        excludeProps.add([name:"isForChildren", type:ModelGenerator.BOOLEAN_TYPE, blank:false]);

        modelProps.addAll(excludeProps);


        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(modelString);
        Class modelClass = gcl.loadClass(modelName);

        def propList=[]
        propList.add(new RapidDomainClassProperty(name:"name",isRelation:false,isKey:true,isOperationProperty:false))
        propList.add(new RapidDomainClassProperty(name:"pagecount",isRelation:true,isKey:false,isOperationProperty:false))
        propList.add(new RapidDomainClassProperty(name:"isForChildren",isRelation:false,isKey:false,isOperationProperty:true))

        modelClass.metaClass.'static'.getPropertiesList = {->
            return propList
        };

        def instance = modelClass.newInstance();
        instance.name="testcraft"
        instance.pagecount=888
        instance.isForChildren=true

        

        AsMapMethod asMap = new AsMapMethod(modelClass.metaClass,modelClass, Logger.getRootLogger());
        def instanceAsMap=asMap.invoke(instance,null);


        assertEquals(propList.size()-excludeProps.size(),instanceAsMap.size())
        for(prop in propList){
            if(!prop.isRelation && !prop.isOperationProperty){
                assertTrue(instanceAsMap.containsKey(prop.name))
            }
            else{
                if(instanceAsMap.containsKey(prop.name))
                {
                    fail("Relation or Operation Property found in default as map results");
                }                    
            }
        }
        assertEquals(instanceAsMap.name,"testcraft")
        
    }
    public void testAsMapWithProperties()
    {
        def modelName = "BookModel";
        def keyProp = [name:"name", type:ModelGenerator.STRING_TYPE, blank:false];

        def modelMetaProps = [name:modelName]
        def keyPropList = [keyProp];
        def modelProps = [keyProp];
        modelProps.add([name:"pagecount", type:ModelGenerator.NUMBER_TYPE, blank:false]);
        modelProps.add([name:"isForChildren", type:ModelGenerator.BOOLEAN_TYPE, blank:false]);



        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(modelString);
        Class modelClass = gcl.loadClass(modelName);

        def propList=[]
        propList.add(new RapidDomainClassProperty(name:"name",isRelation:false,isKey:true,isOperationProperty:false))
        propList.add(new RapidDomainClassProperty(name:"pagecount",isRelation:true,isKey:false,isOperationProperty:false))
        propList.add(new RapidDomainClassProperty(name:"isForChildren",isRelation:false,isKey:false,isOperationProperty:true))

        modelClass.metaClass.'static'.getPropertiesList = {->
            return propList
        };

        def instance = modelClass.newInstance();
        instance.name="testcraft"
        instance.pagecount=888
        instance.isForChildren=true

        

        AsMapMethod asMap = new AsMapMethod(modelClass.metaClass,modelClass, Logger.getRootLogger());
        def requestedProps=["name","pagecount"]
        def instanceAsMap=asMap.invoke(instance,requestedProps);

        assertEquals(requestedProps.size(),instanceAsMap.size())
        for(prop in requestedProps){
            assertTrue(instanceAsMap.containsKey(prop))
        }
        assertEquals(instanceAsMap.name,"testcraft")
        assertEquals(instanceAsMap.pagecount,888)
        
    }
   

    
}