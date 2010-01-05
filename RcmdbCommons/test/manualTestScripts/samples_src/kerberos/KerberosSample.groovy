package kerberos



public class KerberosSample
{

    public static void main(String[] args)
    {
        def confFilePath="D:/Ideaworkspace/RapidModules/RapidCMDB/test/manualTestScripts/samples_src/kerberos/kerberos_jaas.conf";
        def authenticator=new KerberosAuthenticator("KERBEROS.MOLKAY","192.168.1.201",confFilePath);

        authenticator.authenticate ("administrator","admin");


    }
}