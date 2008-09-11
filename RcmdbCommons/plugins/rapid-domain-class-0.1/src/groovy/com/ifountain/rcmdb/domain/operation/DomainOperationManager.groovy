package com.ifountain.rcmdb.domain.operation
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 8, 2008
 * Time: 5:04:32 PM
 * To change this template use File | Settings | File Templates.
 */
class DomainOperationManager {
    public static final String OPERATION_SUFFIX = "Operations";
    Class domainClass;
    String operationsDirectory;
    Class operationClass;
    Map operationClassMethods = [:];
    public DomainOperationManager(Class domainClass, String operationsDirectory)
    {
        this.operationsDirectory = operationsDirectory;
        this.domainClass = domainClass;
    }


    public Class loadOperation()
    {
        def operationFile = getOperationFile()
        if(operationFile.exists())
        {
            def operationName = domainClass.name+OPERATION_SUFFIX;
            def gcl = new GroovyClassLoader();
            gcl.addClasspath (operationsDirectory);
            Class cls = null;
            try
            {
                cls = gcl.loadClass (operationName);
            }
            catch(Throwable t)
            {
                throw DomainOperationLoadException.compileException(t)   
            }
            if(!AbstractDomainOperation.isAssignableFrom(cls))
            {
                throw DomainOperationLoadException.shouldInheritAbstractDomainOperation();   
            }
            operationClass = cls;
            operationClassMethods.clear();
            operationClass.metaClass.methods.each{MetaMethod method->
                operationClassMethods[method.name] = method.name;
            };
            return operationClass;
        }
        else{
            throw DomainOperationLoadException.operationFileDoesnotExist(operationFile.path);
        }
    }

    private String getOperationClassName()
    {
        return domainClass.name+OPERATION_SUFFIX
    }

    public File getOperationFile()
    {
        def className = getOperationClassName();
        def fileName = className.replaceAll("\\.", "/")
        return new File("$operationsDirectory/${fileName}.groovy")
    }
}