package auth
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: May 15, 2009
 * Time: 4:08:27 PM
 */
class ChannelUserInformation extends RsUserInformation {
    static searchable = {
        except = ["errors"];
    };
    String destination = "";
    org.springframework.validation.Errors errors;

    static constraints = {
        destination(blank: true, nullable: true)
        errors(nullable: true)
    }
    static transients = ["errors"];

    public String toString()
    {
        return " ${type} : ${destination}  ";
    }
}