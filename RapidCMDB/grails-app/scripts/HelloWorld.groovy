/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 * 
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
/**
 * Created by IntelliJ IDEA.
 * User: Pinar Kinikoglu
 * Date: Apr 2, 2008
 * Time: 9:16:40 AM
 * To change this template use File | Settings | File Templates.
 */
Author.list().each {it.remove()}
Publisher.list().each {it.remove()}
Book.list().each {it.remove()}

def auth = Author.add(name:"tayyip");
Author.add(name:"mustafa");
Author.add(name:"sezgin");
def publisher = Publisher.add(name:"akp");
Publisher.add(name:"akp1");
Publisher.add(name:"akp2");

def book1 = Book.add(name:"Zengin olmanin kolay yollari 1");
def book2 = Book.add(name:"Zengin olmanin kolay yollari 2");
def book3 = Book.add(name:"Kolay gemi alma yollari 1");
def book4 = Book.add(name:"Arap sermayesi");

auth.addRelation(books:[book1, book2, book3], publisher:publisher);
auth.removeRelation(books:[book1, book2, book3], publisher:publisher);

auth.addRelation(books:[book1, book2, book3], publisher:publisher);



def count = 0;
Author.list().each
{
    println it.toString()
    println it.asMap()
    println "GET"+Author.get(name:it.name);
    it.remove()
}
return Author.list()