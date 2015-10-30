package akj.apiautomation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonComparer {
	static Logger log = Logger.getLogger(JsonComparer.class);

	public static void main(String[] args) throws IOException {
		System.out.println("Self test !!!");
		ArrayList<ArrayList<String>> rows = ExcelParser.getInstance()
				.readAllRow("resources/selfTest.xlsx");
		for (ArrayList<String> row : rows) {
			System.out.println(row.get(0) + " : "
					+ compareJson(row.get(1), row.get(2)));
		}
	}

	public static boolean compareJson(String actual, String expected) {
		JsonElement e1 = new JsonParser().parse(actual);
		JsonElement e2 = new JsonParser().parse(expected);
		return compareJson(e1, e2);
	}

	public static boolean compareJson(JsonElement e1, JsonElement e2) {
		if (e2 == null || e2.isJsonNull()) {
			return true;
		} else if (e1 == null && e2 != null && !e2.isJsonNull()) {
			print(e1);
			print(e2);
			System.out.println(false);
			return false;
		} else if (e1.isJsonNull() && !e2.isJsonNull()) {
			print(e1);
			print(e2);
			System.out.println(false);
			return false;
		}

		if (e1.isJsonObject() && e2.isJsonObject()) {
			JsonObject o1 = e1.getAsJsonObject();
			JsonObject o2 = e2.getAsJsonObject();
			// TODO
			List<String> p2 = new ArrayList<String>();
			for (Entry<String, JsonElement> e : o2.entrySet()) {
				p2.add(e.getKey());
			}

			boolean flag = true;
			for (String key : p2) {
				JsonElement innere1 = o1.get(key);
				JsonElement innere2 = o2.get(key);
				if (!compareJson(innere1, innere2)) {
					flag = false;
				}
			}
			if (!flag) {
				print(e1);
				print(e2);
				System.out.println(flag);
			}
			return flag;
		} else if (e1.isJsonArray() && e2.isJsonArray()) {
			JsonArray a1 = e1.getAsJsonArray();
			JsonArray a2 = e2.getAsJsonArray();

			if (a2.size() == 0) {
				return true;
			} else if (a1.size() == a2.size() || a2.size() == 1) {
				// TODO : Compare array objects.
				boolean flag = true;
				for (int i = 0; i < a1.size(); i++) {
					boolean tempFlag = compareJson(a1.get(i),
							(a2.size() == 1) ? a2.get(0) : a2.get(i));
					// verify the fields if expected array's size is 1 else
					// verify objects in the array.
					if (!tempFlag) {
						print(e1);
						print(e2);
						System.out.println(tempFlag);
					}
					if (flag) {
						flag = tempFlag;
					}
				}
				return flag;
			} else {
				print(e1);
				print(e2);
				System.out.println(false);
				return false;
			}
		} else if (e1.isJsonPrimitive() && e2.isJsonPrimitive()) {
			if (e1.getAsString().equals(e2.getAsString())) {
				return true;
			} else {
				print(e1);
				print(e2);
				System.out.println(false);
				return false;
			}
		} else {
			print(e1);
			print(e2);
			System.out.println(false);
			return false;
		}
	}

	private static void print(JsonElement je) {
		if (je == null || je.isJsonNull()) {
			System.out.println(je);
		} else {
			String j = je.toString();
			System.out.println(j.substring(0, (j.length() < 100) ? j.length()
					: 100)
					+ ((j.length() < 100) ? "" : "..."));
			// trimming the string to 50 chars and adding ... in the end if
			// length is more than 50.
		}
	}
}
