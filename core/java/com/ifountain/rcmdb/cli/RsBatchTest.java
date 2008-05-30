package com.ifountain.rcmdb.cli;

import com.ifountain.rcmdb.test.util.RCMDBTestCase;
import com.ifountain.rcmdb.exception.RCMDBException;
import com.ifountain.comp.test.util.file.TestFile;
import com.ifountain.comp.test.util.file.FileTestUtils;
import com.ifountain.comp.exception.RCliException;

import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.log4j.Level;

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
public class RsBatchTest extends RCMDBTestCase{
    private String hostName;
    private String port;
    private String username = "rsadmin";
    private String password = "changeme";
    String testCommandFile = "testFile.txt";
    File file = new TestFile(testCommandFile);
	protected void setUp() throws Exception {

		super.setUp();
        hostName ="localhost";
        port = "9191";
        FileTestUtils.generateFile(file.getPath(), "");
	}

    protected void tearDown() throws Exception
    {
        super.tearDown();
        FileTestUtils.deleteFile(file);
    }

	public void testCreateOptions() throws Exception {

		RsBatch tool = new RsBatch();
		Options options = tool.getOptions();
		assertEquals(6, options.getOptions().size());
		for (Iterator iter = options.getOptions().iterator(); iter.hasNext();) {
			Option element = (Option) iter.next();
			if (element.getArgName().equals(RsBatch.USERNAME_OPTION)) {
				assertTrue(element.hasArg());
                assertTrue(element.isRequired());
				assertEquals("User name required to authenticate to Rapid Suite", element.getDescription());
			}
			else if (element.getArgName().equals(RsBatch.COMMANDFILE_OPTION)) {
				assertTrue(element.hasArg());
                assertTrue(element.isRequired());
				assertEquals("File that contains batch REST API commands. Command file must be specified relative to RapidSuite insallation directory. Entries in command file must be in '|' delimeted format.", element.getDescription());
			}
			else if (element.getArgName().equals(RsBatch.PASSWORD_OPTION)) {
				assertTrue(element.hasArg());
                assertTrue(element.isRequired());
				assertEquals("Password required to authenticate to Rapid Suite", element.getDescription());
			}
			else if (element.getArgName().equals(RsBatch.HOST_OPTION)) {
			    assertTrue(element.hasArg());
			    assertTrue(element.isRequired());
			    assertEquals("Host address of Rapid Suite", element.getDescription());
			}
			else if (element.getArgName().equals(RsBatch.PORT_OPTION)) {
			    assertTrue(element.hasArg());
                assertTrue(element.isRequired());
			    assertEquals("Port required to connect to Rapid Suite", element.getDescription());
			}
			else if (element.getArgName().equals(RsBatch.LOGLEVEL_OPTION)) {
				assertTrue(element.hasArg());
                assertFalse(element.isRequired());
				assertEquals("Log level of " + tool.getToolName() + ". Valid log levels: " + Level.ALL.toString()+ ", " + Level.DEBUG.toString() + ", " + Level.INFO.toString() + ", " + Level.WARN.toString()
						+ ", " + Level.ERROR.toString() + ", " + Level.FATAL.toString() + ", " + Level.OFF.toString(), element.getDescription());
			}
			else {
				fail("Invalid option: " + element.getArgName());
			}

		}

	}

//	public void testProcessActionCallsUpdatePwdIfPwdCanBeUpdatedSuccessfully() throws Exception {
//		String actionParams = "User/update";
//		RsBatch commandLine = new RsBatch(){
//			protected void updateToolPassword(String actionParameters) {
//				this.setPassword("newPassword");
//			}
//
//			protected String makeCall(String call) throws RapidManagerException{
//				return RifeTemplateUtils.TemplateTagNames.SUCCESSFUL_TEMPLATE_TAG + "aaa";
//			}
//		};
//		assertNull(commandLine.getPassword());
//		commandLine.processAction(actionParams);
//		assertEquals("newPassword", commandLine.getPassword());
//
//	}

//	public void testProcessActionDoesNotCallUpdatePwdIfPwdCannotBeUpdatedSuccessfully() throws Exception {
//		String actionParams = "User/update";
//		RsBatch commandLine = new RsBatch(){
//			protected void updateToolPassword(String actionParameters) {
//				this.setPassword("newPassword");
//			}
//
//			protected String makeCall(String call) throws RapidManagerException{
//				return "<Error>aaa";
//			}
//		};
//		assertNull(commandLine.getPassword());
//		commandLine.processAction(actionParams);
//		assertNull(commandLine.getPassword());
//	}

