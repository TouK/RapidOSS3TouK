import datasource.RCMDBDatasource
import model.Model
import org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.ApplicationHolder

class BootStrap {

     def init = { servletContext ->
        def rcmdbDatasource = RCMDBDatasource.findByName(RapidCMDBConstants.RCMDB);
        if(rcmdbDatasource == null){
            new RCMDBDatasource(name:RapidCMDBConstants.RCMDB).save();
        }
        def generator = new DefaultGrailsTemplateGenerator();
        println ApplicationHolder.application;
        ApplicationHolder.application.domainClasses.each
        {
            Model model = Model.findByName(it.class.getName());
            if(model && model.generateAll)
            {
                generator.generateViews(it,".")
                generator.generateController(it,".")
            }
        }
        
     }
     def destroy = {
     }
} 