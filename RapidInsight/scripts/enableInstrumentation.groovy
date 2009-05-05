if(params.enable=="false")
{
    Statistics.disableGlobally();
    return "Instrumentation disabled"
}
else
{
    Statistics.enableGlobally();
    return "Instrumentation enabled"
}