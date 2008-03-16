import com.ifountain.comp.utils.CaseInsensitiveMap
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.ConnectionParameterSupplier
import org.apache.log4j.Logger
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean

class ConnectionService implements InitializingBean, DisposableBean, ConnectionParameterSupplier{
    boolean transactional = true

    public ConnectionParam getConnectionParam(String connConfigName) {
        def connection = Connection.findByName(connConfigName);
        def optProps = new CaseInsensitiveMap(connection.properties);        
        return new ConnectionParam(connection.getClass().getName(), connection.name, connection.connectionClass,optProps)
    }
    public void afterPropertiesSet()
    {
        ConnectionManager.initialize (Logger.getRootLogger(), this, this.getClass().getClassLoader());
    }

    public void destroy()
    {
        ConnectionManager.destroy();
    }

    
}
