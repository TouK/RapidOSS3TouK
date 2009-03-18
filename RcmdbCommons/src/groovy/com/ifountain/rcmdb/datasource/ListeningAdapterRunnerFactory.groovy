package com.ifountain.rcmdb.datasource
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 13, 2009
 * Time: 9:53:36 AM
 * To change this template use File | Settings | File Templates.
 */
class ListeningAdapterRunnerFactory {
    private static ListeningAdapterRunner runner;

    public static ListeningAdapterRunner getRunner(datasourceId) {
        if (runner != null) return runner;
        return new ListeningAdapterRunner(datasourceId);
    }

    public static void setRunner(ListeningAdapterRunner mockRunner) {
        runner = mockRunner;
    }
}