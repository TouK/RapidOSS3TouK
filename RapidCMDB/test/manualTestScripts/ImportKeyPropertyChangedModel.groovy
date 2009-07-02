assert (Author.list().size == 0)
def impUtility = new ImportUtility()
def fname = "export.xml"
impUtility.importBothObjectsAndRelationsForAModelAndItsChildren(web,fname)
def author = Author.search("lastname:Steinbeck").results[0]
assert author.name == "John"
def books = author.myBooks 
assert books.size == 1
books.each{
	assert it.isbn == "isbn_GOW"
}

author = Author.search("lastname:Pamuk").results[0]
assert author.name == "Orhan"
books = author.myBooks
assert books.size == 2
books.each{
	assert (it.isbn == "isbn_BAK" || it.isbn == "isbn_Kar")
}

author = Author.search("lastname:Sagan").results[0]
assert author.name == "Karl"
books = author.myBooks
assert books.size == 1
books.each{
	assert (it.isbn == "isbn_Csms")
}

author = Author.search("lastname:Diamond").results[0]
assert author.name == "Jared"
books = author.myBooks
assert books.size == 2
books.each{
	assert (it.isbn == "isbn_TC" || it.isbn == "isbn_GGS")
}

return "Success"