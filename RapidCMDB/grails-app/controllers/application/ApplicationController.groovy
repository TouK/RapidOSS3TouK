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
package application

import model.ModelAction
import model.PropertyAction
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainConfigurationUtil
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import com.ifountain.rcmdb.util.ModelUtils
import com.ifountain.rcmdb.domain.generation.ExistingDataAnalyzer
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException;

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
    def searchableService;
    def index = {render(view: "application")}
    def reload = {
        def oldDomainClasses = [:]
        def baseDir = grailsApplication.config.toProperties()["rapidCMDB.base.dir"];
        def tempBaseDir = grailsApplication.config.toProperties()["rapidCMDB.temp.dir"];
        def currentModelDir = "${baseDir}/grails-app/domain";
        def currentModelDirFile = new File(currentModelDir);
        def tempModelDir = "${tempBaseDir}/grails-app/domain";
        def tempModelDirFile = new File(tempModelDir);
        Collection tempModelFileList = [];
        if (tempModelDirFile.exists()) {
            tempModelFileList = FileUtils.listFiles(tempModelDirFile, ["groovy"] as String[], false);
        }
        Collection currentModelFileList = FileUtils.listFiles(currentModelDirFile, ["groovy"] as String[], false);
        currentModelFileList.each {File modelFile ->
            String modelName = StringUtils.substringBefore(modelFile.name, ".groovy");
            GrailsDomainClass cls = grailsApplication.getDomainClass(modelName);
            if (cls)
            {
                oldDomainClasses[modelName] = cls;
            }
        }
        def domainClassesWillBeGenerated = [];
        def newDomainClassesMap = [:];
        GrailsAwareClassLoader gcl = new GrailsAwareClassLoader(Thread.currentThread().getContextClassLoader().parent);
        gcl.setShouldRecompile(true);
        gcl.addClasspath(tempModelDir);
        gcl.addClasspath(baseDir + "/src/groovy");
        gcl.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);
        boolean isNewClassesLoadedSuccessfully = true;
        tempModelFileList.each {File modelFile ->

            String modelName = StringUtils.substringBefore(modelFile.name, ".groovy");
            try
            {
                def cls = gcl.loadClass(modelName);
                def domainClass = new DefaultGrailsDomainClass(cls);
                domainClassesWillBeGenerated += domainClass;
                newDomainClassesMap[modelName] = domainClass;
            }
            catch (MultipleCompilationErrorsException e)
            {
                e.printStackTrace();
                isNewClassesLoadedSuccessfully = false;
                if (e.getErrorCollector().getErrorCount() > 0)
                {
                    def exception = e.getErrorCollector().getError(0).getCause();
                    if (exception instanceof SyntaxException)
                    {
                        if (exception.startLine > 0)
                        {
                            def syntaxError = modelFile.readLines()[exception.startLine - 1]
                            if (syntaxError != null && exception.startColumn > 0 && exception.startColumn <= syntaxError.length())
                            {
                                syntaxError = syntaxError.substring(exception.startColumn - 1);
                            }                            
                            addError("default.property.name.invalid", [syntaxError, modelName]);
                            flash.errors = errors;
                            return;
                        }
                    }

                }
                flash.message = "Could not reload because following exception occurred ${e.toString()}."
                return;
            }

        }
        if (!isNewClassesLoadedSuccessfully) {
            if (params.targetURI) {
                redirect(uri: params.targetURI);
            }
            else {
                render(view: "application", controller: "application");
            }
            return;
        }
        com.ifountain.rcmdb.domain.generation.DataCorrectionUtilities.dataCorrectionBeforeReloadStep(baseDir, tempModelDir, oldDomainClasses, domainClassesWillBeGenerated, newDomainClassesMap);
        if (tempModelDirFile.exists()) {
            FileUtils.copyDirectory(tempModelDirFile, currentModelDirFile);
        }
        flash.message = "Reloading application."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
        GroovyPagesTemplateEngine.pageCache.clear();
        System.setProperty(RESTART_APPLICATION, "true");
    }

    def reloadControllers = {
        RsApplication.reloadControllers();
        flash.message = "Controllers reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }

    def reloadFilters = {
        RsApplication.reloadFilters();
        flash.message = "Filters reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }

    def reloadViewsAndControllers = {
        RsApplication.reloadViewsAndControllers();
        flash.message = "Views and controllers reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }
    
    def reloadViews = {
        RsApplication.reloadViews();
        flash.message = "Views reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }
}
