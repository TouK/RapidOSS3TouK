package com.ifountain.rui.designer
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Oct 20, 2009
 * Time: 3:36:09 PM
 */
class DesignerSpaceClassLoaderFactory {

    private static GroovyClassLoader gcl;
    public static GroovyClassLoader getDesignerClassLoader() {
        if (gcl != null) {
            return gcl;
        }
        return new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public static void setDesignerClassLoader(GroovyClassLoader classLoader) {
        gcl = classLoader;
    }
}