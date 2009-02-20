import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 19, 2009
* Time: 5:28:00 PM
* To change this template use File | Settings | File Templates.
*/
class RenderingTagLib {
    static namespace = "rui"
    GroovyPagesTemplateEngine groovyPagesTemplateEngine
    def include = { attrs ->
	    if(!groovyPagesTemplateEngine) throw new IllegalStateException("Property [groovyPagesTemplateEngine] must be set!")
		if(attrs.template) {
	        def uri = attrs.template;
	        def t = groovyPagesTemplateEngine.createTemplate(  uri )

            if(attrs.model instanceof Map) {
                t.make(attrs.model).writeTo(out)    
            }
            else
            {
                t.make().writeTo(out)    
            }

		}
	}
}