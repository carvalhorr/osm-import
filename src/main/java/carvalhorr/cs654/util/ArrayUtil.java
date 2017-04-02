package carvalhorr.cs654.util;

import java.util.List;

public class ArrayUtil {

	public static String[] convertStringListToStringArray(List<String> list) {
		String[] argsStr = new String[list.size()];
		argsStr = list.toArray(argsStr);
		return argsStr;
	}
}
