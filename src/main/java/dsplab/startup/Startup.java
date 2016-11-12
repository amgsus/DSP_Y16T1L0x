package dsplab.startup;

import dsplab.DSPLabApplication;

public final class Startup
{
    Startup() {}

    static final String LOG4J_PROP_CFG_FILE
        = "log4j.configurationFile";
    static final String LOG4J_CONFIGURATION_FILE
        = "/LogConfig.xml";

    public static void main(String[] args)
    {
        System.setProperty(LOG4J_PROP_CFG_FILE, LOG4J_CONFIGURATION_FILE);
        DSPLabApplication.launch(DSPLabApplication.class, args);
    }
}
