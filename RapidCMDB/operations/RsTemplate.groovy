import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.support.*
import org.codehaus.groovy.grails.web.context.*

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Aug 4, 2009
 * Time: 9:43:51 AM
 * To change this template use File | Settings | File Templates.
 */
class RsTemplate {
    static String render(String templatePath,params)
    {
        def engine=new GroovyPagesTemplateEngine()
        def template= engine.createTemplate(new File(templatePath));

        def requestAttributes = RequestContextHolder.getRequestAttributes()

        boolean unbindRequest = false

        // outside of an executing request, establish a mock version
        if(!requestAttributes) {
            def servletContext  = ServletContextHolder.getServletContext()
            def applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
            requestAttributes = grails.util.GrailsWebUtil.bindMockWebRequest(applicationContext)
            unbindRequest = true
        }

        def out = new StringWriter();
        def originalOut = requestAttributes.getOut()
        requestAttributes.setOut(out)
        try {

            template.make( params ).writeTo(out)

        }
        finally {
            requestAttributes.setOut(originalOut)
            if(unbindRequest) {
                RequestContextHolder.setRequestAttributes(null)
            }
        }

        return out.toString();
    }
}