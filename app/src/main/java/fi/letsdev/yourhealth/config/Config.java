package fi.letsdev.yourhealth.config;

public final class Config {

	final public static String CLUSTERURL = "http://ortc-developers.realtime.co/server/2.1/";
	final public static String URL = null;
	final public static String METADATA = "androidApp";
	final public static String TOKEN = "token";
	final public static String APPKEY = "yaaMkI";
	final public static String PROJECT_ID = "638145090557";

	final public static int CHANNEL_NAME = 1;
	final public static int CHANNEL_ADD = 2;
	final public static int CHANNEL_DEL = 3;

	final public static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

	final public static String ortcFactory = "IbtRealtimeSJ";

	private Config() throws InstantiationException {
		throw new InstantiationException("This class is not created for instantiation");
	}
}
