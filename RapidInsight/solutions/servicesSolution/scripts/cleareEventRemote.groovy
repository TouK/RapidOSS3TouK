import com.ifountain.rcmdb.domain.util.ControllerUtils

if(!params.name)
{
	return ControllerUtils.convertErrorToXml("Name parameter is missing");
}

def existingEvent=RsEvent.get(name:params.name);
if(existingEvent)
{
	//will create journal, if given properties in params goes to historical event
	def eventProps=[:];
	eventProps.putAll(params);

	existingEvent.clear(true,eventProps);

	return ControllerUtils.convertSuccessToXml("Event ${params.name} cleared successfully.");
}
else
{
	return ControllerUtils.convertErrorToXml("Event ${params.name} not found. Can not clear");
}




