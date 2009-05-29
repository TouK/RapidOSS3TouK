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
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.IdGeneratorStrategyImpl
import com.ifountain.rcmdb.domain.method.*
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.springframework.validation.BindException
import org.springframework.validation.Errors
import org.codehaus.groovy.grails.validation.GrailsDomainClassValidator
import com.ifountain.rcmdb.domain.validator.RapidGrailsDomainClassValidator
import com.ifountain.rcmdb.transaction.RapidCmdbTransactionManager
import com.ifountain.compass.transaction.CompassTransactionFactory
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.apache.lucene.search.BooleanQuery
import org.codehaus.groovy.runtime.InvokerHelper
import com.ifountain.rcmdb.domain.cache.IdCache

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 18, 2008
* Time: 11:35:59 AM
* To change this template use File | Settings | File Templates.
*/
class SearchableExtensionGrailsPlugin {
    def logger = Logger.getLogger("grails.app.plugins.SearchableExtension")
    def version = 0.1
    def dependsOn = [searchable: "0.5-SNAPSHOT"]
    def loadAfter = ['searchable', 'hibernate']
    def domainClassMap;
    def doWithSpring = {
        BooleanQuery.setMaxClauseCount (3000);
        for(dc in application.domainClasses) {
            "${dc.fullName}Validator"(RapidGrailsDomainClassValidator) {
                messageSource = ref("messageSource")
                domainClass = ref("${dc.fullName}DomainClass")                
            }
		}
    }

    def doWithApplicationContext = {applicationContext ->
    }

    def doWithWebDescriptor = {xml ->
    }

    def doWithDynamicMethods = {ctx ->
        CompassTransactionFactory factory = new CompassTransactionFactory(ctx.getBean("compass"));
		RapidCmdbTransactionManager.initializeTransactionManager (factory);
        IdGenerator.initialize(new IdGeneratorStrategyImpl());
        domainClassMap = [:];
        for (dc in application.domainClasses) {
            MetaClass mc = dc.metaClass
            domainClassMap[mc.getTheClass().name] = dc
        }
        def domainClassesToBeCreated = [];
        for (dc in application.domainClasses) {
            MetaClass mc = dc.metaClass
            if (isSearchable(mc))
            {
                if (!domainClassMap.containsKey(mc.getTheClass().getSuperclass().getName()))
                {
                    domainClassesToBeCreated += dc;
                }
            }
        }
        for (dc in domainClassesToBeCreated) {
            MetaClass mc = dc.metaClass
            registerDynamicMethods(dc, application, ctx);
        }

        
    }

    def onChange = {event ->
    }

    def onApplicationChange = {event ->
    }

    def registerDynamicMethods(dc, application, ctx)
    {
        def mc = dc.clazz.metaClass;
        try
        {
            dc.metaClass.getTheClass().newInstance().delete();
        }
        catch(t)
        {
            logger.debug("Delete method injection didnot performed for ${dc.name} by hibernate plugin.");
        }
        addBasicPersistenceMethods(dc, application, ctx)
        addQueryMethods(dc, application, ctx)
        mc.'static'.methodMissing = {String methodName, args ->
            if (methodName.startsWith("findBy"))
            {
                def searchKeyMap = [:]
                def propName = StringUtils.substringAfter(methodName, "findBy");
                propName = propName.substring(0, 1).toLowerCase() + propName.substring(1, propName.length());
                searchKeyMap[propName] = args[0];
                return CompassMethodInvoker.search(mc, searchKeyMap).results[0];
            }
            else if (methodName.startsWith("findAllBy"))
            {
                def searchKeyMap = [:]
                def propName = StringUtils.substringAfter(methodName, "findAllBy");
                propName = propName.substring(0, 1).toLowerCase() + propName.substring(1, propName.length());
                searchKeyMap[propName] = args[0];
                return CompassMethodInvoker.search(mc, searchKeyMap).results;
            }
            return mc.invokeStaticMethod(mc.theClass, "_methodMissing", [methodName, args] as Object[]);
        }

        mc.'static'._methodMissing = {String methodName, args ->
            throw new MissingMethodException(methodName, mc.theClass, args);
        }
        for (subClass in dc.subClasses)
        {
            if (subClass.metaClass.getTheClass().getSuperclass().name == dc.metaClass.getTheClass().name)
            {
                registerDynamicMethods(subClass, application, ctx);
            }
        }
    }

