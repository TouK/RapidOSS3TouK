import com.ifountain.rcmdb.domain.util.ControllerUtils

if(!params.name)
{
	return ControllerUtils.convertErrorToXml("Name parameter is missing");
}
//all props in params goes to eventProps
def eventProps=[:];
eventProps.putAll(params);

//can change the props here
//eventProps.description="...";
//eventProps.remove("source");

def event=RsRiEvent.notify(eventProps);
if(!event.hasErrors())
{
	return ControllerUtils.convertSuccessToXml("Event ${params.name} added successfully.");
}
else
{
	return ControllerUtils.convertErrorToXml("Event ${params.name} can not be added. Reason ${event.errors}");
}



