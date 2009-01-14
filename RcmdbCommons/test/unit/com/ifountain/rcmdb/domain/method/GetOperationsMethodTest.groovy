package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 14, 2009
* Time: 3:05:26 PM
* To change this template use File | Settings | File Templates.
*/
class GetOperationsMethodTest extends RapidCmdbTestCase{
    public void testGetOperations()
    {
        String operationClassName = "${GetOperationsMethodTest.simpleName}Operation1";
        def methods = [[name:"method1", params:[String, Long, List]],
        [name:"method2", params:[], returnType:String],
        [name:"method3", params:[String], returnType:String]
        ]
        def methodText = "";
        methods.each{Map method->
            def name = method.name;
            def params = method.params;
            def returnType = method.returnType;
            methodText += returnType?returnType.name:"def";
            methodText +=" ${name}(";
            if(params)
            {
                int number = 0;
                params.each{
                    methodText += it.name+" p${number++},"
                }
                methodText = methodText.substring(0,methodText.length()-1);
            }
            methodText += "){}\n"

        }
        def operationsClassText = """
        class ${operationClassName}{
            ${methodText}
        }
        """
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class operationClass = gcl.parseClass(operationsClassText);
        Class domainClass = gcl.parseClass("class DomainClass{}");

        //Test with null domain class
        GetOperationsMethod method = new GetOperationsMethod(domainClass.metaClass);
        List operationList = method.getOperations();
        assertEquals (0, operationList.size());

        try
        {
            operationList.add("asdasd");
            fail("Should throw exception. It could not be modified");
        }
        catch(UnsupportedOperationException e)
        {
        }

        //Test with domain class
        method.setOperationClass (operationClass);
        operationList = method.getOperations();
        assertEquals (3, operationList.size());
        for(int i=0; i < methods.size(); i++)
        {
            def methodExpectedProps = methods[i];
            assertEquals (methodExpectedProps.name, operationList[i].name);
            for(int j=0; j < methodExpectedProps.params.size(); j++)
            {
                assertEquals(methodExpectedProps.params[j].name, operationList[i].parameters[j].name);
            }
            assertEquals (methodExpectedProps.returnType?methodExpectedProps.returnType:Object, operationList[i].returnType);
        }
        try
        {
            operationList.add("asdasd");
            fail("Should throw exception. It could not be modified");
        }
        catch(UnsupportedOperationException e)
        {
        }

        //Test setting domain class to null
        method.setOperationClass (null);
        operationList = method.getOperations();
        assertEquals (0, operationList.size());
        try
        {
            operationList.add("asdasd");
            fail("Should throw exception. It could not be modified");
        }
        catch(UnsupportedOperationException e)
        {
        }
    }
}