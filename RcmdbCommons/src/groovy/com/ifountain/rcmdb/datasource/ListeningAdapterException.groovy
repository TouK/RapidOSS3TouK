package com.ifountain.rcmdb.datasource
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 3, 2009
 * Time: 9:21:58 AM
 * To change this template use File | Settings | File Templates.
 */
class ListeningAdapterException extends Exception{

    public ListeningAdapterException(String message) {
        super(message); //To change body of overridden methods use File | Settings | File Templates.
    }

    public ListeningAdapterException(String message, Throwable cause) {
        super(message, cause); //To change body of overridden methods use File | Settings | File Templates.
    }

    public static ListeningAdapterException adapterAlreadyStartedException(String adapterName)
    {
        return new ListeningAdapterException("Adapter ${adapterName} already started.")
    }
    public static ListeningAdapterException noAdapterDefined(String dsName)
    {
        return new ListeningAdapterException("No adapter defined in datasource ${dsName}.")
    }
    public static ListeningAdapterException adapterAlreadyStoppedException(String adapterName)
    {
        return new ListeningAdapterException("Adapter ${adapterName} already stopped.")
    }
    public static ListeningAdapterException adapterDoesNotExist(String adapterName)
    {
        return new ListeningAdapterException("Adapter ${adapterName} does not exist.")
    }
    public static ListeningAdapterException adapterAlreadyExists(String adapterName)
    {
        return new ListeningAdapterException("Adapter ${adapterName} already exists.")
    }
    public static ListeningAdapterException noListeningScript(String adapterName)
    {
        return new ListeningAdapterException("No listening script is defined for ${adapterName}.")
    }
    public static ListeningAdapterException listeningScriptExecutionException(String adapterName, String scriptName, String methodName, Throwable e)
    {
        return new ListeningAdapterException("Exception occurred while executing ${methodName} method of script ${scriptName} in adapter ${adapterName}. Reason:${e.getMessage()}", e)
    }
    public static ListeningAdapterException couldNotSubscribed(String adapterName, Throwable e)
    {
        return new ListeningAdapterException("Adapter ${adapterName} could not subscribed successfully. Reason:${e.getMessage()}", e)
    }
}