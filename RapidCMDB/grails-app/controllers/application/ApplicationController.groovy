package application

import model.Model
import com.ifountain.rcmdb.domain.ModelUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.runtime.StackTraceUtils
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.codehaus.groovy.grails.commons.GrailsDomainConfigurationUtil
import org.codehaus.groovy.grails.commons.GrailsClass
import model.ModelModificationSqls
import org.hibernate.cfg.ImprovedNamingStrategy

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 26, 2008
* Time: 1:35:56 AM
* To change this template use File | Settings | File Templates.
*/
class ApplicationController {
    public static final String RESTART_APPLICATION = "restart.application"
    def sessionFactory;

    def reload = {
        def models = Model.findAllByResourcesWillBeGenerated(true);
        GrailsAwareClassLoader gcl = new GrailsAwareClassLoader(Thread.currentThread().getContextClassLoader().parent);
        gcl.setShouldRecompile(true);
        String baseDirectory = System.getProperty("base.dir")
        gcl.addClasspath(baseDirectory + "/grails-app/domain");
        gcl.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);
        def domainClassesWillBeGenerated = [];
        def domainClassesMap = [:];
        models.each {Model model ->
            try
            {
                def cls = gcl.loadClass(model.name);
                def domainClass = new DefaultGrailsDomainClass(cls);
                domainClassesWillBeGenerated += domainClass;
                domainClassesMap[model.name] = domainClass;
            }
            catch (t)
            {
                log.error("Exception occurred while creating controller, view and operations files of model ${model.name}", StackTraceUtils.deepSanitize(t));
            }
        }
        GrailsDomainConfigurationUtil.configureDomainClassRelationships(domainClassesWillBeGenerated as GrailsClass[], domainClassesMap);
        models.each {Model model ->
            def domainClass = domainClassesMap[model.name];
            if (domainClass)
            {
                ModelUtils.generateModelArtefacts(domainClass, baseDirectory);
                model.resourcesWillBeGenerated = false;
                model.save(flush: true);
            }
        }
        def droppedTables = [:];
        def sqlToBeExecuted = [];

        ModelModificationSqls.list().each {
            def sql = it.sqlStatement;
            if (sql.startsWith("DROP TABLE")) {
                def tableName = sql.substring(10).trim();
                droppedTables.put(tableName, tableName);
                sqlToBeExecuted.add(sql);
            }
            else {
                def wordsInSql = sql.split("\\s+");
                def tableName;
                if (sql.startsWith("UPDATE")) {
                    tableName = wordsInSql[1];
                }
                else if (sql.startsWith("ALTER TABLE")) {
                    tableName = wordsInSql[2];
                }
                if (!droppedTables.containsKey(tableName)) {
                    sqlToBeExecuted.add(sql);
                }
            }
        }
        println "sqls: " + sqlToBeExecuted;
        sqlToBeExecuted.each {String sql->
            try
            {
                println "executing sql: " + sql;
                sessionFactory.getCurrentSession().createSQLQuery(sql).executeUpdate();
            }
            catch (t)
            {
                t.printStackTrace();
            }
        }
        ModelModificationSqls.list()*.delete(flush: true);
        render(view: "reloading", controller: "application");
        GroovyPagesTemplateEngine.pageCache.clear();
        System.setProperty(RESTART_APPLICATION, "true");
    }

    def deneme = {
        ImprovedNamingStrategy st = new ImprovedNamingStrategy();
        println "Collection tABLE: " + st.collectionTableName(null, st.tableName("NewAuthor"), null, null, st.tableName("NewBook"))
    }

    def exportModel = {
        def writer = new FileWriter("RCMDB_export.xml");
        def builder = new groovy.xml.StreamingMarkupBuilder();

        def mymodels = model.Model.list();
        def models = {
            models() {
                for (mymodel in mymodels) {
                    out << mymodel.xml()
                }
            }
        }

        def datasources = {
            datasources() {
                datasource("DS1")
                datasource("DS2")
                datasource("DS3")
            }
        }

        def export = {
            export() {
                out << models
                out << datasources
            }
        }


        writer << builder.bind(export);
    }
}