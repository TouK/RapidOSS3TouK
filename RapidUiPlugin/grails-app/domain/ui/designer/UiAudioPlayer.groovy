package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 16, 2009
 * Time: 5:47:40 PM
 * To change this template use File | Settings | File Templates.
 */
class UiAudioPlayer extends UiComponent{
  static searchable = {
        storageType "FileAndMemory"
    };
    String url = "";
    String soundFile = "";
    String playCondition = "";
    String suggestionAttribute = "";
    Long volume = 100;
    Long timeout = 0;
    Long pollingInterval = 0;
    org.springframework.validation.Errors errors ;
    static datasources = [:]
    static relations = [:]
    static constraints={
        url(nullable:true, blank:true)
        volume(nullable:true)
        playCondition(nullable:true, blank:true)
        soundFile(nullable:false, blank:false)
        timeout(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}