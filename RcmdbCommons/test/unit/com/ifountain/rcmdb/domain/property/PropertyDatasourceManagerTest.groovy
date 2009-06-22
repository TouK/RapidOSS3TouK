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
package com.ifountain.rcmdb.domain.property

import com.ifountain.rcmdb.test.util.RapidCmdbMockTestCase
import groovy.xml.MarkupBuilder
import com.ifountain.rcmdb.domain.generation.ModelGenerator

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 12, 2008
 * Time: 10:17:52 AM
 * To change this template use File | Settings | File Templates.
 */
class PropertyDatasourceManagerTest extends RapidCmdbMockTestCase
{
    public void setUp()
    {
        super.setUp();
        if(new File(".").getCanonicalPath().endsWith("RapidModules"))
        {
            ModelGenerator.getInstance().initialize (null, null, "RcmdbCommons");
        }
        else
        {
            ModelGenerator.getInstance().initialize (null, null, ".");
        }
    }
    public void testGetDatasourceProperties()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]],
                [name:"ds2", keyProperties:[[name:"prop1"]]]
        ];
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"],
        [name:"prop4", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds2"]];
        def modelClassText = ModelGenerator.getInstance().getModelText(createModel(modelName, null, datasources, properties));
        def modelClass = gcl.parseClass(modelClassText);
        println modelClassText
        PropertyDatasourceManagerBean manager = new PropertyDatasourceManagerBean();
        manager.afterPropertiesSet();
        def propsForDs1 = manager.getDatasourceProperties(modelClass, "ds1");
        assertEquals (2, propsForDs1.size());
        assertTrue (propsForDs1.contains(new DatasourceProperty(name:"prop2", nameInDatasource:"prop2", datasourceName:"ds1",type:String)));
        assertTrue (propsForDs1.contains(new DatasourceProperty(name:"prop3", nameInDatasource:"prop3", datasourceName:"ds1",type:String)));

        def propsForDs2 = manager.getDatasourceProperties(modelClass, "ds2");
        assertEquals (1, propsForDs2.size());
        assertTrue (propsForDs2.contains(new DatasourceProperty(name:"prop4", nameInDatasource:"prop4", datasourceName:"ds2",type:String)));

        def propsForDs1ReRequested = manager.getDatasourceProperties(modelClass, "ds1");
        assertSame (propsForDs1, propsForDs1ReRequested);

        def propsForDs2ReRequested = manager.getDatasourceProperties(modelClass, "ds2");
        assertSame (propsForDs2, propsForDs2ReRequested);
    }


    public void testGetDatasourceKeys()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"], [name:"prop2"]]],
                [name:"ds2", keyProperties:[[name:"prop3"]]],
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"]];
        def modelClassText = ModelGenerator.getInstance().getModelText(createModel(modelName, null, datasources, properties));
        def modelClass = gcl.parseClass(modelClassText);

        PropertyDatasourceManagerBean manager = new PropertyDatasourceManagerBean();
        manager.afterPropertiesSet();
        def keysForDs1 = manager.getDatasourceKeys(modelClass, "ds1");
        assertEquals (2, keysForDs1.size());
        assertTrue (keysForDs1.contains(new DatasourceProperty(name:"prop1", nameInDatasource:"prop1", datasourceName:"ds1",type:String)));
        assertTrue (keysForDs1.contains(new DatasourceProperty(name:"prop2", nameInDatasource:"prop2", datasourceName:"ds1",type:String)));

        def keysForDs2 = manager.getDatasourceKeys(modelClass, "ds2");
        assertEquals (1, keysForDs2.size());
        assertTrue (keysForDs2.contains(new DatasourceProperty(name:"prop3", nameInDatasource:"prop3", datasourceName:"ds2",type:String)));

        def keysForDs1ReRequested = manager.getDatasourceKeys(modelClass, "ds1");
        assertSame (keysForDs1, keysForDs1ReRequested);

        def keysForDs2ReRequested = manager.getDatasourceKeys(modelClass, "ds2");
        assertSame (keysForDs2, keysForDs2ReRequested);
    }


    public void testGetMappedDatasourceName()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"], [name:"prop2"]], mappedName:"aDsName"],
                [name:"ds2", keyProperties:[[name:"prop3"]], mappedNameProperty:"prop1"],
                [name:"ds3", keyProperties:[[name:"prop3"]]],
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"]];
        def modelClassText = ModelGenerator.getInstance().getModelText(createModel(modelName, null, datasources, properties));
        def modelClass = gcl.parseClass(modelClassText);

        PropertyDatasourceManagerBean manager = new PropertyDatasourceManagerBean();
        manager.afterPropertiesSet();
        def mappedNameForDs1 = manager.getMappedDatasourceName(modelClass, "ds1");
        def mappedNameForDs2 = manager.getMappedDatasourceName(modelClass, "ds2");
        def mappedNameForDs3 = manager.getMappedDatasourceName(modelClass, "ds3");
        assertEquals ("aDsName", mappedNameForDs1.name);
        assertFalse (mappedNameForDs1.isProperty);
        assertEquals ("prop1", mappedNameForDs2.name);
        assertTrue (mappedNameForDs2.isProperty);
        assertEquals ("ds3", mappedNameForDs3.name);
        assertFalse (mappedNameForDs3.isProperty);
    }

    public void testIsFederated()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]],
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"]]
        def modelClassText = ModelGenerator.getInstance().getModelText(createModel(modelName, null, datasources, properties));
        def modelClass = gcl.parseClass(modelClassText);

        PropertyDatasourceManagerBean manager = new PropertyDatasourceManagerBean();
        manager.afterPropertiesSet();
        assertFalse(manager.isFederated(modelClass, "prop1"));
        assertTrue(manager.isFederated(modelClass, "prop2"));
    }

    public void testIsLazy()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]],
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1", lazy:true],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1", lazy:false]]
        def modelClassText = ModelGenerator.getInstance().getModelText(createModel(modelName, null, datasources, properties));
        def modelClass = gcl.parseClass(modelClassText);

        PropertyDatasourceManagerBean manager = new PropertyDatasourceManagerBean();
        manager.afterPropertiesSet();
        assertFalse(manager.isLazy(modelClass, "prop1"));
        assertTrue(manager.isLazy(modelClass, "prop2"));
        assertFalse(manager.isLazy(modelClass, "prop3"));
    }

    public void testGetDatasourcePropertiesWithParentClass()
    {
         def parentModelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]]
        ]
        def parentModelProperties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"]];
        def parentModelClassText = ModelGenerator.getInstance().getModelText(createModel(parentModelName, null, datasources, parentModelProperties));
        def parentModelClass = gcl.parseClass(parentModelClassText);

        def childModelName = "Model2"
        def childModelProperties = [[name:"prop4", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:"ds1"]];
        def childModelClassText = ModelGenerator.getInstance().getModelText(createModel(childModelName, parentModelName, [], childModelProperties));
        def childModelClass = gcl.parseClass(childModelClassText);

        PropertyDatasourceManagerBean manager = new PropertyDatasourceManagerBean();
        manager.afterPropertiesSet();
        def props = manager.getDatasourceProperties(childModelClass, "ds1");
        assertEquals (3, props.size());
        assertTrue (props.contains(new DatasourceProperty(name:"prop2", nameInDatasource:"prop2", datasourceName:"ds1",type:String)));
        assertTrue (props.contains(new DatasourceProperty(name:"prop3", nameInDatasource:"prop3", datasourceName:"ds1",type:String)));
        assertTrue (props.contains(new DatasourceProperty(name:"prop4", nameInDatasource:"prop4", datasourceName:"ds1",type:String)));
    }

    public void testGetDatasourcePropertiesWithDynamicProperty()
    {
        def modelName = "Model1"
        def datasources = [
                [name:"RCMDB", keyProperties:[[name:"prop1"]]],
                [name:"ds1", keyProperties:[[name:"prop1"]]]
        ]
        def properties = [[name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"],
        [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasourceProperty:"ds1"],
        [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasourceProperty:"ds1"]];
        def modelClassText = ModelGenerator.getInstance().getModelText(createModel(modelName, null, datasources, properties));
        def modelClass = gcl.parseClass(modelClassText);

        PropertyDatasourceManagerBean manager = new PropertyDatasourceManagerBean();
        manager.afterPropertiesSet();
        def props = manager.getDatasourceProperties(modelClass, "ds1");
        assertEquals (2, props.size());
        assertTrue (props.contains(new DatasourceProperty(name:"prop2", nameInDatasource:"prop2", datasourceName:"ds1",type:String)));
        assertTrue (props.contains(new DatasourceProperty(name:"prop3", nameInDatasource:"prop3", datasourceName:"ds1",type:String)));

        def propsReRequested = manager.getDatasourceProperties(modelClass, "ds1");
        assertSame (props, propsReRequested);
    }


    def createModel(String name, String parentModelName, List datasources, List properties)
    {
        def model = new StringWriter();
        def modelbuilder = new MarkupBuilder(model);
        def modelMeta = [name:name];
        if(parentModelName)
        {
            modelMeta["parentModel"] = parentModelName;
        }
        modelbuilder.Model(modelMeta){
            modelbuilder.Datasources(){
                datasources.each{datasource->
                    def dsConfig = new HashMap(datasource);
                    dsConfig.remove ("keyProperties")
                    modelbuilder.Datasource(dsConfig){
                        datasource.keyProperties.each{Map keyPropConfig->
                            modelbuilder.Key(propertyName:keyPropConfig.name)
                        }
                    }
                }
            }

            modelbuilder.Properties(){
                properties.each{Map propConfig->
                    modelbuilder.Property(propConfig)
                }
            }
        }
        return model.toString();
    }
}