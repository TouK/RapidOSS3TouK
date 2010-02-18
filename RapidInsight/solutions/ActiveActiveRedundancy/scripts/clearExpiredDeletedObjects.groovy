def hoursBefore=12;
def before=Date.now()-(hoursBefore*360000);

logger.warn("Removing expired DeletedObjects information before : ${new Date(before)}");
DeletedObjects.removeAll("rsUpdatedAt:[0 TO ${before}]");
logger.warn("Removed expired DeletedObjects information before : ${new Date(before)}");

return "Removed expired DeletedObjects information before : ${new Date(before)}"