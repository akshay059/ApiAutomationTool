package akj.apiautomation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

public class SSHUtils {
	static Logger log = Logger.getLogger(SSHUtils.class);
	
	public static void main(String argsp[]){
		try {
			System.out.println(execute("ssh user@host 'ab -n 100 -c 10 http://yahoo.com'"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String execute(String command) throws IOException{
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader bis = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String all = "", line = null;
			while ((line = bis.readLine()) != null){
				all += line + "\n";
			}
			return all;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("error executing command : " + command);
			throw e;
		}
	}
	
	public static String execute(String user, String host, String command) throws IOException{
		StringBuffer com = new StringBuffer("ssh ");
		com.append(user + "@" + host + " " + "'" + command + "'");
		try {
			Process p = Runtime.getRuntime().exec(com.toString());
			BufferedReader bis = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String all = "", line = null;
			while ((line = bis.readLine()) != null){
				System.out.println(line);
				all += line + "\n";
			}
			return all;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("error executing command : " + command + " on host : " + host);
			throw e;
		}
	}
	
	public static String execute(String user, String host, String port, String command) throws IOException{
		StringBuffer com = new StringBuffer("ssh ");
		com.append(user + "@" + host + " " + "-p" + port + " " + "'" + command + "'");
		try {
			Process p = Runtime.getRuntime().exec(com.toString());
			BufferedReader bis = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String all = "", line = null;
			while ((line = bis.readLine()) != null){
				all += line + "\n";
			}
			return all;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("error executing command : " + command + " on host : " + host);
			throw e;
		}
	}
}
