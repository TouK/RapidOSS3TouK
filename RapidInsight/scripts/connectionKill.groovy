import com.ifountain.core.connection.ConnectionManager;

def name=params.name;

if(!name)
{
	return "name parameter can not be empty"
}


ConnectionManager.removeConnection(name);

return "${name} is destoroyed"