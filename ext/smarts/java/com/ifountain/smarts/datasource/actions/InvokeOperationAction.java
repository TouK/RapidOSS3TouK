/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 * 
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
/**
 * Created on Feb 19, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.datasource.actions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.util.SmartsPropertyHelper;
import com.smarts.remote.SmRemoteException;
import com.smarts.repos.*;
import com.ifountain.smarts.util.property.MRToEntry;
import com.ifountain.smarts.util.property.MRToEntryFactory;

public class InvokeOperationAction implements Action {

	public static final String MSG_SUBARGUMENTS_REQUIRED  = "ObjRef argument type requires className and instanceName as sub arguments.";
    private String className; 
    private Logger logger; 
    private String instanceName;
    private String opName;
    private List<String> opParams;
    private Object invokeResult;
    public InvokeOperationAction(Logger logger, String className, String instanceName, String opName, List<String> opParams) {
        this.logger = logger;
        this.className = className;
        this.instanceName = instanceName;
        this.opName = opName;
        this.opParams = opParams;
    }
    @Override
    public void execute(IConnection conn) throws Exception {
    	MR_AnyVal[] args = getArgsAsMRAnyValArray(conn, className, opName, opParams);
    	MR_AnyVal result = ((SmartsConnectionImpl)conn).getDomainManager().invokeOperation(className, instanceName, opName, args);

    	MRToEntry entry = MRToEntryFactory.getMRToEntry("returnVal", result); 
    	Object val = entry.getValue();
    	if(val instanceof HashMap[]){
    		HashMap[] mapArray = (HashMap[])val;
    		invokeResult = mapArray[0];
    	}
    	else{
    		Map<String, Object> record = new HashMap<String, Object>();
            record.put("element0", val);
            invokeResult = record;
    	}
    }
    
    public Object getInvokeResult() {
        return invokeResult;
    }
    
    /*private MR_AnyVal[] getArgsAsMRAnyValArray(IConnection ds, String className, String methodName,  List<String> stringArgs) throws IOException, SmRemoteException {
        String[] opargs = ((SmartsConnectionImpl)ds).getDomainManager().getOpArgs(className, methodName);
        if(opargs.length<stringArgs.size()) throw new SmRemoteException("Too many arguments provided for operation: " + methodName + ". Required " + opargs.length + " arguments.");
        StringBuffer opArgsBuffer = new StringBuffer();
        for (int i = 0; i < opargs.length; i++) {
            opArgsBuffer.append(opargs[i]).append(", ");
        }
        MR_AnyVal[] args = null;
        if(opargs.length == 0){
            args = new MR_AnyVal[0];
            return args;
        }
        args = new MR_AnyVal[stringArgs.size()];
        for (int i = 0; i < stringArgs.size(); i++) {
            String oparg = opargs[i];
            int argtype = ((SmartsConnectionImpl)ds).getDomainManager().getArgType(className, methodName, oparg);
            logger.debug("Argument type for " + oparg + "is " + argtype);
            args[i] = SmartsPropertyHelper.getAsMrAnyVal(argtype, (String)stringArgs.get(0));
            logger.debug("Arg type for primitive argument is: " + args[i].getType());
            if(args[i] == null){
                throw new IOException("Unsupported argument type: " + argtype);
            }
        }
        return args;
    }*/
    private MR_AnyVal[] getArgsAsMRAnyValArray(IConnection ds, String className, String methodName,  List stringArgs) throws IOException, SmRemoteException {
    	String[] opargs = ((SmartsConnectionImpl)ds).getDomainManager().getOpArgs(className, methodName);    	if(opargs.length<stringArgs.size()) throw new SmRemoteException("Too many arguments provided for operation: " + methodName + ". Required " + opargs.length + " arguments.");
    	
    	MR_AnyVal[] args = null;
    	if(opargs.length == 0){
    		args = new MR_AnyVal[0];
    		return args;
    	}
    	args = new MR_AnyVal[stringArgs.size()];
    	for (int i = 0; i < stringArgs.size(); i++) {
    		String oparg = opargs[i];
    		int argtype = ((SmartsConnectionImpl)ds).getDomainManager().getArgType(className, methodName, oparg);
    		logger.debug("Argument type for " + oparg + "is " + argtype);
    		args[i] = getArgAsMRAnyVal(argtype, stringArgs.get(i), methodName);
    		logger.debug("Arg type for primitive argument is: " + args[i].getType());
    		if(args[i] == null){
    			throw new IOException("Unsupported argument type: " + argtype);
    		}
    	}
    	return args;
    }

    public MR_AnyVal getArgAsMRAnyVal(int argtype, Object args, String methodName) throws IOException{
        MR_AnyVal val = null;
        logger.debug("Argument to convert into MRAnyVal: " + args);
        if(argtype == MR_ValType.MR_OBJREF){
            List listArgs = (List)args;
            if (listArgs.get(0) instanceof List)
        	{
            	logger.debug("Processing OBJREF_SET argument");
            	MR_AnyValObjRefSet set = null;
            	if (methodName.equalsIgnoreCase("makeOneVuCircuit")) {
            		set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
            	}
            	if (methodName.equalsIgnoreCase("makeHOCircuit")) {
            		set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
            	}
            	if (methodName.equalsIgnoreCase("makeHOCircuitWithoutEndCTP")) {
            		set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
            	}
            	if (methodName.equalsIgnoreCase("makeLOCircuitWithoutEndCTP")) {
            		set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
            	}
                if (methodName.equalsIgnoreCase("makeLOCircuit")) {
                    set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
                }
            	val = set;
			}
        	else
        	{
                logger.debug("Processing ObjRef argument with arguments: " + (String)listArgs.get(0) + " and " + (String)listArgs.get(1));
                if(listArgs.size()!=2) throw new IOException(MSG_SUBARGUMENTS_REQUIRED);
                MR_AnyValObjRef objRef = new MR_AnyValObjRef(new MR_Ref((String)listArgs.get(0), (String)listArgs.get(1)));
                val = objRef;
        	}
        }
        else if(argtype == MR_ValType.MR_ANYVALARRAY){
            logger.debug("Processing Array argument");
            List listArgs = (List)args;
            MR_AnyValArray mr_anyValArray = null;
            if (methodName.equalsIgnoreCase("makeOpticalNetworkElement")) {
				mr_anyValArray = processMakeOpticalNetworkElementArrayArgument(listArgs);
			}
            else if (methodName.equalsIgnoreCase("makePG")) {
            	mr_anyValArray = processMakePGArrayArgument(listArgs);
			}
            else {
            	mr_anyValArray = processArrayArgument(listArgs);
            }
            val = mr_anyValArray;
        }
        else if(argtype == MR_ValType.MR_OBJREF_SET){
        	logger.debug("Processing OBJREF_SET argument");
            List listArgs = (List)args;
            MR_AnyValObjRefSet set = null;
        	
        	if (methodName.equalsIgnoreCase("makeOneVuCircuit")) 
        	{
        		set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
        	}
        	if (methodName.equalsIgnoreCase("makeHOCircuit")) 
        	{
        		set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
        	}
        	if (methodName.equalsIgnoreCase("makeHOCircuitWithoutEndCTP")) 
        	{
        		set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
        	}
        	if (methodName.equalsIgnoreCase("makeLOCircuitWithoutEndCTP")) {
        		set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
        	}
            if (methodName.equalsIgnoreCase("makeLOCircuit")) {
                set = processMakeOneVuCircuitObjRefSetArgument(listArgs);
            }
        	val = set;
        }
        else{
            if(args instanceof String)
            {
                logger.debug("Processing primitive argument: " + (String)args);
                val = SmartsPropertyHelper.getAsMrAnyVal(argtype, (String)args);
                logger.debug("Arg type for primitive argument is: " + val.getType());
                if(val == null){
                    throw new IOException("Unsupported argument type: " + argtype);
                }
            }
            else
            {
                throw new IOException("Unsupported argument : " + args + ".  Primitive argurments can only be passed string.");
            }
        }
        return val;
    }

	protected MR_AnyValObjRefSet processMakeOneVuCircuitObjRefSetArgument(List args) {
		MR_Ref [] refs = new MR_Ref[args.size()];
		for (int i = 0; i < refs.length; i++) {
			List list = (List) args.get(i);
			refs[i] = new MR_Ref((String) list.get(0), (String) list.get(1));
			logger.debug("Created MR_Ref" + i + " as : " + refs[i]);
		}
		
		MR_AnyValObjRefSet anyValObjRefSet = new MR_AnyValObjRefSet(refs);
		
		return anyValObjRefSet;
	}

	protected MR_AnyValArray processMakeOpticalNetworkElementArrayArgument(List args) {

		MR_AnyVal[] vals = new MR_AnyVal[args.size()];
		MR_AnyValArray mr_anyValArray = new MR_AnyValArray(vals);
		for (int i = 0; i < vals.length; i++) {
			MR_AnyVal[] value = new MR_AnyVal[3];
			value[0] = new MR_AnyValUnsignedInt(i+1);
			List list = (List) args.get(i);
			value[1] = new MR_AnyValString((String) list.get(0));
			value[2] = new MR_AnyValString((String) list.get(1));
			vals[i] = new MR_AnyValArray(value);
		}
		return mr_anyValArray;
	}

	protected MR_AnyValArray processMakePGArrayArgument(List args) {
		MR_AnyVal[] vals = new MR_AnyVal[args.size()];
		MR_AnyValArray mr_anyValArray = new MR_AnyValArray(vals);
		for (int i = 0; i < vals.length; i++) {
			MR_AnyVal[] value = new MR_AnyVal[2];
			value[0] = new MR_AnyValUnsignedInt(i+1);
			List list = (List) args.get(i);
			value[1] = new MR_AnyValString((String) list.get(0));
			vals[i] = new MR_AnyValArray(value);
		}
		return mr_anyValArray;
	}

	protected MR_AnyValArray processArrayArgument(List args) {

		MR_AnyVal[] vals = new MR_AnyVal[args.size()];
		MR_AnyValArray mr_anyValArray = new MR_AnyValArray(vals);
		for (int i = 0; i < args.size(); i++) {
		    logger.debug("Array element" + i + ": " + (String)args.get(i));
		    vals[i] = new MR_AnyValString((String)args.get(i));
		}
		return mr_anyValArray;
	}

    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

