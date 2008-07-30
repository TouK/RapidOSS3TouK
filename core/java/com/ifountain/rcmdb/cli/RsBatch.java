package com.ifountain.rcmdb.cli;

import com.ifountain.comp.cli.CommandLineUtility;
import com.ifountain.comp.exception.RCliException;
import com.ifountain.comp.exception.RapidException;
import com.ifountain.comp.utils.HttpUtils;
import com.ifountain.rcmdb.exception.RCMDBException;
import com.ifountain.rcmdb.config.StartConfig;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Level;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class RsBatch extends CommandLineUtility {
    public static final String TOOL_NAME = "rsbatch";
	public static final String PASSWORD_OPTION = "password";
    public static final String USERNAME_OPTION = "username";
    public static final String LOGLEVEL_OPTION = "loglevel";
    public static final String COMMANDFILE_OPTION = "commandfile";
    public static final String HOST_OPTION = "host";
    public static final String PORT_OPTION = "port";


    private String username;
    private String commandFile;
    private String password;
    private String loglevel = Level.INFO.toString();
    private int port;
    private String host;
    private HttpUtils httpUtils;

    public static final String DELIMETER = "|";


    public RsBatch() {
        super(TOOL_NAME, TOOL_NAME + ".log");
        this.httpUtils = new HttpUtils();
    }


    public static void main(String[] args) {
        RsBatch rsBatch = new RsBatch();
        try {
            rsBatch.run(args);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }


    protected String getBaseDirectory() {
        return StartConfig.getRsHome();
    }

    public Options createOptions() {
        OptionBuilder.withArgName(COMMANDFILE_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("File that contains batch REST API commands. Command file must be specified relative to RapidServer insallation directory. Entries in command file must be in '|' delimeted format.");
        Option commandfile = OptionBuilder.create(COMMANDFILE_OPTION);

        OptionBuilder.withArgName(USERNAME_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("User name required to authenticate to Rapid Server");
        Option username = OptionBuilder.create(USERNAME_OPTION);

        OptionBuilder.withArgName(PASSWORD_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("Password required to authenticate to Rapid Server");
        Option password = OptionBuilder.create(PASSWORD_OPTION);

        OptionBuilder.withArgName(HOST_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("Host address of Rapid Server");
        Option host = OptionBuilder.create(HOST_OPTION);

        OptionBuilder.withArgName(PORT_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.withDescription("Port required to connect to Rapid Server");
        Option port = OptionBuilder.create(PORT_OPTION);

        OptionBuilder.withArgName(LOGLEVEL_OPTION);
        OptionBuilder.hasArg();
        OptionBuilder.isRequired(false);
        OptionBuilder.withDescription("Log level of " + getToolName() + ". Valid log levels: "
                + Level.ALL.toString()
                + ", "
                + Level.DEBUG.toString()
                + ", "
                + Level.INFO.toString()
                + ", "
                + Level.WARN.toString()
                + ", "
                + Level.ERROR.toString()
                + ", "
                + Level.FATAL.toString() + ", " + Level.OFF.toString());
        Option loglevel = OptionBuilder.create(LOGLEVEL_OPTION);

        Options options = new Options();
        options.addOption(host);
        options.addOption(port);
        options.addOption(commandfile);
        options.addOption(username);
        options.addOption(password);
        options.addOption(loglevel);

        return options;
    }

    protected String makeCall(String call) throws RCMDBException {
        logger.debug("Making api call.");
        String response = "";
        try {
            response = httpUtils.doPostRequest(call, new HashedMap());
        }
        catch (Exception e) {
            logger.warn("Could not access the URL: " + call);
            throw new RCMDBException(RCMDBException.ERROR_CONNECT_SERVER, new Object[0]);
        }
        logger.info("Response XML for call: " + call);
        logger.info(response);
        return response;
    }

    protected void processOption(Option option) throws RapidException {
        logger.debug("Processing option <" + option + ">");
        if (option.getOpt().equals(COMMANDFILE_OPTION)) {
            this.commandFile = getRequiredOptionValue(option);
            File file = new File(StartConfig.getRsHome() + "/" + commandFile);
            if (!file.exists()) {
                logger.warn("Cannot find specified command file: " + file.getAbsolutePath());
                throw new RCMDBException(RCMDBException.FILE_NOT_FOUND,
                        new Object[]{file.getAbsolutePath()});
            }
        } else if (option.getOpt().equals(HOST_OPTION)) {
            this.host = getRequiredOptionValue(option);
        } else if (option.getOpt().equals(PORT_OPTION)) {
            String portString = getRequiredOptionValue(option);
            try {
                this.port = Integer.parseInt(portString);
            }
            catch (NumberFormatException e) {
                throw new RCliException(RCliException.INVALID_COMMAND_LINE_OPTION_VALUE, new Object[]{portString, PORT_OPTION});
            }
        }
        else if (option.getOpt().equals(USERNAME_OPTION)) {
            this.username = getRequiredOptionValue(option);
        }

        else if (option.getOpt().equals(PASSWORD_OPTION)) {
            this.password = getRequiredOptionValue(option);
        }

        else if (option.getOpt().equals(LOGLEVEL_OPTION)) {
            String level = getRequiredOptionValue(option);
            if (level.equals(Level.ALL.toString()) ||
                    level.equals(Level.DEBUG.toString()) ||
                    level.equals(Level.INFO.toString()) ||
                    level.equals(Level.WARN.toString()) ||
                    level.equals(Level.ERROR.toString()) ||
                    level.equals(Level.FATAL.toString()) ||
                    level.equals(Level.OFF.toString())) {
                this.loglevel = level;
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    protected void authenticate(String login, String pass)throws RCMDBException {
        logger.debug("Authenticating to RapidCMDB with username <" + login + ">");
        String response = "";
         try {
             Map params = new HashMap();
             params.put("login", login);
             params.put("password", pass);
             params.put("format", "xml");
             response = httpUtils.doPostRequest("http://" + host + ":" + port + "/RapidCMDB/auth/signIn", params);
         } catch (Exception e) {
             e.printStackTrace();
             logger.fatal("Could not connect to RapidCMDB");
             throw new RCMDBException(RCMDBException.ERROR_CONNECT_SERVER, new Object[0]);
         }
        if (!(response.indexOf("Successful") > -1)) {
            logger.fatal("Could not authenticate to RapidCMDB with username <" + login + ">");
            throw new RCMDBException(RCMDBException.CANNOT_AUTHENTICATE, new Object[]{login});
        }
        logger.info("Successfully authenticated.");

    }

    public void execute() throws RapidException {
        logger.debug("rsbatch is starting");
        authenticate(getUsername(), getPassword());
        File configFile = new File(StartConfig.getRsHome() + File.separator + commandFile);
        if (!configFile.exists()) {
            logger.fatal("Cannot find " + configFile.getAbsolutePath());
            throw new RCMDBException(RCMDBException.FILE_NOT_FOUND, new Object[]{configFile.getAbsolutePath()});
        }
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(configFile);
            br = new BufferedReader(fr);

            String line = "";
            while ((line = br.readLine()) != null) {
                logger.debug("Processing line: " + line);
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                try {
                    processAction(line);
                }
                catch (Exception e) {
                    logger.warn("Skipping line <" + line + "> reason: " + e.getMessage());
                    continue;
                }
            }
        }
        catch (IOException e) {
            throw new RCMDBException(RCMDBException.FILE_IO_EXCEPTION, new Object[]{configFile.getAbsolutePath(), e.getMessage()});
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                }
                catch (IOException e) {
                }
            }
        }
        logger.info("rsbatch terminated successfully.");
    }

    protected void processAction(String actionParameters) throws Exception {
        String callPrefix = "http://" + host + ":" + port;
        String call = callPrefix + actionParameters;

        String response = makeCall(call);
//		if((actionParameters.indexOf("User/update") > -1) && (response.indexOf(RifeTemplateUtils.TemplateTagNames.SUCCESSFUL_TEMPLATE_TAG) > -1))
//		{
//			updateToolPassword(actionParameters);
//		}
    }

//    protected void updateToolPassword(String actionParameters) {
//    	if (actionParameters.indexOf(AuthenticationConstants.PARAMETER_USERNAME + "=" + this.username) > -1)
//    	{
//			String 	newPass1 = null,
//					newPass2 = null,
//					token;
//			StringTokenizer tokenizer = new StringTokenizer(actionParameters,"=?&");
//    		while(tokenizer.hasMoreTokens())
//    		{
//    			token = tokenizer.nextToken();
//    			if (token.equals(AuthenticationConstants.PARAMETER_NEWPASSWORD1) && tokenizer.hasMoreTokens()) {
//					newPass1 = tokenizer.nextToken();
//				}
//    			if (token.equals(AuthenticationConstants.PARAMETER_NEWPASSWORD2) && tokenizer.hasMoreTokens()) {
//    				newPass2 = tokenizer.nextToken();
//    			}
//    		}
//    		if (newPass1 != null && newPass1.equals(newPass2))
//    		{
//				this.password = newPass1;
//			}
//		}
//	}

    public void setPort(int port) {
        this.port = port;
    }

    public String getLoglevel() {
        return loglevel;
    }

    public String getCommandFile() {

        return commandFile;
    }


    public void setCommandFile(String commandFile) {

        this.commandFile = commandFile;
    }


    public void setPassword(String password) {

        this.password = password;
    }


    public void setUsername(String username) {

        this.username = username;
    }


    public String getHost() {
        return host;
    }


    public void setHost(String host) {
        this.host = host;
    }

    protected void validateArgs() throws RCliException {
    }
}
