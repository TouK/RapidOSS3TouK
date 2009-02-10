package com.ifountain.rcmdb.scripting
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.runtime.InvokerHelper
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 4, 2009
 * Time: 12:53:53 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractScriptOperation {
   public static final String OPERATION_PROPERTY_NAME = "__operation_class__";
   def domainObject;
   public Object getProperty(String propName)
   {
       def prop = this.metaClass.getMetaProperty(propName);
       if(prop != null && propName != "properties")
       {
           return prop.getProperty(this);
       }
       else
       {
           return domainObject.getProperty(propName);
       }
   }

   public Map getProperties()
   {
       domainObject.getProperty("properties");
   }

   public void setProperty(String propName, Object value)
   {
           def prop = AbstractScriptOperation.metaClass.getMetaProperty(propName);
           if(prop != null && propName != "properties")
           {
               prop.setProperty(this, value);
           }
           else
           {
               domainObject.setProperty(propName, value);
           }
   }

//   public Object methodMissing(String methodName, Object args)
//   {
//       def argsInList = InvokerHelper.asList(args)
//       def types = [];
//       argsInList.each{
//           types += it.class;
//       }
//       if(domainObject.metaClass.getMetaMethod(methodName, types as Object[]) != null)
//       {
//           return domainObject.invokeMethod(methodName, args);
//       }
//
//       throw new MissingMethodException (methodName,  this.class, args);
//
//   }

}