package connection;
class Connection {
       static searchable = {
        except = [];
    	};
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    String name ="";
    
    String connectionClass ="";
    

    static hasMany = [:]

       static constraints = {
             name(blank:false,nullable:false,key:[])
        
     connectionClass(blank:true,nullable:true)
       };
       
       static mappedBy=[:]
    static belongsTo = []
    static transients = [];

       String toString(){
           return "$name";
       }
}
