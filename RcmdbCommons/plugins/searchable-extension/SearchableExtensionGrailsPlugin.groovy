import com.ifountain.compass.transaction.CompassTransactionFactory
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.IdGeneratorStrategyImpl
import com.ifountain.rcmdb.domain.cache.IdCache
import com.ifountain.rcmdb.domain.method.*
import com.ifountain.rcmdb.domain.property.FederatedPropertyManager
import com.ifountain.rcmdb.domain.property.PropertyDatasourceManagerBean
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.domain.validator.RapidGrailsDomainClassValidator
import com.ifountain.rcmdb.transaction.RapidCmdbTransactionManager
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.apache.lucene.search.BooleanQuery
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.springframework.validation.BindException
import org.springframework.validation.Errors

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
        BooleanQuery.setMaxClauseCount(3000);
        for (dc in application.domainClasses) {
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
        RapidCmdbTransactionManager.initializeTransactionManager(factory);
        
        IdGenerator.initialize(new IdGeneratorStrategyImpl(application.config.toProperties()["rapidCMDB.id.start"]));
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
        catch (t)
        {
            logger.debug("Delete method injection didnot performed for ${dc.name} by hibernate plugin.");
        }
        addBasicPersistenceMethods(dc, application, ctx)
        addQueryMethods(dc, application, ctx)
        def cls = dc.clazz;
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
        addUniqueMethod.setWillReturnErrorIfExist(true);
        def removeAllMatchingMethod = new RemoveAllMatchingMethod(mc, relations);
        def getPropertyValuesMethod = new GetPropertyValuesMethod(mc, relations);
        def getPropertyValuesAsStringMethod = new GetPropertyValuesAsStringMethod(mc);
        def getRelatedModelPropertyValuesMethod = new GetRelatedObjectPropertyValuesMethod(mc, relations);
        def getRelatedObjectsMethod = new GetRelatedObjectsMethod(mc, relations);
        def removeMethod = new RemoveMethod(mc, relations);
        def updateMethod = new UpdateMethod(mc, dc.validator, persProps, relations);
        def addRelationMethod = new AddRelationMethod(mc, relations);
        def removeRelationMethod = new RemoveRelationMethod(mc, relations);

        mc._update = {Map props ->
            return updateMethod.invoke(delegate, [props] as Object[])
        }

        mc._addRelation = {Map props ->
            return addRelationMethod.invoke(delegate, [props, null] as Object[])
        }
        mc._addRelation = {Map props, String source ->
            return addRelationMethod.invoke(delegate, [props, source] as Object[])
        }
        mc._removeRelation = {Map props, String source ->
            return removeRelationMethod.invoke(delegate, [props, source] as Object[])
        }
        mc._removeRelation = {Map props ->
            return removeRelationMethod.invoke(delegate, [props, null] as Object[])
        }

        mc._remove = {->
            return removeMethod.invoke(delegate, null);
        }

        mc.update = {Map props ->
            return invokeCompassOperation("update", [props]);
        }

        mc.addRelation = {Map props ->
            return invokeCompassOperation("addRelation", [props]);
        }
        mc.addRelation = {Map props, String source ->
            return invokeCompassOperation("addRelation", [props, source]);
        }
        mc.removeRelation = {Map props, String source ->
            return invokeCompassOperation("removeRelation", [props, source]);
        }
        mc.removeRelation = {Map props ->
            return invokeCompassOperation("removeRelation", [props]);
        }

        mc.remove = {->
            return invokeCompassOperation("remove", []);
        }

        mc.'static'.searchAsString = {query->
            return getPropertyValuesAsStringMethod.invoke(mc.theClass, [query, [:]] as Object[]);
        }
        mc.'static'.searchAsString = {query, params->
            return getPropertyValuesAsStringMethod.invoke(mc.theClass, [query, params] as Object[]);
        }

        mc.'static'.removeAll = {->
            removeAllMatchingMethod.invoke(mc.theClass, ["alias:*"] as Object[]);
        }
        mc.'static'.removeAll = {String query ->
            removeAllMatchingMethod.invoke(mc.theClass, [query] as Object[]);
        }

        mc.'static'.add = {Map props ->
            return addMethod.invoke(mc.theClass, [props] as Object[]);
        }
        mc.'static'.addUnique = {Map props ->
            return addUniqueMethod.invoke(mc.theClass, [props] as Object[]);
        }

        mc.getRelatedModelPropertyValues = {String relationName, Collection propertyList ->
            getRelatedModelPropertyValuesMethod.invoke(delegate, [relationName, propertyList, [:], null] as Object[])
        }

        mc.getRelatedModelPropertyValues = {String relationName, Collection propertyList, Map options ->
            getRelatedModelPropertyValuesMethod.invoke(delegate, [relationName, propertyList, options, null] as Object[])
        }

        mc.getRelatedModelPropertyValues = {String relationName, Collection propertyList, Map options, String source ->
            getRelatedModelPropertyValuesMethod.invoke(delegate, [relationName, propertyList, options, source] as Object[])
        }

        mc.getRelatedObjects = {String relationName, String source ->
            getRelatedObjectsMethod.invoke(delegate, [relationName, source] as Object[])
        }
        mc.getRelatedObjects = {String relationName ->
            getRelatedObjectsMethod.invoke(delegate, [relationName, null] as Object[])
        }

        mc.'static'.getPropertyValues = {String query, Collection propertyList ->
            delegate.'getPropertyValues'(query, propertyList, [:]);
        }

        mc.'static'.getPropertyValues = {String query, Collection propertyList, Map options ->
            getPropertyValuesMethod.invoke(mc.theClass, [query, propertyList, options] as Object[]);
        }

    }

    def addQueryMethods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def keys = DomainClassUtils.getKeys(dc);
        def propSummaryMethod = new PropertySummaryMethod(mc);
        def parentDomainClass = DomainClassUtils.getParentDomainClass(dc, application.getDomainClasses())
        def relations = DomainClassUtils.getRelations(dc);
        def getMethod = new GetMethod(mc, keys, relations);
        mc.'static'.getFromHierarchy = {Map searchParams ->
            return getMethod.invoke(parentDomainClass, [searchParams] as Object[])
        }

        mc.'static'.getFromHierarchy = {Map searchParams, boolean willTriggerOnLoad ->
            return getMethod.invoke(parentDomainClass, [searchParams, willTriggerOnLoad] as Object[])
        }
        mc.'static'.getCacheEntry = {object ->
            return IdCache.get(mc.theClass, object);
        }

        mc.'static'.updateCacheEntry = {object, boolean exist ->
            return IdCache.update(object, exist);
        }
        mc.'static'.get = {Map searchParams ->
            return getMethod.invoke(mc.theClass, [searchParams] as Object[])
        }

        mc.'static'.get = {Map searchParams, boolean willTriggerOnLoad ->
            return getMethod.invoke(mc.theClass, [searchParams, willTriggerOnLoad] as Object[])
        }
        mc.'static'.get = {Long searchParams ->
            return getMethod.invoke(mc.theClass, [searchParams] as Object[])
        }


        mc.'static'.list = {->
            return CompassMethodInvoker.searchEvery(mc, "alias:*");
        }
        mc.'static'.propertySummary = {query, propNames ->
            return propSummaryMethod.invoke(mc.theClass, [query, propNames] as Object[])
        }

        mc.'static'.list = {Map options ->
            return CompassMethodInvoker.searchEvery(mc, "alias:*", options);
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
        if (errors == null) return;
        super.addAllErrors(errors); //To change body of overridden methods use File | Settings | File Templates.
    }

}