	public void testGettingArguments() throws Exception {

        String[] args = new String[] {"-" + RsBatch.HOST_OPTION, hostName,
                                      "-" + RsBatch.PORT_OPTION, port,
                                      "-" + RsBatch.COMMANDFILE_OPTION, testCommandFile,
                                      "-" + RsBatch.LOGLEVEL_OPTION, Level.DEBUG.toString(),
                                      "-" + RsBatch.USERNAME_OPTION, username,
                                      "-" + RsBatch.PASSWORD_OPTION, password};
		RsBatch tool = new RsBatch();
		tool.parseArgs(args);
		assertEquals(username, tool.getUsername());
		assertEquals(password, tool.getPassword());
		assertEquals(testCommandFile, tool.getCommandFile());
		assertEquals(Level.DEBUG.toString(), tool.getLoglevel());
	}

	public void testInvalidLogLevelIsIgnoredAndSetToINFO() throws Exception {
        String[] args = new String[] {"-" + RsBatch.HOST_OPTION, hostName,
                                      "-" + RsBatch.PORT_OPTION, port,
                                      "-" + RsBatch.COMMANDFILE_OPTION, testCommandFile,
                                      "-" + RsBatch.LOGLEVEL_OPTION, "invalid",
                                      "-" + RsBatch.USERNAME_OPTION, username,
                                      "-" + RsBatch.PASSWORD_OPTION, password};
		RsBatch tool = new RsBatch();
		tool.createOptions();
		tool.parseArgs(args);
		assertEquals(Level.INFO.toString(), tool.getLoglevel());
	}




	public void testEmptyUsernamePrintsUsage() throws Exception {

		String[] args = new String[] {"-" + RsBatch.HOST_OPTION, hostName,
                                      "-" + RsBatch.PORT_OPTION, port,
                                      "-" + RsBatch.COMMANDFILE_OPTION, testCommandFile,
                                      "-" + RsBatch.USERNAME_OPTION, "",
                                      "-" + RsBatch.PASSWORD_OPTION, password};
		RsBatch console = new RsBatch();
		try {
            console.parseArgs(args);
			fail("Should throw exception because username cannot be empty");
		}
		catch(RCliException e){
			assertEquals(new RCliException(RCliException.EMPTY_ARGUMENT_FOR_COMMAND_LINE_OPTION, new Object[] { RsBatch.USERNAME_OPTION }).getMessage(), e.getMessage());
		}
	}


    public void testEmptyHostPrintsUsage() throws Exception {

        String[] args = new String[] {"-" + RsBatch.HOST_OPTION, " ",
                                      "-" + RsBatch.PORT_OPTION, port,
                                      "-" + RsBatch.COMMANDFILE_OPTION, testCommandFile,
                                      "-" + RsBatch.USERNAME_OPTION, username,
                                      "-" + RsBatch.PASSWORD_OPTION, password};
        RsBatch console = new RsBatch();
        try {
            console.parseArgs(args);
            fail("Should throw exception because username cannot be empty");
        }
        catch(RCliException e){
            assertEquals(new RCliException(RCliException.EMPTY_ARGUMENT_FOR_COMMAND_LINE_OPTION, new Object[] { RsBatch.HOST_OPTION }).getMessage(), e.getMessage());
        }
    }



    public void testEmptyPortPrintsUsage() throws Exception {
        String[] args = new String[] {"-" + RsBatch.HOST_OPTION, hostName,
                                      "-" + RsBatch.PORT_OPTION, "  ",
                                      "-" + RsBatch.COMMANDFILE_OPTION, testCommandFile,
                                      "-" + RsBatch.USERNAME_OPTION, username,
                                      "-" + RsBatch.PASSWORD_OPTION, password};
        RsBatch console = new RsBatch();
        try {
            console.parseArgs(args);
            fail("Should throw exception because username cannot be empty");
        }
        catch(RCliException e){
            assertEquals(new RCliException(RCliException.EMPTY_ARGUMENT_FOR_COMMAND_LINE_OPTION, new Object[] { RsBatch.PORT_OPTION }).getMessage(), e.getMessage());
        }
    }
    public void testInvalidPortPrintsUsage() throws Exception {

        port = "invalid int";
        String[] args = new String[] {"-" + RsBatch.HOST_OPTION, hostName,
                                      "-" + RsBatch.PORT_OPTION, port,
                                      "-" + RsBatch.COMMANDFILE_OPTION, testCommandFile,
                                      "-" + RsBatch.USERNAME_OPTION, username,
                                      "-" + RsBatch.PASSWORD_OPTION, password};
        RsBatch console = new RsBatch();
        try {
            console.parseArgs(args);
            fail("Should throw exception because username cannot be empty");
        }
        catch(RCliException e){
            assertEquals(new RCliException(RCliException.INVALID_COMMAND_LINE_OPTION_VALUE, new Object[] { port, RsBatch.PORT_OPTION }).getMessage(), e.getMessage());
        }
    }



