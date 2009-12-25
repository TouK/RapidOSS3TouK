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
package com.ifountain.rcmdb.domain.operation
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 8, 2008
 * Time: 5:04:32 PM
 * To change this template use File | Settings | File Templates.
 */
class DomainOperationManager {
    public static final ABSTRACT_DOMAIN_OPERATION_METHODS = [:]
    static{
        AbstractDomainOperation.metaClass.methods.each{MetaMethod method->
            ABSTRACT_DOMAIN_OPERATION_METHODS[method.name] = method.name;
        };
    }
    public static final String OPERATION_SUFFIX = "Operations";
    Class domainClass;
    private ClassLoader parentClassLoader;
    private String operationsDirectory;
    private Class operationClass;
    private Map operationClassMethods = [:];
    private DomainOperationManager parentOperationManager;
    private Map defaultMethods;

    private static boolean enableLoadOperation=true;

    public DomainOperationManager(Class domainClass, String operationsDirectory, DomainOperationManager parentOperationManager, Map defaultMethods, ClassLoader parentClassLoader)
    {
        this.parentClassLoader = parentClassLoader;
        this.defaultMethods = defaultMethods;
        this.operationsDirectory = operationsDirectory;
        this.domainClass = domainClass;
        this.parentOperationManager = parentOperationManager;
    }

    public synchronized Class getOperationClass()
    {
        if(operationClass != null)
        {
            return operationClass;
        }
        if(parentOperationManager != null)
        {
            return parentOperationManager.getOperationClass();
        }
        else
        {
            return AbstractDomainOperation;
        }
    }
    public synchronized Map getOperationClassMethods()
    {
        if(operationClass != null)
        {
            return operationClassMethods;
        }
        else if(parentOperationManager != null)
        {
            return parentOperationManager.getOperationClassMethods();
        }
        else
        {
            return ABSTRACT_DOMAIN_OPERATION_METHODS;
        }
    }
    public synchronized static void disableLoadOperation()
    {
         enableLoadOperation=false;
    }
    public synchronized static void enableLoadOperation()
    {
         enableLoadOperation=true;
    }
    public synchronized Class loadOperation()
    {
        def operationFile = getOperationFile()
        if(!enableLoadOperation)
        {
            throw DomainOperationLoadException.operationLoadingIsDisabled(operationFile.path);
        }
        else if(operationFile.exists())
        {
            def operationName = domainClass.name+OPERATION_SUFFIX;
            GroovyClassLoader gcl = new GroovyClassLoader(parentClassLoader);
            gcl.addClasspath (operationsDirectory);           
            Class cls = null;
            try
            {
                cls = gcl.loadClass (operationName);
                if(cls == operationClass)
                {
                    throw DomainOperationLoadException.sameOperationClassIsLoaded();
                }
                defaultMethods.each{String methodName, Map methodConfig->
                    boolean isStatic = methodConfig.isStatic;
                    def method = methodConfig.method;
                    if(!isStatic)
                    cls.metaClass."${methodName}" = method;
                    else
                    cls.metaClass.'static'."${methodName}" = method;                    
                }
            }
            catch(DomainOperationLoadException ex)
            {
                throw ex;                
            }
            catch(Throwable t)
            {
                throw DomainOperationLoadException.compileException(t)   
            }
            if(!AbstractDomainOperation.isAssignableFrom(cls))
            {
                throw DomainOperationLoadException.shouldInheritAbstractDomainOperation();   
            }
            setOperationClass (cls);
            return operationClass;
        }
        else{
            throw DomainOperationLoadException.operationFileDoesnotExist(operationFile.path);
        }
    }

    public void setOperationClass(Class cls)
    {
        operationClass = cls;
        operationClassMethods.clear();
        operationClass.metaClass.methods.each{MetaMethod method->
            operationClassMethods[method.name] = method.name;
        };
    }

    private String getOperationClassName()
    {
        return domainClass.name+OPERATION_SUFFIX
    }

    public synchronized File getOperationFile()
    {
        def className = getOperationClassName();
        def fileName = className.replaceAll("\\.", "/")
        return new File("$operationsDirectory/${fileName}.groovy")
    }
}