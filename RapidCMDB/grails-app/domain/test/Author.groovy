package test;
class Author {       
    String name;
    static hasMany = [books: Book];
    static constraints = {             
        name(blank:false, unique:true);
    }                   
}
  