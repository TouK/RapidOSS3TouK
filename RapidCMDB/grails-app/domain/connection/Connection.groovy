package connection;
class Connection {
       String name;
       String connectionClass;

       static constraints = {
            name(unique:true, blank:false, nullable:false);
            connectionClass(blank:false, nullable:false);
       };
       static mapping = {
	      table 'base_connection'
	  } 

       String toString(){
           return "$name";
       }
}