	public void testEmptyPasswordPrintsUsage() throws Exception {
        String[] args = new String[] {"-" + RsBatch.HOST_OPTION, hostName,
                                      "-" + RsBatch.PORT_OPTION, port,
                                      "-" + RsBatch.COMMANDFILE_OPTION, testCommandFile,
                                      "-" + RsBatch.USERNAME_OPTION, username,
                                      "-" + RsBatch.PASSWORD_OPTION, " "};
		RsBatch console = new RsBatch();
		try {
            console.parseArgs(args);
            fail();
		}
		catch(RCliException e){
			assertEquals(new RCliException(RCliException.EMPTY_ARGUMENT_FOR_COMMAND_LINE_OPTION, new Object[] { RsBatch.PASSWORD_OPTION }).getMessage(), e.getMessage());
		}
	}

	public void testEmptyCommandFilePrintsUsage() throws Exception {
         String[] args = new String[] {"-" + RsBatch.HOST_OPTION, hostName,
                                      "-" + RsBatch.PORT_OPTION, port,
                                      "-" + RsBatch.COMMANDFILE_OPTION, " ",
                                      "-" + RsBatch.USERNAME_OPTION, username,
                                      "-" + RsBatch.PASSWORD_OPTION, password};
		RsBatch console = new RsBatch();
		try {
            console.parseArgs(args);
            fail();
		}
		catch(RCliException e){
			assertEquals(new RCliException(RCliException.EMPTY_ARGUMENT_FOR_COMMAND_LINE_OPTION, new Object[] { RsBatch.COMMANDFILE_OPTION }).getMessage(), e.getMessage());
		}
	}

	public void testInvalidCommandFilePrintsUsage() throws Exception {
        String[] args = new String[] {"-" + RsBatch.HOST_OPTION, hostName,
                                      "-" + RsBatch.PORT_OPTION, port,
                                      "-" + RsBatch.COMMANDFILE_OPTION, "invalid",
                                      "-" + RsBatch.USERNAME_OPTION, username,
                                      "-" + RsBatch.PASSWORD_OPTION, password};
		RsBatch console = new RsBatch();
		try {
            console.parseArgs(args);
            fail();
		}
		catch(RCMDBException e){
			assertEquals(new RCMDBException(RCMDBException.FILE_NOT_FOUND, new Object[] { new TestFile("invalid").getAbsolutePath() }).getMessage(), e.getMessage());
		}
	}


	public void testIfCommandFileCanNotBeFoundExecuteThrowsException() throws Exception {

		FileTestUtils.deleteFile(file);

		RsBatch tool = new RsBatch()
        {
            protected void authenticate(String login, String pass) throws RCMDBException
            {
            }
        };
		tool.setCommandFile(testCommandFile);
		try {
			tool.execute();
			fail("Should throw exception because the RSUsers.txt file does not exist.");
		}
		catch (RCMDBException e) {
			assertEquals(new RCMDBException(RCMDBException.FILE_NOT_FOUND, new Object[] { file.getAbsolutePath() }).getMessage(), e.getMessage());
		}
	}

	public void testProcessAction() throws Exception {
		String callPrefix = "http://"+hostName+":111/";
		final ArrayList methodCalls = new ArrayList();
		RsBatch tool = new RsBatch() {
			protected String makeCall(String call) throws RCMDBException {
				methodCalls.add(call);
				return "";
			}
            protected void authenticate(String login, String pass) throws RCMDBException
            {
            }
		};
		tool.setPort(111);
		tool.setHost(hostName);

		String actionParameters = "Url/prefix?ParameterName=ParameterValue";
		tool.processAction(actionParameters);

		assertEquals(1, methodCalls.size());
		assertEquals(callPrefix + actionParameters, methodCalls.get(0));

	}


