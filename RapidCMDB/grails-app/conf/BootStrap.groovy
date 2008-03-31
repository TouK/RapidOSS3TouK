import datasource.RCMDBDatasource
import model.Model
import org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class BootStrap {

     def init = { servletContext ->
        def rcmdbDatasource = RCMDBDatasource.findByName(RapidCMDBConstants.RCMDB);
        if(rcmdbDatasource == null){
            new RCMDBDatasource(name:RapidCMDBConstants.RCMDB).save();
        }
        def generator = new DefaultGrailsTemplateGenerator();
        generator.overwrite = true;
        ApplicationHolder.application.domainClasses.each
        {
            Model model = Model.findByName(it.getFullName());
            if(model && model.generateAll && model.generateAll.booleanValue())
            {
                generator.generateViews(it,".")
                generator.generateController(it,".")
                model.generateAll = Boolean.FALSE;
                model.save();
            }
        }
        
     }
     def destroy = {
     }
} 