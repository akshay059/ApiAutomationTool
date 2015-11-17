package akj.apiautomation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

// this test class reads the excel sheet given in the config and performs the tests from sheet one.

public class GeneralTest {
	Logger log = Logger.getLogger(this.getClass());
	TestConfiguration config = TestConfiguration.getInstance();
	Client client;

	// These are used as input for test cases that follow the executed test
	// cases.

	private HashMap<Integer, String> responseBodies;
	// Map of test case Ids and the corresponding response bodies.
	private HashMap<Integer, MultivaluedMap<String, String>> responseHeaders;

	// Map of test case ids and response headers.

	@BeforeClass
	public void before() {
		log.info("--------------------------------------------------");
		log.info("Staring the automation. Setting up the test class.");
		log.info("--------------------------------------------------");
		client = Client.create();
		responseBodies = new HashMap<Integer, String>();
		responseHeaders = new HashMap<Integer, MultivaluedMap<String, String>>();
	}

	@AfterClass
	public void after() {
		log.info("--------------------------------------------------");
		log.info("   Ending the automation. Nothing to tear down.   ");
		log.info("--------------------------------------------------");
	}

	@Test(dataProvider = "requestData")
	public void test(ArrayList<String> row) {
		Integer tcId = (int) Float.parseFloat(row.get(0));
		String url = row.get(1);
		String verb = row.get(2);
		String headers = row.get(3);
		String params = row.get(4);
		String body = row.get(5);
		String resStatusExp = row.get(6);
		String resBodyExp = row.get(7);
		String testDesc = row.get(8);
		String preStepRef = "";
		String preStepRefHeaders = "";
		Boolean execute = true;

		Boolean testStatus = false;
		// for checking in finally if the test was pass. will save response body
		// and headers iff test passes.

		if (row.size() > 9) {
			preStepRef = row.get(9);
		}

		if (row.size() > 10) {
			preStepRefHeaders = row.get(10);
		}

		if (row.size() > 11) {
			execute = !row.get(11).equalsIgnoreCase("FALSE");
			// only set to false when cell value is false. In any other case
			// test will execute.
		}

		String resBody = null;
		MultivaluedMap<String, String> resHeaders = null;

		try {
			System.out
					.println("---------------------started--------------------");
			log.info("---------------------started--------------------");

			if (!execute) {
				System.out.println("Skipped : Test not marked for execution.");
				throw new SkipException("Test not marked for execution.");
			}

			System.out.println(tcId + "----" + testDesc);
			log.info(tcId + "----" + testDesc);
			log.info("URL: " + url);
			log.info("Verb : " + verb);

			HashMap<String, String> templateVars = new HashMap<String, String>();

			templateVars = extractVarPreStepBody(preStepRef, tcId, templateVars);

			templateVars = extractVarPreStepHeader(preStepRefHeaders, tcId,
					templateVars);

			ClientResponse res = null;
			WebResource webResource = client.resource(url);

			log.info("Params : ");
			String[] pArr = params.split("&");
			for (String p : pArr) {
				if (!p.equals("")) {
					String[] kvP = p.split("=");
					String value = null;
					try {
						value = StrSubstitutor.replace(kvP[1], templateVars);
					} catch (ArrayIndexOutOfBoundsException aiobe) {
						System.out
								.println("Skipped : wrong param details, format is wrong.");
						throw new SkipException(
								"wrong param details, format is wrong.");
					}

					log.info(kvP[0] + " : " + value);

					webResource = webResource.queryParam(kvP[0], value);
				}
			}
			Builder req = webResource.header("Content-Type",
					MediaType.APPLICATION_JSON);

			log.info("Request Headers : ");

			String[] hArr = headers.split(",");
			for (String p : hArr) {
				if (!p.equals("")) {
					String[] kvP = p.split("=");
					String value = null;
					try {
						value = StrSubstitutor.replace(kvP[1], templateVars);
					} catch (ArrayIndexOutOfBoundsException aiobe) {
						System.out
								.println("Skipped : wrong header details, format is wrong.");
						throw new SkipException(
								"wrong header details, format is wrong.");
					}

					log.info(kvP[0] + " : " + value);

					req = req.header(kvP[0], value);
				}
			}

			body = StrSubstitutor.replace(body, templateVars);

			log.info("Body: " + body);

			if (verb.equalsIgnoreCase("GET")) {
				res = req.get(ClientResponse.class);
			} else if (verb.equalsIgnoreCase("POST")) {
				res = req.post(ClientResponse.class, body);
			} else if (verb.equalsIgnoreCase("PUT")) {
				res = req.put(ClientResponse.class, body);
			} else if (verb.equalsIgnoreCase("DELETE")) {
				res = req.delete(ClientResponse.class);
			}

			log.info("Status : " + res.getStatus());
			log.info("St Exp : " + resStatusExp);
			resHeaders = res.getHeaders();
			log.info("Headers : " + resHeaders);
			resBody = res.getEntity(String.class);
			log.info("Output : " + resBody);
			log.info("Op Exp : " + resBodyExp);

			resBodyExp = StrSubstitutor.replace(resBodyExp, templateVars);

			Assert.assertEquals(res.getStatus(),
					(int) Float.parseFloat(resStatusExp), testDesc);
			Assert.assertTrue(JsonComparer.compareJson(resBody, resBodyExp),
					testDesc);
			testStatus = true;
		} finally {
			if (testStatus) {
				responseBodies.put(tcId, resBody);
				responseHeaders.put(tcId, resHeaders);
				System.out
				.println("---------------------ended-passed--------------");
				log.info("---------------------ended-passed--------------");
			} else{
				System.out
				.println("<<<<<<<<<<<<<<<<<<<<<ended-failed>>>>>>>>>>>>>>>");
				log.info("<<<<<<<<<<<<<<<<<<<<<ended-failed>>>>>>>>>>>>>>>");
			}
		}
	}

