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

    public static ListeningAdapterException adapterAlreadyStartedException(Long datasourceId)
    {
        return new ListeningAdapterException("Adapter with datasource id ${datasourceId} already started.")
    }
    public static ListeningAdapterException noAdapterDefined(Long dsId)
    {
        return new ListeningAdapterException("No adapter defined in datasource with id ${dsId}.")
    }
    public static ListeningAdapterException adapterAlreadyStoppedException(Long datasourceId)
    {
        return new ListeningAdapterException("Adapter with datasource id ${datasourceId} already stopped.")
    }

    public static ListeningAdapterException stoppingStateException(Long datasourceId, String actionName)
    {
        return new ListeningAdapterException("You cannot ${actionName} adapter withy datasource id ${datasourceId} while it is in stopping state.")
    }
    public static ListeningAdapterException runnerDoesNotExist(Long datasourceId)
    {
        return new ListeningAdapterException("Adapter runner for adapter with datasource id ${datasourceId} has not created.")
    }
    public static ListeningAdapterException adapterAlreadyExists(Long datasourceId)
    {
        return new ListeningAdapterException("Adapter with datasource id ${datasourceId} already exists.")
    }
    public static ListeningAdapterException noListeningScript(Long datasourceId)
    {
        return new ListeningAdapterException("No listening script is defined for adapter with datasource id ${datasourceId}.")
    }
    public static ListeningAdapterException listeningScriptExecutionException(Long datasourceId, String scriptName, String methodName, Throwable e)
    {
        return new ListeningAdapterException("Exception occurred while executing ${methodName} method of script ${scriptName} in adapter with datasource id ${datasourceId}. Reason:${e.getMessage()}", e)
    }
    public static ListeningAdapterException couldNotSubscribed(Long datasourceId, Throwable e)
    {
        return new ListeningAdapterException("Adapter with datasource id ${datasourceId} could not subscribed successfully. Reason:${e.getMessage()}", e)
    }
}