package com.ifountain.rcmdb.util
import org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ApplicationHolder

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 1, 2008
 * Time: 10:49:52 AM
 * To change this template use File | Settings | File Templates.
 */
class RapidGrailsTemplateGenerator extends DefaultGrailsTemplateGenerator{
     public void generateViews(GrailsDomainClass domainClass, String destdir) {
           super.generateViews(domainClass, destdir);
           def viewsDir = new File("${destdir}/grails-app/views/${domainClass.propertyName}")
           DefaultGrailsTemplateGenerator.LOG.info("Generating create view for domain class [${domainClass.fullName}]")
           generateAddToView(domainClass,viewsDir)
     }

     private generateAddToView(domainClass,destDir) {
        def addToFile = new File("${destDir}/addTo.gsp")
        if(canWrite(addToFile)) {
            addToFile.withWriter { w ->
                generateView(domainClass, "addTo", w)
            }
            DefaultGrailsTemplateGenerator.LOG.info("AddTo view generated at ${addToFile.absolutePath}")
        }
    }

    private canWrite(testFile) {
        if(!overwrite && testFile.exists()) {
			try {
                ant.input(message: "File ${testFile} already exists. Overwrite?", "y,n,a", addproperty: "overwrite.${testFile.name}")
                overwrite = (ant.antProject.properties."overwrite.${testFile.name}" == "a") ? true : overwrite
                return overwrite || ((ant.antProject.properties."overwrite.${testFile.name}" == "y") ? true : false)
            } catch (Exception e) {
                // failure to read from standard in means we're probably running from an automation tool like a build server
                return true
            }
        }
        return true
    }

    private getTemplateText(String template) {
        def application = ApplicationHolder.getApplication()
        // first check for presence of template in application
		if(resourceLoader && application?.warDeployed) {
			return resourceLoader
					.getResource("/WEB-INF/templates/scaffolding/${template}")
					.inputStream
					.text
		}
		else {
	        def templateFile = "${basedir}/src/templates/scaffolding/${template}"
	        if (!new File(templateFile).exists()) {
	            // template not found in application, use default template
	            def ant = new AntBuilder()
	            ant.property(environment:"env")
	            def grailsHome = ant.antProject.properties."env.GRAILS_HOME"
	            templateFile = "${grailsHome}/src/grails/templates/scaffolding/${template}"
	        }
	        return new File(templateFile).getText()
		}
    }

}