	@DataProvider
	public Object[][] requestData() {
		ExcelParser p = ExcelParser.getInstance();
		ArrayList<ArrayList<String>> rows = p.readAllRow(config.getSheetPath());
		Object[][] data = new Object[rows.size() - 1][];
		for (int i = 1; i < rows.size(); i++) {
			data[i - 1] = new Object[] { rows.get(i) };
		}
		return data;
	}

	public HashMap<String, String> extractVarPreStepBody(String preStepRef,
			int tcId, HashMap<String, String> templateVars) {
		String[] vars = preStepRef.split(",");
		for (String p : vars) {
			if (!p.equals("")) {
				String[] varMap = p.split("=");
				String varName = varMap[0].trim();
				String varPath = varMap[1].trim();
				if (varPath.contains("EPOCH")) {
					Long time = new Long(System.currentTimeMillis());
					Integer days = 0;
					if (varPath.contains("+")) {
						days = Integer.parseInt(varPath.split("\\+")[1]);
						// change to get 12:00 am epoch for same date.
						Calendar c = Calendar.getInstance();
						c.setTimeZone(TimeZone.getTimeZone("IST"));
						c.add(Calendar.DAY_OF_MONTH, days);
						if (days == 0) {
							c.set(Calendar.HOUR, 0);
						} else if (days > 0) {
							c.set(Calendar.HOUR, -12);
						}
						c.set(Calendar.MINUTE, 0);
						c.set(Calendar.SECOND, 0);
						time = c.getTimeInMillis() / 1000;
						time *= 1000;
					}
					templateVars.put(varName, time.toString());
				} else if (varPath.indexOf(':') == -1) {
					templateVars.put(varName, varPath);
				} else {
					String[] pathParts = varPath.split(":");
					Integer dependencyId = Integer
							.parseInt(pathParts[0].trim());
					String jsonPath = pathParts[1].trim();
					if (responseBodies.get(dependencyId) == null) {
						System.out
								.println("Skipped : The test ("
										+ dependencyId
										+ ") "
										+ "upon which current test depends might have not passed, or may not have executed yet."
										+ "Please check.");
						throw new SkipException(
								"The test ("
										+ dependencyId
										+ ") "
										+ "upon which current test depends might have not passed, or may not have executed yet."
										+ "Please check.");
					} else {
						try {
							Object value = JsonPath.read(
									responseBodies.get(dependencyId), jsonPath);
							if (value == null) {
								System.out
										.println("Skipped : The value at Jsonpath mentioned in the PreStepRef is null, please check.");
								throw new SkipException(
										"The value at Jsonpath mentioned in the PreStepRef is null, please check.");
							}
							templateVars.put(varName, value.toString().trim());
						} catch (PathNotFoundException pnfe) {
							System.out
									.println("Skipped : The json path is invalid.");
							throw new SkipException("The json path is invalid.");
						}
					}
				}
			}
		}

		return templateVars;
	}

	private HashMap<String, String> extractVarPreStepHeader(
			String preStepRefHeaders, Integer tcId,
			HashMap<String, String> templateVars) {
		String[] vars = preStepRefHeaders.split(",");
		for (String p : vars) {
			if (!p.equals("")) {
				String[] varMap = p.split("=");
				String varName = varMap[0].trim();
				String varHeader = varMap[1].trim();

				String[] pathParts = varHeader.split(":");
				Integer dependencyId = Integer.parseInt(pathParts[0].trim());
				String headerName = pathParts[1].trim();

				if (responseHeaders.get(dependencyId) == null) {
					System.out
							.println("Skipped : The test ("
									+ dependencyId
									+ ") "
									+ "upon which current test depends might have not passed. "
									+ "Please check.");
					throw new SkipException(
							"The test ("
									+ dependencyId
									+ ") "
									+ "upon which current test depends might have not passed. "
									+ "Please check.");
				} else {
					try {
						Object value = responseHeaders.get(dependencyId).get(
								headerName);
						if (value == null) {
							System.out
									.println("Skipped : The header mentioned in the PreStepRefHeader is null, please check.");
							throw new SkipException(
									"The header mentioned in the PreStepRefHeader is null, please check.");
						}
						templateVars.put(varName, value.toString().toString());
					} catch (PathNotFoundException pnfe) {
						System.out
								.println("Skipped : The header name is invalid.");
						throw new SkipException("The header name is invalid.");
					}
				}

			}
		}

		return templateVars;
	}
}