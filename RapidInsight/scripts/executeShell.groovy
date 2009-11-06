/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 6, 2009
 * Time: 10:27:13 AM
 */

// define the shell command or script to execute
command = "echo \"Hello World\""

//Execute the script, Capture and return the response.
response = new StringBuffer("");
proc = command.execute();
proc.in.eachLine(){
        response.append(it.toString()).append("\n");
}
return "<br>"+response.toString().replaceAll("\n", "<br>");