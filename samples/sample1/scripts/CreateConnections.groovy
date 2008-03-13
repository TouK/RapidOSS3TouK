import models.*;
def name = "ds1";
def driver = "com.mysql.jdbc.Driver";
def url = "jdbc:mysql://localhost/test";
def username = "root";
def password = "root";

DatabaseConnection.create(name, driver, url, username, password);

name = "ds2"; 
url = "jdbc:mysql://192.168.1.100/test";
DatabaseConnection.create(name, driver, url, username, password);

name = "ds3"; 
url = "jdbc:mysql://192.168.1.102/test";
DatabaseConnection.create(name, driver, url, username, password);

name = "smartsDs";
def broker = "192.168.1.102:426" 
def domain = "INCHARGE-SA";
username = "admin";
password = "rcpass"
SmartsConnection.create(name, broker, domain, username, password);