    def addBasicPersistenceMethods(GrailsDomainClass dc, application, ctx)
    {
        def mc = dc.metaClass;
        def parentDomainClass = DomainClassUtils.getParentDomainClass(dc, application.getDomainClasses())
        def relations = DomainClassUtils.getRelations(dc);
        dc.refreshConstraints();
        def keys = DomainClassUtils.getKeys(dc);
        def persProps = DomainClassUtils.getPersistantProperties(dc, true);
        def addMethod = new AddMethod(mc, parentDomainClass, dc.validator, persProps, relations, keys);
        def addUniqueMethod = new AddMethod(mc, parentDomainClass, dc.validator, persProps, relations, keys);
        addUniqueMethod.setWillReturnErrorIfExist (true);
        def removeAllMatchingMethod = new RemoveAllMatchingMethod(mc, relations);
        def getPropertyValuesMethod = new GetPropertyValuesMethod(mc, relations);
        def getRelatedModelPropertyValuesMethod = new GetRelatedObjectPropertyValuesMethod(mc, relations);
        def removeMethod = new RemoveMethod(mc, relations);
        def updateMethod = new UpdateMethod(mc, dc.validator, persProps, relations);
        def addRelationMethod = new AddRelationMethod(mc, relations);
        def removeRelationMethod = new RemoveRelationMethod(mc, relations);

        mc.update = {Map props->
            return delegate.invokeOperation("update", [props] as Object[])    
        }
        mc._update = {Map props->
            return updateMethod.invoke(delegate,  [props] as Object[])
        }

        mc.addRelation = {Map props, String source->
            return delegate.invokeOperation("addRelation", [props, source] as Object[])
        }

        mc.addRelation = {Map props->
            return delegate.invokeOperation("addRelation", [props] as Object[])
        }
        mc._addRelation = {Map props, String source->
          return addRelationMethod.invoke(delegate,  [props, source] as Object[])
        }
        mc.removeRelation = {Map props, String source->
            return delegate.invokeOperation("removeRelation", [props, source] as Object[])
        }
        mc.removeRelation = {Map props->
            return delegate.invokeOperation("removeRelation", [props] as Object[])
        }
        mc._removeRelation = {Map props, String source->
            return removeRelationMethod.invoke(delegate,  [props, source] as Object[])
        }
        mc.remove = {->
            return delegate.invokeOperation("remove", InvokerHelper.EMPTY_ARGS)
        }
        mc._remove = {->
            return removeMethod.invoke(delegate, null);
        }

        mc.'static'.removeAll = {->
            return mc.theClass.invokeStaticOperation("removeAll", [mc.theClass] as Object[])
        }
        mc.'static'.removeAll = {String query->
            return mc.theClass.invokeStaticOperation("removeAll", [mc.theClass, query] as Object[])
        }

        mc.getRelatedModelPropertyValues = {String relationName, Collection propertyList->
            getRelatedModelPropertyValuesMethod.invoke(delegate, [relationName, propertyList, [:]] as Object[])
        }

        mc.getRelatedModelPropertyValues = {String relationName, Collection propertyList, Map options->
            getRelatedModelPropertyValuesMethod.invoke(delegate, [relationName, propertyList, options] as Object[])
        }

        mc.'static'.getPropertyValues = {String query, Collection propertyList->
            delegate.'getPropertyValues'(query, propertyList, [:]);
        }

        mc.'static'.getPropertyValues = {String query, Collection propertyList, Map options->
            getPropertyValuesMethod.invoke(mc.theClass, [query, propertyList, options] as Object[]);
        }

        mc.'static'._removeAll = {query->
            removeAllMatchingMethod.invoke(mc.theClass, [query] as Object[]);
        }

        mc.'static'.add = {Map props->
            return mc.theClass.invokeStaticOperation("add", [mc.theClass, props] as Object[])
        }
        mc.'static'.addUnique = {Map props->
            return mc.theClass.invokeStaticOperation("addUnique", [mc.theClass, props] as Object[])
        }
        mc.'static'._add = {Map props->
            return addMethod.invoke(mc.theClass, [props] as Object[]);
        }
        mc.'static'._addUnique = {Map props->
            return addUniqueMethod.invoke(mc.theClass, [props] as Object[]);
        }
    }

    def addQueryMethods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def keys = DomainClassUtils.getKeys(dc);
        def propSummaryMethod = new PropertySummaryMethod(mc);
        def parentDomainClass = DomainClassUtils.getParentDomainClass(dc,  application.getDomainClasses())
        def relations = DomainClassUtils.getRelations(dc);
        def getMethod = new GetMethod(mc, keys, relations);
        mc.'static'.getFromHierarchy = {Map searchParams->
            return getMethod.invoke(parentDomainClass, [searchParams] as Object[])
        }
        mc.'static'.getCacheEntry = {Map searchParams->
            return IdCache.get(mc.theClass, searchParams);
        }
        mc.'static'.get = {Map searchParams->
            return getMethod.invoke(mc.theClass, [searchParams] as Object[])
        }
        mc.'static'.get = {Long searchParams->
            return getMethod.invoke(mc.theClass, [searchParams] as Object[])
        }


        mc.'static'.list = {->
            return CompassMethodInvoker.searchEvery(mc, "alias:*");
        }
        mc.'static'.propertySummary = {query, propNames->
            return propSummaryMethod.invoke(mc.theClass, [query, propNames] as Object[])
        }

        mc.'static'.list = {Map options->
            return CompassMethodInvoker.search(mc, "alias:*", options).results;
        }

        mc.'static'.count = {->
            return mc.invokeStaticMethod(mc.theClass, "countHits", ["alias:*"] as Object[]);
        }
    }



    private boolean isSearchable(mc)
    {
        def metaProp = mc.getMetaProperty("searchable");
        return metaProp != null;
    }

}

class RapidBindException extends BindException
{

    public RapidBindException(Object o, String s) {
        super(o, s); 
    }

    public void addAllErrors(Errors errors) {
        if(errors == null) return;
        super.addAllErrors(errors);    //To change body of overridden methods use File | Settings | File Templates.
    }

}