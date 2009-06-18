
 import org.apache.commons.lang.StringUtils

  def logFile = new File(params.file);
  def log = logFile.getText();
  return StringUtils.countMatches (log, "Hello from periodic")