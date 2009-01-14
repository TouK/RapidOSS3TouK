package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import java.lang.reflect.Method
import org.codehaus.groovy.runtime.InvokerHelper

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 14, 2009
* Time: 3:24:34 PM
* To change this template use File | Settings | File Templates.
*/
public class GetOperationsMethod {
    private Class operationClass;
    private List operationList;
    private MetaClass mc;
    public GetOperationsMethod(MetaClass mc)
    {
        this.mc = mc;
        operationList = Collections.unmodifiableList([]);
    }

    public void setOperationClass(Class operationClass)
    {
        this.operationClass = operationClass;
        operationList = [];
        if(operationClass != null)
        {
            def domainMethods = [:]
            mc.getMethods().each{
                domainMethods[it.name] = it.name;
            }
            operationClass.getMetaClass().getMethods().each{method->
                if(!domainMethods.containsKey(method.name))
                {
                    operationList.add(new OperationMethod(name:method.name, parameters:method.getParameterTypes(), returnType:method.getReturnType()));
                }
            }
        }
        operationList = operationList.sort {it.name}
        operationList = Collections.unmodifiableList(operationList);
    }

    public List getOperations()
    {
        return operationList;
    }
    
}

class OperationMethod{
    String name;
    List parameters;
    Class returnType;

    public void setProperty(String s, Object o) {
    }
    
}