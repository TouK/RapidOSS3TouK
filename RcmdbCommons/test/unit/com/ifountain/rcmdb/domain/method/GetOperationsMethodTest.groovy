package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation

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
        def methods = [[name:"method1", params:[String, Long, List], isStatic:true, isPublic:true],
        [name:"method2", params:[], returnType:String, isStatic:false, isPublic:true],
        [name:"method3", params:[String], returnType:String, isStatic:false, isPublic:true]
        ]
        def methodText = "";
        methods.each{Map method->
            def name = method.name;
            def params = method.params;
            def returnType = method.returnType;
            methodText += returnType?returnType.name:"def ${method.isStatic?"static":""}";
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
        class ${operationClassName} extends ${AbstractDomainOperation.class.name}{
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
            assertEquals (methodExpectedProps.isStatic, operationList[i].isStatic);
            assertEquals (true, operationList[i].isPublic);
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

    public void testGetOperationsWithAnnotation()
    {
        String operationClassName = "${GetOperationsMethodTest.simpleName}Operation1";
        String description = """
        /*
        This is a comment
        */
        """
        def operationsClassText = """
        import com.ifountain.annotations.*;
        class ${operationClassName}{
            @Description(\"\"\"${description}\"\"\")
            def method1()
            {
            }

            def method2()
            {
            }
        }
        """
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class operationClass = gcl.parseClass(operationsClassText);
        Class domainClass = gcl.parseClass("class DomainClass{}");

        //Test with null domain class
        GetOperationsMethod getOperationsMethod = new GetOperationsMethod(domainClass.metaClass);
        getOperationsMethod.setOperationClass (operationClass);
        def operationList = getOperationsMethod.getOperations();
        assertEquals (2, operationList.size());
        OperationMethod method = operationList[0];
        assertEquals ("method1", method.name);
        assertEquals (0, method.parameters.size());
        assertEquals (Object.name, method.returnType.name);
        assertEquals (description, method.description);

        method = operationList[1];
        assertEquals ("method2", method.name);
        assertEquals (0, method.parameters.size());
        assertEquals (Object.name, method.returnType.name);
        assertEquals ("", method.description);

    }

    public void testGetOperationsWithParentClass()
    {
        String operationClassName = "${GetOperationsMethodTest.simpleName}Operation1";
        String parentClassName = "${GetOperationsMethodTest.simpleName}Operation1Parent";
        String description = """
        /*
        This is a comment
        */
        """
        def operationsClassText = """
        import com.ifountain.annotations.*;
        class ${operationClassName} extends ${parentClassName}{
            def method2()
            {
            }
        }
        class ${parentClassName}
        {
            @Description(\"\"\"${description}\"\"\")
            def method1()
            {
            }
            def method3()
            {
            }
        }
        """
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(operationsClassText);
        def operationClass = gcl.getLoadedClasses().findAll{it.name == operationClassName}[0];
        Class domainClass = gcl.parseClass("class DomainClass{}");

        //Test with null domain class
        GetOperationsMethod getOperationsMethod = new GetOperationsMethod(domainClass.metaClass);
        getOperationsMethod.setOperationClass (operationClass);
        def operationList = getOperationsMethod.getOperations();
        assertEquals (3, operationList.size());
        OperationMethod method = operationList[0];
        assertEquals ("method1", method.name);
        assertEquals (0, method.parameters.size());
        assertEquals (Object.name, method.returnType.name);
        assertEquals (description, method.description);

        method = operationList[1];
        assertEquals ("method2", method.name);
        assertEquals (0, method.parameters.size());
        assertEquals (Object.name, method.returnType.name);
        assertEquals ("", method.description);

        method = operationList[2];
        assertEquals ("method3", method.name);
        assertEquals (0, method.parameters.size());
        assertEquals (Object.name, method.returnType.name);
        assertEquals ("", method.description);

    }
}