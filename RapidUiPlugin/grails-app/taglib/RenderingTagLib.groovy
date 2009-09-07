import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.commons.GrailsControllerClass
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.springframework.web.servlet.support.RequestContextUtils as RCU;

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
    def include = {attrs ->
        if (!groovyPagesTemplateEngine) throw new IllegalStateException("Property [groovyPagesTemplateEngine] must be set!")
        if (attrs.template) {
            def uri = attrs.template;
            def t = groovyPagesTemplateEngine.createTemplate(uri)

            if (attrs.model instanceof Map) {
                t.make(attrs.model).writeTo(out)
            }
            else
            {
                t.make().writeTo(out)
            }

        }
    }

    def createLinkTo = {attrs ->
        def url = "";
        if (attrs.base) {
            url += attrs.remove('base')
        } else {
            url += handleAbsolute(attrs)
        }
        url += grailsAttributes.getApplicationUri(request);
        def dir = attrs['dir']
        if (dir) {
            url += (dir.startsWith("/") ? dir : "/${dir}")
        }
        def file = attrs['file']
        if (file) {
            url += (file.startsWith("/") ? file : "/${file}")
        }
        def params = attrs.remove("params");
        if (params != null) {
            url = URLUtils.createURL(url, params);
        }
        out << url;
    }

    def createLink = {attrs ->
        // prefer a URL attribute
        def urlAttrs = attrs
        if (attrs['url'] instanceof Map) {
            urlAttrs = attrs.remove('url').clone()
        }
        else if (attrs['url']) {
            urlAttrs = attrs.remove('url').toString()
        }

        if (urlAttrs instanceof String) {
            def params = attrs.remove('params')
            def url = urlAttrs;
            if (!urlAttrs.startsWith("/") && !urlAttrs.startsWith("http")) {
                url = grailsAttributes.getApplicationUri(request) + "/" + url;
            }
            if (params != null) {
                url = URLUtils.createURL(url, params)
            }
            out << response.encodeURL(url)
        }
        else {
            def controller = urlAttrs.containsKey("controller") ? urlAttrs.remove("controller") : grailsAttributes.getController(request)?.controllerName
            def action = urlAttrs.remove("action")
            if (controller && !action) {
                GrailsControllerClass controllerClass = grailsApplication.getArtefactByLogicalPropertyName(ControllerArtefactHandler.TYPE, controller)
                String defaultAction = controllerClass?.getDefaultAction()
                if (controllerClass?.hasProperty(defaultAction))
                    action = defaultAction
            }
            def id = urlAttrs.remove("id")
            def frag = urlAttrs.remove('fragment')
            def params = urlAttrs.params && urlAttrs.params instanceof Map ? urlAttrs.remove('params') : [:]

            if (urlAttrs.event) {
                params."_eventId" = urlAttrs.event
            }
            def url
            if (id != null) params.id = id
            def urlMappings = grailsAttributes.getApplicationContext().getBean("grailsUrlMappingsHolder")
            def mapping = urlMappings.getReverseMapping(controller, action, params)
            url = mapping.createURL(controller, action, params, request.characterEncoding, frag)
            if (attrs.base) {
                out << attrs.remove('base')
            } else {
                out << handleAbsolute(attrs)
            }
            out << response.encodeURL(url)
        }
    }

    def link = {attrs, body ->
        def writer = out
        writer << '<a href="'
        // create the link
        if (request['flowExecutionKey']) {
            if (!attrs.params) attrs.params = [:]
            attrs.params."_flowExecutionKey" = request['flowExecutionKey']
        }

        writer << rui.createLink(attrs).encodeAsHTML()
        writer << '"'
        // process remaining attributes
        attrs.each {k, v ->
            writer << " $k=\"$v\""
        }
        writer << '>'
        // output the body
        writer << body()
        // close tag
        writer << '</a>'
    }

    def sortableColumn = {attrs ->
        def writer = out
        if (!attrs.property)
            throwTagError("Tag [sortableColumn] is missing required attribute [property]")

        if (!attrs.title && !attrs.titleKey)
            throwTagError("Tag [sortableColumn] is missing required attribute [title] or [titleKey]")

        def property = attrs.remove("property")
        def defaultOrder = attrs.remove("defaultOrder")
        if (defaultOrder != "desc") defaultOrder = "asc"

        // current sorting property and order
        def sort = params.sort
        def order = params.order

        // add sorting property and params to link params
        def linkParams = [sort: property]
        if (params.id) linkParams.put("id", params.id)
        if (attrs.params) linkParams.putAll(attrs.remove("params"))

        // determine and add sorting order for this column to link params
        attrs.class = (attrs.class ? "${attrs.class} sortable" : "sortable")
        if (property == sort) {
            attrs.class = attrs.class + " sorted " + order
            if (order == "asc") {
                linkParams.order = "desc"
            }
            else {
                linkParams.order = "asc"
            }
        }
        else {
            linkParams.order = defaultOrder
        }

        // determine column title
        def title = attrs.remove("title")
        def titleKey = attrs.remove("titleKey")
        if (titleKey) {
            if (!title) title = titleKey
            def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
            def locale = RCU.getLocale(request)
            title = messageSource.getMessage(titleKey, null, title, locale)
        }
        def linkAttrs = [:]
        if (attrs.url) {
            linkAttrs.url = attrs.remove("url")
        }
        else {
            def action = attrs.action ? attrs.remove("action") : (params.action ? params.action : "list")
            linkAttrs.action = action;
        }
        if (attrs.linkAttrs) {
            linkAttrs.putAll(attrs.remove("linkAttrs"));
        }
        if(linkAttrs.params){
            linkAttrs.params.putAll(linkParams)
        }
        else{
            linkAttrs.params = linkParams
        }

        writer << "<th "
        // process remaining attributes
        attrs.each {k, v ->
            writer << "${k}=\"${v.encodeAsHTML()}\" "
        }
        writer << ">${rui.link(linkAttrs) {title}}</th>"
    }

    def paginate = {attrs ->
        def writer = out
        if (attrs.total == null)
            throwTagError("Tag [paginate] is missing required attribute [total]")

        def messageSource = grailsAttributes.getApplicationContext().getBean("messageSource")
        def locale = RCU.getLocale(request)
        def total = attrs.total.toInteger()
        def offset = params.offset?.toInteger()
        def max = params.max?.toInteger()
        def maxsteps = (attrs.maxsteps ? attrs.maxsteps.toInteger() : 10)

        if (!offset) offset = (attrs.offset ? attrs.offset.toInteger() : 0)
        if (!max) max = (attrs.max ? attrs.max.toInteger() : 10)

        def linkParams = [offset: offset - max, max: max]
        if (params.sort) linkParams.sort = params.sort
        if (params.order) linkParams.order = params.order
        if (attrs.params) linkParams.putAll(attrs.params)

        def linkTagAttrs = [:]
        if (attrs.url) {
            linkTagAttrs.url = attrs.url;
        }
        else {
            def action = (attrs.action ? attrs.action : (params.action ? params.action : "list"))
            linkTagAttrs.action = action;
            if (attrs.controller) {
                linkTagAttrs.controller = attrs.controller
            }
            if (attrs.id != null) {
                linkTagAttrs.id = attrs.id
            }
        }
        def linkAttrs = attrs.remove("linkAttrs");
        if (linkAttrs) {
            linkTagAttrs.putAll(linkAttrs);
        }
        if(linkTagAttrs.params){
            linkTagAttrs.params.putAll(linkParams);            
        }
        else{
            linkTagAttrs.params = linkParams
        }


        // determine paging variables
        def steps = maxsteps > 0
        int currentstep = (offset / max) + 1
        int firststep = 1
        int laststep = Math.round(Math.ceil(total / max))

        // display previous link when not on firststep
        if (currentstep > firststep) {
            linkTagAttrs.class = 'prevLink'
            writer << link(linkTagAttrs.clone()) {
                (attrs.prev ? attrs.prev : messageSource.getMessage('paginate.prev', null, messageSource.getMessage('default.paginate.prev', null, 'Previous', locale), locale))
            }
        }

        // display steps when steps are enabled and laststep is not firststep
        if (steps && laststep > firststep) {
            linkTagAttrs.class = 'step'

            // determine begin and endstep paging variables
            int beginstep = currentstep - Math.round(maxsteps / 2) + (maxsteps % 2)
            int endstep = currentstep + Math.round(maxsteps / 2) - 1

            if (beginstep < firststep) {
                beginstep = firststep
                endstep = maxsteps
            }
            if (endstep > laststep) {
                beginstep = laststep - maxsteps + 1
                if (beginstep < firststep) {
                    beginstep = firststep
                }
                endstep = laststep
            }

            // display firststep link when beginstep is not firststep
            if (beginstep > firststep) {
                linkTagAttrs.params.offset = 0
                writer << rui.link(linkTagAttrs.clone()) {firststep.toString()}
                writer << '<span class="step">..</span>'
            }

            // display paginate steps
            (beginstep..endstep).each {i ->
                if (currentstep == i) {
                    writer << "<span class=\"currentStep\">${i}</span>"
                }
                else {
                    linkTagAttrs.params.offset = (i - 1) * max
                    writer << rui.link(linkTagAttrs.clone()) {i.toString()}
                }
            }

            // display laststep link when endstep is not laststep
            if (endstep < laststep) {
                writer << '<span class="step">..</span>'
                linkTagAttrs.params.offset = (laststep - 1) * max
                writer << rui.link(linkTagAttrs.clone()) {laststep.toString()}
            }
        }

        // display next link when not on laststep
        if (currentstep < laststep) {
            linkTagAttrs.class = 'nextLink'
            linkTagAttrs.params.offset = offset + max
            writer << rui.link(linkTagAttrs.clone()) {
                (attrs.next ? attrs.next : messageSource.getMessage('paginate.next', null, messageSource.getMessage('default.paginate.next', null, 'Next', locale), locale))
            }
        }

    }


    private String handleAbsolute(attrs) {
        def abs = attrs.remove("absolute")
        if (Boolean.valueOf(abs)) {
            def u = makeServerURL()
            if (u) {
                return u
            } else {
                throwTagError("Attribute absolute='true' specified but no grails.serverURL set in Config")
            }
        }
        return "";
    }

    String makeServerURL() {
        def u = ConfigurationHolder.config.grails.serverURL
        if (!u) {
            // Leave it null if we're in production so we can throw
            if (GrailsUtil.environment != GrailsApplication.ENV_PRODUCTION) {
                u = "http://localhost:" + (System.getProperty('server.port') ? System.getProperty('server.port') : "8080")
            }
        }
        return u
    }

}