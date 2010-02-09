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

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.property.RelationUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.domain.cache.IdCache
import com.ifountain.rcmdb.domain.statistics.OperationStatistics

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 13, 2008
 * Time: 6:01:25 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoveAllMatchingsMethodTest  extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        FileUtils.deleteDirectory (new File(ModelGenerationTestUtils.temp_Dir))
        FileUtils.deleteDirectory (new File(ModelGenerationTestUtils.base_Dir))
    }

    public void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testRemoveAllMatchings()
    {
        def modelName = "Model";
        def relatedModelName = "RelatedModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false]
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];

        def relatedModelMetaProps = [name:relatedModelName]
        def modelMetaProps = [name:modelName]
        def modelProps = [keyProp,prop1];
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [rel1])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, modelProps, keyPropList, [revrel1])
        this.gcl.parseClass(modelString+relatedModelString);
        Class modelClass = this.gcl.loadClass(modelName);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        initialize([modelClass, relatedModelClass], [])
        def modelInstance1 = modelClass.'add'(keyProp:"model1Instance1", prop1:"group1");
        def modelInstance2 = modelClass.'add'(keyProp:"model1Instance2", prop1:"group1");
        def modelInstance3 = modelClass.'add'(keyProp:"model1Instance3", prop1:"group2");
        def relatedObject1 = relatedModelClass.'add'(keyProp:"relatedObjectInstance1", revrel1:[modelInstance1, modelInstance3]);
        def relatedObject2 = relatedModelClass.'add'(keyProp:"relatedObjectInstance2", revrel1:[modelInstance2]);
        assertFalse (modelInstance1.hasErrors());
        assertFalse (modelInstance2.hasErrors());
        assertFalse (modelInstance3.hasErrors());
        assertFalse (relatedObject1.hasErrors());
        assertFalse (relatedObject2.hasErrors());
        assertEquals (relatedObject1.id, modelInstance1.rel1[0].id);
        assertEquals (relatedObject2.id, modelInstance2.rel1[0].id);
        assertEquals (relatedObject1.id, modelInstance3.rel1[0].id);
        assertTrue (IdCache.get(modelInstance1.class, modelInstance1).exist());
        assertTrue (IdCache.get(modelInstance2.class, modelInstance2).exist());
        assertTrue (IdCache.get(modelInstance3.class, modelInstance3).exist());
        assertTrue (IdCache.get(modelInstance1.id).exist());
        assertTrue (IdCache.get(modelInstance2.id).exist());
        assertTrue (IdCache.get(modelInstance3.id).exist());

        OperationStatistics.getInstance().reset();
        modelClass.'removeAll'("prop1:group1");
        def stats=OperationStatistics.getInstance().getOperationStatisticsAsMap(OperationStatistics.REMOVE_ALL_OPERATION_NAME);
        println stats
        assertEquals(1,stats.global.NumberOfOperations);
        assertEquals(1,stats.Model.NumberOfOperations);
        

        assertNull (modelClass.'get'(keyProp:modelInstance1.keyProp));
        assertNull (modelClass.'get'(keyProp:modelInstance2.keyProp));
        assertNotNull (modelClass.'get'(keyProp:modelInstance3.keyProp));
        def objectIdsForInstance1 = RelationUtils.getRelatedObjectsIdsByObjectId(modelInstance1.id, "rel1", "revrel1");
        def objectIdsForInstance2 = RelationUtils.getRelatedObjectsIdsByObjectId(modelInstance2.id, "rel1", "revrel1");
        assertTrue (objectIdsForInstance1.isEmpty());
        assertTrue (objectIdsForInstance2.isEmpty());

        assertFalse(IdCache.get(modelInstance1.class, modelInstance1).exist());
        assertFalse (IdCache.get(modelInstance2.class, modelInstance2).exist());
        assertTrue (IdCache.get(modelInstance3.class, modelInstance3).exist());
        assertFalse (IdCache.get(modelInstance1.id).exist());
        assertFalse (IdCache.get(modelInstance2.id).exist());
        assertTrue (IdCache.get(modelInstance3.id).exist());

    }
    public void testRemoveAllMatchingsWithCascade()
    {
        def modelName = "Model";
        def relatedModelName = "RelatedModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false]
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:false];

        def relatedModelMetaProps = [name:relatedModelName]
        def modelMetaProps = [name:modelName]
        def modelProps = [keyProp,prop1];
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [rel1])
        def beforeClosure = StringUtils.substringBefore(modelString, "{");
        def afterClosure = StringUtils.substringAfter(modelString, "{");
        modelString = beforeClosure+"""{static cascaded = ["rel1":true]"""+afterClosure 
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, modelProps, keyPropList, [revrel1])
        this.gcl.parseClass(modelString+relatedModelString);
        Class modelClass = this.gcl.loadClass(modelName);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        initialize([modelClass, relatedModelClass], [])
        def modelInstance1 = modelClass.'add'(keyProp:"model1Instance1", prop1:"group1");
        def modelInstance3 = modelClass.'add'(keyProp:"model1Instance3", prop1:"group2");
        def relatedObject1 = relatedModelClass.'add'(keyProp:"relatedObjectInstance1", revrel1:[modelInstance1]);
        def relatedObject2 = relatedModelClass.'add'(keyProp:"relatedObjectInstance2", revrel1:[modelInstance3]);
        assertFalse (modelInstance1.hasErrors());
        assertFalse (modelInstance3.hasErrors());
        assertFalse (relatedObject1.hasErrors());
        assertFalse (relatedObject2.hasErrors());
        assertEquals (relatedObject1.id, modelInstance1.rel1.id);
        assertEquals (relatedObject2.id, modelInstance3.rel1.id);

        modelClass.'removeAll'("prop1:group1");
        assertNull (modelClass.'get'(keyProp:modelInstance1.keyProp));
        def modelInstance2FromRepo = modelClass.'get'(keyProp:modelInstance3.keyProp);
        assertEquals (relatedObject2.id, modelInstance2FromRepo.rel1.id);
        assertNull (relatedModelClass.'get'(keyProp:relatedObject1.keyProp));
        assertNotNull (relatedModelClass.'get'(keyProp:relatedObject2.keyProp));
        def objectIdsForInstance1 = RelationUtils.getRelatedObjectsIdsByObjectId(modelInstance1.id, "rel1", "revrel1");
        def objectIdsForInstance3 = RelationUtils.getRelatedObjectsIdsByObjectId(modelInstance3.id, "rel1", "revrel1");
        assertTrue (objectIdsForInstance1.isEmpty());
        assertFalse (objectIdsForInstance3.isEmpty());

    }
}