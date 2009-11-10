package search

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.compass.CompassTestObject
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.util.RapidStringUtilities

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Nov 10, 2009
* Time: 3:49:09 PM
* To change this template use File | Settings | File Templates.
*/
class SearchAsStringTest extends RapidCmdbWithCompassTestCase{
    public void testSearchAsString()
    {
        def models = initializePluginAndClasses();
        def parentModel1 = models.parent.add(keyProp:"parentObj1", numberProp:6, dateProp:new Date(System.currentTimeMillis()+1000));
        def parentModel2 = models.parent.add(keyProp:"parentObj2", numberProp:7, dateProp:new Date(System.currentTimeMillis()+2000));
        def childModel1 = models.child.add(keyProp:"childObj1", numberProp:6, dateProp:new Date(System.currentTimeMillis()+3000), childProp1:"childProp1Value1", childProp2:"childProp2Value1");
        def childModel2 = models.child.add(keyProp:"childObj2", numberProp:9, dateProp:new Date(System.currentTimeMillis()+4000), childProp1:"childProp1Value2", childProp2:"childProp2Value2");
        assertFalse (parentModel1.hasErrors());
        assertFalse (parentModel2.hasErrors());
        assertFalse (childModel1.hasErrors());
        assertFalse (childModel2.hasErrors());

        def searchResults = models.parent.searchAsString("alias:*");
        assertEquals (4, searchResults.total);
        assertEquals (4, searchResults.results.size());
        assertEquals (0, searchResults.offset);
        def propsMap = parentModel1.asStringMap()
        propsMap.alias = models.parent.name
        assertEquals (propsMap, searchResults.results[0]);
        propsMap = parentModel2.asStringMap()
        propsMap.alias = models.parent.name
        assertEquals (propsMap, searchResults.results[1]);
        propsMap = childModel1.asStringMap()
        propsMap.alias = models.child.name
        assertEquals (propsMap, searchResults.results[2]);
        propsMap = childModel2.asStringMap()
        propsMap.alias = models.child.name
        assertEquals (propsMap, searchResults.results[3]);

        //test with sorting
        searchResults = models.parent.searchAsString("alias:*", [sort:"id"]);
        assertEquals (4, searchResults.total);
        assertEquals (4, searchResults.results.size());
        assertEquals (0, searchResults.offset);
        propsMap = parentModel1.asStringMap()
        propsMap.alias = models.parent.name
        assertEquals (propsMap, searchResults.results[0]);
        propsMap = parentModel2.asStringMap()
        propsMap.alias = models.parent.name
        assertEquals (propsMap, searchResults.results[1]);
        propsMap = childModel1.asStringMap()
        propsMap.alias = models.child.name
        assertEquals (propsMap, searchResults.results[2]);
        propsMap = childModel2.asStringMap()
        propsMap.alias = models.child.name
        assertEquals (propsMap, searchResults.results[3]);

        // test with max
        searchResults = models.parent.searchAsString("alias:*", [sort:"id", max:2]);
        assertEquals (4, searchResults.total);
        assertEquals (2, searchResults.results.size());
        assertEquals (0, searchResults.offset);
        propsMap = parentModel1.asStringMap()
        propsMap.alias = models.parent.name
        assertEquals (propsMap, searchResults.results[0]);
        propsMap = parentModel2.asStringMap()
        propsMap.alias = models.parent.name
        assertEquals (propsMap, searchResults.results[1]);

        // test with max  and offset
        searchResults = models.parent.searchAsString("alias:*", [sort:"id", max:2, offset:1]);
        assertEquals (2, searchResults.results.size());
        assertEquals (4, searchResults.total);
        assertEquals (1, searchResults.offset);
        propsMap = parentModel2.asStringMap()
        propsMap.alias = models.parent.name
        assertEquals (propsMap, searchResults.results[0]);
        propsMap = childModel1.asStringMap()
        propsMap.alias = models.child.name
        assertEquals (propsMap, searchResults.results[1]);

        // test with different query
        searchResults = models.parent.searchAsString("alias:${RapidStringUtilities.exactQuery (models.child.name)}", [sort:"id", max:2, offset:1]);
        assertEquals (1, searchResults.results.size());
        assertEquals (2, searchResults.total);
        assertEquals (1, searchResults.offset);
        propsMap = childModel2.asStringMap()
        propsMap.alias = models.child.name
        assertEquals (propsMap, searchResults.results[0]);


        // test with property list
        def propList = ["childProp1", "childProp2", "keyProp", "undefinedProp"]
        searchResults = models.parent.searchAsString("alias:*", [sort:"id", propertyList:propList]);
        assertEquals (4, searchResults.total);
        assertEquals (4, searchResults.results.size());
        assertEquals (0, searchResults.offset);
        propsMap = parentModel1.asStringMap(propList)
        propsMap.alias = models.parent.name
        assertEquals (propsMap, searchResults.results[0]);
        propsMap = parentModel2.asStringMap(propList)
        propsMap.alias = models.parent.name
        assertEquals (propsMap, searchResults.results[1]);
        propsMap = childModel1.asStringMap(propList)
        propsMap.alias = models.child.name
        assertEquals (propsMap, searchResults.results[2]);
        propsMap = childModel2.asStringMap(propList)
        propsMap.alias = models.child.name
        assertEquals (propsMap, searchResults.results[3]);

    }

    private Map initializePluginAndClasses()
    {
        def parentModelName = "ParentModel";
        def childModelName = "ChildModel";
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];
        def dateProp = [name: "dateProp", type: ModelGenerator.DATE_TYPE];
        def numberProp = [name: "numberProp", type: ModelGenerator.DATE_TYPE];
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE, blank: false];
        def childProp1 = [name: "childProp1", type: ModelGenerator.STRING_TYPE, blank: false];
        def childProp2 = [name: "childProp2", type: ModelGenerator.STRING_TYPE, blank: false];

        def parentModelMetaProps = [name: parentModelName]
        def childModelMetaProps = [name: childModelName, parentModel: parentModelName]
        def modelProps = [keyProp, prop1];
        def keyPropList = [keyProp];
        String parentModelString = ModelGenerationTestUtils.getModelText(parentModelMetaProps, [], modelProps, keyPropList, [])
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, [], [childProp1, childProp2], [], [])
        this.gcl.parseClass(parentModelString + childModelString);
        Class parentModelClass = this.gcl.loadClass(parentModelName);
        Class childModelClass = this.gcl.loadClass(childModelName);
        initialize([parentModelClass, childModelClass], [])
        return [parent: parentModelClass, child: childModelClass];
    }
}