package com.sankarg.webapps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class AppConfig {
    private final static Logger logger = LoggerFactory.getLogger(AppConfig.class);

    // Environment variables
    public final static String ENV_VAR_PORT = "PORT";
    public  final static String ENV_DS_URI = "TODOLIST_API_DS_URI";
    public  final static String ENV_DS_USER = "TODOLIST_API_DS_USERNAME";
    public final static String ENV_DS_PWD = "TODOLIST_API_DS_USERPWD";

    public final static String APP_SERVICE_PORT = "APP_PORT";
    public final static String APP_DS_URI = "DS_URI";
    public final static String APP_DS_USER = "DS_USERNAME";
    public final static String APP_DS_PWD = "DS_USERPWD";
    public final static String APP_DS_URL = "DS_URL";

    public final static String APP_DS_DB = "webapps";
    public final static String APP_DS_DB_COLLECTION = "todolist";
    private final static String APP_SERVICE_PORT_NUMBER_VAL = "9090";

    private static Map<String, String> configValuesMap = new HashMap<>();

    public static void init() {
        StringBuffer exceptionMessage = new StringBuffer();
        ProcessBuilder pb = new ProcessBuilder();

        String port = APP_SERVICE_PORT_NUMBER_VAL;
        if (pb.environment().get(ENV_VAR_PORT) != null) {
            port  = pb.environment().get(ENV_VAR_PORT);
        } else {
            exceptionMessage.append("port is not set as environment variable");
        }
        configValuesMap.put(APP_SERVICE_PORT, port);

        if (pb.environment().get(ENV_DS_URI) != null) {
            configValuesMap.put(APP_DS_URI, pb.environment().get(ENV_DS_URI));
        } else {
            exceptionMessage.append("\nData source URI is not set as environment variable");
        }

        if (pb.environment().get(ENV_DS_USER) != null) {
            configValuesMap.put(APP_DS_USER, pb.environment().get(ENV_DS_USER));
        } else {
            exceptionMessage.append("\nData source username is not set as environment variable");
        }

        if (pb.environment().get(ENV_DS_PWD) != null) {
            configValuesMap.put(APP_DS_PWD, pb.environment().get(ENV_DS_PWD));
        } else {
            exceptionMessage.append("\nData source user password is not set as environment variable");
        }
        configValuesMap.put(APP_DS_URL, "mongodb://" + configValuesMap.get(APP_DS_USER) + ":" +
            configValuesMap.get(APP_DS_PWD) + "@" + configValuesMap.get(APP_DS_URI));
        if (exceptionMessage.length() > 0) {
            throw new RuntimeException(exceptionMessage.toString());
        }
    }

    public static String get(String paramName) {
        return configValuesMap.get(paramName);
    }
}