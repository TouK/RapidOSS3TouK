package test
class Book {
    String title;
    String description;
    Date dateCreated = new Date();
    Author author;
    static belongsTo = Author;
    static constraints = {
        title(blank:false, unique:'author');
    }
}
