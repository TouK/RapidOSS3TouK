class RapidInsightConnection extends Connection{
    String baseUrl;
    String username;
    String password;

    static constraints = {
        baseUrl(blank:false, nullable:false);
        username(blank:false, nullable:false);
        password(blank:false,nullable:false);
    };
}
