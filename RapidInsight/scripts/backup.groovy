import java.text.SimpleDateFormat


def backupDirectory="backup";



SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
String directory = "${backupDirectory}/${df.format(new Date())}".toString();

logger.warn("-----------------------------------------------------------------------------");
logger.warn("********** Starting backup to directory '${directory}' **********");
application.RapidApplication.backup (directory);
logger.warn("********** Backup successfully done to directory '${directory}' **********");