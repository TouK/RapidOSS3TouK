package message

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Mar 17, 2010
* Time: 11:04:19 AM
*/
class RsMessageRuleCalendarController {
    def delete = {
        def rsMessageRuleCalendar = RsMessageRuleCalendar.get([id: params.id])
        if (rsMessageRuleCalendar) {
            rsMessageRuleCalendar.remove()
            render(text: ControllerUtils.convertSuccessToXml("RsMessageRuleCalendar ${rsMessageRuleCalendar.id} deleted"), contentType: "text/xml")
        }
        else {
            addError("default.couldnot.delete", [RsMessageRuleCalendar, "RsMessageRuleCalendar not found with id ${params.id}"])
            render(text: errorsToXml(this.errors), contentType: "text/xml");
        }
    }

    def update = {
        def rsMessageRuleCalendar = RsMessageRuleCalendar.get([id: params.id])
        if (rsMessageRuleCalendar) {
            try {
                def calParams = [username: session.username]
                calParams.putAll(params);
                calParams.putAll(ControllerUtils.getClassProperties(params, RsMessageRuleCalendar))
                RsMessageRuleCalendar.updateCalendar(rsMessageRuleCalendar, calParams)
                if (!rsMessageRuleCalendar.hasErrors()) {
                    render(text: ControllerUtils.convertSuccessToXml("RsMessageRuleCalendar ${rsMessageRuleCalendar.id} updated"), contentType: "text/xml")
                }
                else {
                    render(text: errorsToXml(rsMessageRuleCalendar.errors), contentType: "text/xml")
                }
            }
            catch (e)
            {
                addError("default.couldnot.create", [RsMessageRuleCalendar, e.getMessage()])
                render(text: errorsToXml(this.errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.couldnot.create", [RsMessageRuleCalendar, "RsMessageRuleCalendar not found with id ${params.id}"])
            render(text: errorsToXml(this.errors), contentType: "text/xml")
        }
    }

    def save = {
        try {
            def calParams = [username: session.username]
            calParams.putAll(params);
            calParams.putAll(ControllerUtils.getClassProperties(params, RsMessageRuleCalendar))
            def rsMessageRuleCalendar = RsMessageRuleCalendar.addCalendar(calParams)
            if (!rsMessageRuleCalendar.hasErrors()) {
                render(text: ControllerUtils.convertSuccessToXml("RsMessageRuleCalendar ${rsMessageRuleCalendar.id} created"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(rsMessageRuleCalendar.errors), contentType: "text/xml")
            }
        }
        catch (e)
        {
            addError("default.couldnot.create", [RsMessageRuleCalendar, e.getMessage()])
            render(text: errorsToXml(this.errors), contentType: "text/xml")
        }
    }
}