import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;


Logger.getRootLogger().setLevel(Level.DEBUG);
Logger.getLogger("grails.app.realm.JsecDbRealm").setLevel(Level.DEBUG);
Logger.getLogger(org.compass.core.transaction.LocalTransaction.class).setLevel(Level.INFO);
Logger.getLogger(org.codehaus.groovy.grails.plugins.searchable.compass.DefaultSearchableMethodFactory.class).setLevel(Level.INFO);
Logger.getLogger(org.codehaus.groovy.grails.plugins.searchable.compass.search.DefaultSearchMethod.class).setLevel(Level.INFO);

output="  all loggers: :";
LogManager.getCurrentLoggers().each{
	output+="<br> ${it.name} ${it.level}";
}

return output;
