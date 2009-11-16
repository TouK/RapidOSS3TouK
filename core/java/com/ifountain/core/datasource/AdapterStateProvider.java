package com.ifountain.core.datasource;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 16, 2009
 * Time: 9:13:59 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AdapterStateProvider {
    public static final String NOT_STARTED = "NotStarted";
    public static final String INITIALIZING = "Initializing";
    public static final String INITIALIZED = "Initialized";
    public static final String STARTING = "Starting";
    public static final String STARTED = "Started";
    public static final String STOPPING = "Stopping";
    public static final String STOPPED_WITH_EXCEPTION = "StoppedWithException";
    public static final String STOPPED = "Stopped";
    public void setState(String state);
    public String getState();
}
