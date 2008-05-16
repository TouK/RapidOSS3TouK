package test;
class Author {
    static searchable = true;
    String name;
    static hasMany = [books: Book];
    static constraints = {             
        name(blank:false, unique:true);
    }                   
}
  