	public void testProcessingLinesWithEmptySpaces() throws Exception {
		int port = 2121;
		String callPrefix = "http://"+hostName+":"+port +"/";
		final ArrayList methodCalls = new ArrayList();
		ArrayList lines = new ArrayList();
		String line = "  " + "Model/create" + "?Name=asdf&ParentModel=assdf        ";
		lines.add(line);
		FileTestUtils.generateFile(file.getAbsolutePath(), lines);

		RsBatch tool = new RsBatch() {
			protected String makeCall(String call) throws RCMDBException {
				methodCalls.add(call);
				return "";
			}
            protected void authenticate(String login, String pass) throws RCMDBException
            {
            }
		};
		tool.setPort(port);
        tool.setHost(hostName);
		tool.setCommandFile(testCommandFile);
		tool.execute();

		assertEquals(1, methodCalls.size());
		assertEquals(callPrefix + line.trim(), methodCalls.get(0));
	}

//	public void testUpdateToolsPassword() throws Exception {
//
//		RsBatch tool = new RsBatch();
//		tool.setUsername(AuthenticationConstants.ADMIN);
//		tool.setPassword(AuthenticationConstants.DEFAULT_PASSWORD);
//
//		String newPass = "newPass";
//		String actionParameters = "User/update" + "?" + AuthenticationConstants.PARAMETER_USERNAME + "=" + AuthenticationConstants.ADMIN + "&" + AuthenticationConstants.PARAMETER_NEWPASSWORD1 + "=" + newPass  + "&" + AuthenticationConstants.PARAMETER_NEWPASSWORD2 + "=" + newPass;
//		tool.updateToolPassword(actionParameters);
//		assertEquals(newPass, tool.getPassword());
//
//		String newPass2 = "newPass";
//		actionParameters = "User/update" + "?" + AuthenticationConstants.PARAMETER_USERNAME + "=" + AuthenticationConstants.ADMIN + "&" + AuthenticationConstants.PARAMETER_NEWPASSWORD1 + "=" + newPass2  + "&" + AuthenticationConstants.PARAMETER_NEWPASSWORD2 + "=" + newPass;
//		tool.updateToolPassword(actionParameters);
//		assertEquals(newPass, tool.getPassword());
//
//		String newPass3 = "newPass";
//		actionParameters = "User/update" + "?" + "&" + AuthenticationConstants.PARAMETER_NEWPASSWORD1 + "=" + newPass3  + "&" + AuthenticationConstants.PARAMETER_NEWPASSWORD2 + "=" + newPass3 + AuthenticationConstants.PARAMETER_USERNAME + "=" + AuthenticationConstants.ADMIN ;
//		tool.updateToolPassword(actionParameters);
//		assertEquals(newPass3, tool.getPassword());
//
//		actionParameters = "User/update" + "?" + AuthenticationConstants.PARAMETER_USERNAME + "=" + "otherUser" + "&" + AuthenticationConstants.PARAMETER_NEWPASSWORD1 + "=" + newPass  + "&" + AuthenticationConstants.PARAMETER_NEWPASSWORD2 + "=" + newPass;
//		tool.updateToolPassword(actionParameters);
//		assertEquals(newPass3, tool.getPassword());
//	}

	//file with empty lines
	public void testEmptyLinesAreIgnored() throws Exception {
		int port = 2121;
		String callPrefix = "http://"+hostName+":"+port +"/";
		final ArrayList methodCalls = new ArrayList();

		ArrayList lines = new ArrayList();
		String line = "Model/create" + "?Name=asdf&ParentModel=assdf";
		lines.add("");
		lines.add(line);
		lines.add("");
		FileTestUtils.generateFile(file.getAbsolutePath(), lines);

		RsBatch tool = new RsBatch() {
			protected String makeCall(String call) throws RCMDBException {
				methodCalls.add(call);
				return "";
			}

            protected void authenticate(String login, String pass) throws RCMDBException
            {
            }
		};
		tool.setPort(port);
		tool.setHost(hostName);
		tool.setCommandFile(testCommandFile);
		tool.execute();

		assertEquals(1, methodCalls.size());
		assertEquals(callPrefix + line, methodCalls.get(0));
	}
}
