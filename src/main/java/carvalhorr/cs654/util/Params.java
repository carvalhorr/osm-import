package carvalhorr.cs654.util;

import java.util.HashMap;
import java.util.Map;

public class Params {
	
	public static final String PARAM_DB_CONFIG_FILENAME = "db_config";

	private Map<String, Object> params = new HashMap<String, Object>();
	
	private static Params instance;
	
	private Params() {}
	
	public static Params getInstance() {
		if (instance == null) {
			instance = new Params();
		}
		return instance;
	}
	
	public void setParam(String paramName, Object paramValue) {
		params.put(paramName, paramValue);
	}
	
	public Object getParam(String paramName) {
		return params.get(paramName);
	}
}
