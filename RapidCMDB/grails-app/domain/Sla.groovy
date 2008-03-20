class Sla {

    String level;
    Customer customer;
    Service service;

    static belongsTo = [Customer, Service];    
}
