import com.ifountain.core.connection.ConnectionManager;

def name=params.name;
def disconnect=params.disconnect;

if(!name)
{
	return "name parameter can not be empty"
}

if(disconnect)
{
    def pools=ConnectionManager.pools;
    pools[name].getBorrowedConnections().each{ con ->
      Thread.start(){
          try{
            con.disconnect();
          }
          catch(e)
          {
            logger.warn("Exception occured while disconnecting connection : ${name} ${con}")
          }
      }

    }
}


ConnectionManager.removeConnection(name);

return "${name} is destoroyed"