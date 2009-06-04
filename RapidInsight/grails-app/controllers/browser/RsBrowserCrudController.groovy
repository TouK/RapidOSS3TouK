package browser

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 4, 2009
* Time: 2:17:19 PM
*/
class RsBrowserCrudController {

    def save = {
        def domainClass = grailsApplication.getDomainClass(params.className);
        if (domainClass) {
            def domainObject = domainClass.clazz."add"(ControllerUtils.getClassProperties(params, domainClass.clazz))
            if (!domainObject.hasErrors()) {
                render(text: ControllerUtils.convertSuccessToXml("${params.className} ${domainObject.id} created"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(domainObject.errors), contentType: "text/xml")
            }

        }
        else {
            addError("default.class.not.found", [params.className]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }

    def update = {
        def domainClass = grailsApplication.getDomainClass(params.className);
        if (domainClass) {
            def domainObject = domainClass.clazz."get"(id: params.id);
            if (domainObject) {
                domainObject.update(ControllerUtils.getClassProperties(params, domainClass.clazz));
                if (!domainObject.hasErrors()) {
                    render(text: ControllerUtils.convertSuccessToXml("${params.className} ${domainObject.id} updated"), contentType: "text/xml")
                }
                else {
                    render(text: errorsToXml(domainObject.errors), contentType: "text/xml")
                }
            }
            else {
                addError("default.object.not.found", [params.className, params.id]);
                render(text: errorsToXml(errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.class.not.found", [params.className]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }

    def delete = {
        def domainClass = grailsApplication.getDomainClass(params.className);
        if (domainClass) {
            def domainObject = domainClass.clazz."get"(id: params.id);
            if (domainObject) {
                domainObject.remove();
                render(text: ControllerUtils.convertSuccessToXml("${params.className} ${domainObject.id} deleted"), contentType: "text/xml")
            }
            else {
                addError("default.object.not.found", [params.className, params.id]);
                render(text: errorsToXml(errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.class.not.found", [params.className]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }
}