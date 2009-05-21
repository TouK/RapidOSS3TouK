package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import java.lang.reflect.Method
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.reflection.CachedMethod
import com.ifountain.annotations.Description
import java.lang.reflect.Modifier

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

            operationClass.getMethods().each{Method method->
                if(!domainMethods.containsKey(method.name))
                {
                    Description annotation = method.getAnnotation(Description);
                    operationList.add(new OperationMethod(name:method.name, parameters:method.getParameterTypes(), isStatic:Modifier.isStatic(method.modifiers), isPublic:!Modifier.isPrivate(method.modifiers),returnType:method.getReturnType(), description:annotation?annotation.value():""));
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
    String description;
    boolean isStatic;
    boolean isPublic;

    public void setProperty(String s, Object o) {
    }

    public String toString() {
        return name; //To change body of overridden methods use File | Settings | File Templates.
    }


}