import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;


Logger.getLogger("com.ifountain.core.connection").setLevel(Level.DEBUG);

output="  all loggers: :";
LogManager.getCurrentLoggers().each{
	output+="<br> ${it.name} ${it.level}";
}

return output;
