import remoteModification.RemoteApplicationModification
def comment = params.comment;
if(comment == null)
{
    comment = "";
}
RemoteApplicationModification.ignoreAllChanges(comment);