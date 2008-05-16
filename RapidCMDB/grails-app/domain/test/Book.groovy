package test
class Book {
    static searchable = true;
    String title;
    String description;
    Date dateCreated = new Date();
    Author author;
    static belongsTo = Author;
    static constraints = {
        title(blank:false, unique:'author');
    }
}
  