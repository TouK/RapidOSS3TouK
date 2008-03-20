import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.hibernate.SessionFactory

class RapidDomainClassGrailsPlugin {
    def watchedResources = ["file:./grails-app/scripts/*.groovy"]
    def version = 0.1
    def dependsOn = [:]
    def loadAfter = ['hibernate']
    def doWithSpring = {
    }

    def doWithApplicationContext = { applicationContext ->

    }

    def doWithWebDescriptor = { xml ->
    }

    def doWithDynamicMethods = { ctx ->
        SessionFactory sessionFactory = ctx.sessionFactory
        for (dc in application.domainClasses) {
            //    registerDynamicMethods(dc, application, ctx)
            MetaClass mc = dc.metaClass
            registerDynamicMethods(dc, application, ctx)
            for (subClass in dc.subClasses) {
                registerDynamicMethods(subClass, application, ctx)
            }
            configureFederation(log, dc);
            MetaClass emc = GroovySystem.metaClassRegistry.getMetaClass(dc.clazz)
        }
    }

    def onChange = { event ->
    }

    def onApplicationChange = { event ->
    }


    def registerDynamicMethods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        mc.'static'.get = {Map keys->
            def sampleBean = delegate.newInstance();
            keys.each{key,value->
                sampleBean.setProperty (key, value);
            }
            return mc.invokeStaticMethod(delegate, "find", sampleBean)
        }

        mc.'static'.add = {Map props->
            def sampleBean = delegate.newInstance();
            props.each{key,value->
                sampleBean.setProperty (key, value);
            }
            sampleBean.save();
            return sampleBean;
        }

        mc.'static'.create = {Map props->
            def sampleBean = delegate.newInstance();
            props.each{key,value->
                sampleBean.setProperty (key, value);
            }
            return sampleBean;
        }

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

    def configureFederation(log, dc)
    {
        log.info("Configuring federation");
        MetaClass mc = dc.metaClass
        def dataSources = [:];
        def propertyConfiguration = [:];
        getDatasourcesAndPropertyConfigurations (dataSources, propertyConfiguration, dc);
        if(dataSources.size() > 0 && propertyConfiguration.size() > 0)
        {
            mc.setProperty = {String name, Object value->
                def propertyConfig = propertyConfiguration[name];
                if(!propertyConfig || propertyConfig.datasource == RapidCMDBConstants.RCMDB)
                {
                    mc.getMetaProperty(name).setProperty(delegate, value);
                }
            };

            mc.getProperty = {String name->
                def propertyConfig = propertyConfiguration[name];
                if(!propertyConfig || propertyConfig.datasource == RapidCMDBConstants.RCMDB)
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
                            def currentDomainObject = delegate;
                            keyConfiguration.each{key,value->
                                def nameInDs = key;
                                if(value && value.nameInDs)
                                {
                                    nameInDs = value.nameInDs;
                                }
                                def keyValue =   mc.getMetaProperty(key).getProperty(currentDomainObject);
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
