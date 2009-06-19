

 import org.apache.commons.lang.StringUtils

  def logFile = new File(params.file);
  def log = logFile.getText();
  return StringUtils.countMatches(log,"scriptName:aScript loglevel: DEBUG useOwnLogger: false staticParameter:" )


