package com.ifountain.rcmdb.execution
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 7, 2009
 * Time: 2:53:10 PM
 * To change this template use File | Settings | File Templates.
 */
class ExecutionContextManager {
    ExecutionContextThreadLock<Stack<ExecutionContext>> contextStack = new ExecutionContextThreadLock<Stack<ExecutionContext>>();
    private static ExecutionContextManager manager;
    protected ExecutionContextManager()
    {
    }
    public static ExecutionContextManager getInstance()
    {
        if(manager == null)
        {
            manager = new ExecutionContextManager();
        }
        return manager;
    }
    public static void destroy()
    {
        manager = null;   
    }
    public synchronized ExecutionContext getExecutionContext()
    {
        if (hasExecutionContext())
        {
            return contextStack.get().peek();
        }
        else
        {
            return null;
        }
    }
    
    public synchronized void clearExecutionContexts()
    {
        contextStack.get().clear();
    }
    public synchronized boolean hasExecutionContext()
    {
        return !contextStack.get().isEmpty();
    }
    public synchronized ExecutionContext endExecutionContext()
    {
        return contextStack.get().pop();
    }
    public synchronized ExecutionContext startExecutionContext(Map contextParameters)
    {
        ExecutionContext newContext = new ExecutionContext();
        if (hasExecutionContext())
        {
            ExecutionContext prevContext = getExecutionContext();
            newContext.putAll(prevContext);
        }
        newContext.putAll(contextParameters);
        contextStack.get().push(newContext);
        return newContext;
    }
}

class ExecutionContextThreadLock<T> extends InheritableThreadLocal {

    protected Object initialValue() {
        return new Stack<ExecutionContext>(); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected Object childValue(Object parentValue) {
        Stack<ExecutionContext> parentStack = (Stack<ExecutionContext>) parentValue;
        try {
            ExecutionContext topContext = parentStack.peek();
            get().push(topContext);
        }
        catch (java.util.EmptyStackException e) {/*ignore*/}
        return get();
    }

}