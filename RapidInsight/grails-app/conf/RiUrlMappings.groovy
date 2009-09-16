/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 6, 2009
 * Time: 4:34:55 PM
 * To change this template use File | Settings | File Templates.
 */
class RiUrlMappings {
    static mappings = {
        "/rsBrowser/classes" {
            controller = "rsBrowser"
            action = "classes";
        }
        "/rsBrowser/searchWithQuery"{
            controller = "rsBrowser"
            action = "searchWithQuery"
        }
        "/rsBrowser/$domain/" {
            controller = "rsBrowser"
            action = "listDomain";
        }
        "/rsBrowser/$domain/search/$searchQuery?" {
            controller = "rsBrowser"
            action = "search";
        }
        "/rsBrowser/$domain/$id" {
            controller = "rsBrowser"
            action = "show";
        }
        "/rsBrowser/$domain/propertiesAndOperations" {
            controller = "rsBrowser"
            action = "propertiesAndOperations";
        }
    }
}