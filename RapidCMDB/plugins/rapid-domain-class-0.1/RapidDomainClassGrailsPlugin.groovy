import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU

class RapidDomainClassGrailsPlugin {
    def version = 0.1
    def dependsOn = [:]
    def loadAfter = ['hibernate']
    def doWithSpring = {
//        println application.domainClasses;
    }

    def doWithApplicationContext = { applicationContext ->

    }

    def doWithWebDescriptor = { xml ->
    }
    
    def doWithDynamicMethods = { ctx ->
        configureFederation(log, application);
    }

    def onChange = { event ->
    }

    def onApplicationChange = { event ->
    }

    def getDatasourcesAndPropertyConfigurations(allDatasources, allPropertyConfiguration, domainClass)
    {
        def realClass = domainClass.metaClass.getTheClass();
        def superClass = realClass.getSuperclass();
        if(superClass && superClass != Object.class)
        {
            getDatasourcesAndPropertyConfigurations(allDatasources, allPropertyConfiguration, superClass);
        }
        if(domainClass.metaClass.hasProperty(domainClass, "propertyConfiguration") && domainClass.metaClass.hasProperty(domainClass, "dataSources"))
        {
            def dataSources = GCU.getStaticPropertyValue (realClass, "dataSources");
            def propertyConfiguration = GCU.getStaticPropertyValue (realClass, "propertyConfiguration");
            allPropertyConfiguration.putAll(propertyConfiguration);
            allDatasources.putAll(dataSources);
        }
    }

    def configureFederation(log, application)
    {
        log.info("Configuring federation");
        for (dc in application.domainClasses) {
            MetaClass mc = dc.metaClass
            def dataSources = [:];
            def propertyConfiguration = [:];
            getDatasourcesAndPropertyConfigurations (dataSources, propertyConfiguration, dc);
            if(dataSources.size() > 0 && propertyConfiguration.size() > 0)
            {
                mc.setProperty = {String name, Object value->
                    def propertyConfig = propertyConfiguration[name];
                    if(!propertyConfig)
                    {
                        mc.getMetaProperty(name).setProperty(delegate, value);
                    }
                };

                mc.getProperty = {String name->
                    def propertyConfig = propertyConfiguration[name];
                    if(!propertyConfig)
                    {
                        return mc.getMetaProperty(name).getProperty(delegate);
                    }
                    else
                    {
                        def datasourceName =  propertyConfig.datasource;
                        if(datasourceName)
                        {
                            def datasourceConfig = dataSources[datasourceName];
                            def referenceProperty = datasourceConfig.referenceProperty;
                            def propertyDatasource;
                            if(referenceProperty)
                            {
                                def metaProp = mc.getMetaProperty(referenceProperty);
                                if(metaProp)
                                {
                                    def referencedDatasourceName = metaProp.getProperty(delegate);
                                    if(referencedDatasourceName)
                                    {
                                        propertyDatasource = BaseDatasource.findByName(referencedDatasourceName);
                                    }
                                }

                            }
                            else
                            {
                                 propertyDatasource = BaseDatasource.findByName(datasourceName)
                            }

                            if(propertyDatasource)
                            {
                                def keyConfiguration = datasourceConfig.keys;
                                def keys = [:];
                                def isNull = false;
                                keyConfiguration.each{key,value->
                                    def nameInDs = key;
                                    if(value && value.nameInDs)
                                    {
                                        nameInDs = value.nameInDs;
                                    }
                                    def keyValue =   delegate.getProperty(key);
                                    if(keyValue)
                                    {
                                        keys[nameInDs] = keyValue;
                                    }
                                    else
                                    {
                                        isNull = true;
                                        return;
                                    }
                                }
                                if(!isNull&& keys.size() > 0)
                                {
                                    def propName = name;
                                    if(propertyConfig.nameInDs)
                                    {
                                        propName = propertyConfig.nameInDs;
                                    }
                                    try
                                    {
                                        return propertyDatasource.getProperty (keys, propName);
                                    }
                                    catch(Throwable e)
                                    {
                                    }
                                }
                            }
                        }

                        return "";
                    }
                };
            }
        }
    }
}
