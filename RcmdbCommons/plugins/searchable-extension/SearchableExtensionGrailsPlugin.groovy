import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.IdGeneratorStrategyImpl
import com.ifountain.rcmdb.domain.method.*
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.springframework.validation.BindException
import org.springframework.validation.Errors

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
    }

    def doWithApplicationContext = {applicationContext ->
    }

    def doWithWebDescriptor = {xml ->
    }

    def doWithDynamicMethods = {ctx ->
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
            return null;
        }
        for (subClass in dc.subClasses)
        {
            if (subClass.metaClass.getTheClass().getSuperclass().name == dc.metaClass.getTheClass().name)
            {
                registerDynamicMethods(subClass, application, ctx);
            }
        }
    }

    def addBasicPersistenceMethods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def relations = DomainClassUtils.getRelations(dc);
        dc.refreshConstraints();
        def keys = DomainClassUtils.getKeys(dc);
        def persProps = DomainClassUtils.getPersistantProperties(dc, true);
        def addMethod = new AddMethod(mc, dc.validator, persProps, relations, keys);
        def removeAllMethod = new RemoveAllMethod(mc);
        def removeMethod = new RemoveMethod(mc, relations);
        def updateMethod = new UpdateMethod(mc, dc.validator, persProps, relations);
        def addRelationMethod = new AddRelationMethod(mc, relations);
        def removeRelationMethod = new RemoveRelationMethod(mc, relations);

        mc.update = {Map props->
            return updateMethod.invoke(delegate,  [props] as Object[])
        }
        mc.addRelation = {Map props->
          return addRelationMethod.invoke(delegate,  [props] as Object[])
        }
        mc.addRelation = {Map props, boolean flush->
          return addRelationMethod.invoke(delegate,  [props, flush] as Object[])
        }
        mc.removeRelation = {Map props->
            return removeRelationMethod.invoke(delegate,  [props] as Object[])
        }

        mc.removeRelation = {Map props, boolean flush->
            return removeRelationMethod.invoke(delegate,  [props, flush] as Object[])
        }
        mc.remove = {->
            return removeMethod.invoke(delegate, null);
        }
        mc.'static'.removeAll = {->
            removeAllMethod.invoke(mc.theClass, null);
        }
        mc.'static'.add = {Map props->
            return addMethod.invoke(mc.theClass, [props] as Object[]);
        }
    }

    def addQueryMethods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def keys = DomainClassUtils.getKeys(dc);
        def relations = DomainClassUtils.getRelations(dc);
        def getMethod = new GetMethod(mc, keys, relations);
        mc.'static'.get = {Map searchParams->
            return getMethod.invoke(mc.theClass, [searchParams] as Object[])
        }
        mc.'static'.get = {Long searchParams->
            return getMethod.invoke(mc.theClass, [searchParams] as Object[])
        }


        mc.'static'.list = {->
            return CompassMethodInvoker.searchEvery(mc, "alias:*");
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