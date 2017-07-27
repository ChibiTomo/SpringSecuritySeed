package net.chibidevteam.securityseed.util;

public class Utils {

    public static String   versionPathPrefix;
    public static String   pathVarname;
    public static String   versionRegex;
    public static String   noCaptureVersionRegex;
    public static String   basePath;
    public static String   apiPath;

    public static String[] supportedVersions;

    public static void resetDefaults() {
        versionPathPrefix = "v";
        pathVarname = "apiVersion";
        versionRegex = "(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(.*)";
        noCaptureVersionRegex = "(?:\\d+)(?:\\.(?:\\d+))?(?:\\.(?:\\d+))?(?:.*)";
        basePath = "/api";
        apiPath = basePath + "/{" + pathVarname + "}";
        supportedVersions = new String[] { "0", "1.7", "1.8", "2.5", "3", "4.0" };
    }

    public static void applyConfig() {
    }
}
