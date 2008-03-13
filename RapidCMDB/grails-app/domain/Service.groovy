import javax.persistence.*;

class Service {
    static hasMany = [slas:ServiceLevelAgree];
    @Id
    String name;
}
