package runner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.time.LocalDateTime;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;

import stepdefinitions.CukeHooks;

public class TestPlan {

	private TestPlan() {
	}

	protected static final Logger LOG = LoggerFactory.getLogger(TestPlan.class);
	protected static ArrayList<String> TestPoints = new ArrayList<String>();
	protected static ArrayList<String> TestresultStatus = new ArrayList<String>();
	protected static Map<String, String> Testresults = new HashMap<String, String>();

	public static void updateTestResults(String planName, Map<String, String> details) {
		LocalDateTime timenow = LocalDateTime.now();
		String testplanID = getTestPlanID(planName);
		for (Entry<String, String> detail : details.entrySet()) {
			String suiteID = getSuiteID(testplanID, detail.getKey().split(":")[0]);
			String testcaseID = getTestCaseID(testplanID, suiteID, detail.getKey().split(":")[1]);
			String testpointID = getTestPointID(testplanID, suiteID, testcaseID);
			TestPoints.add(testpointID);
			TestresultStatus.add(detail.getKey().split(":")[1] + ":" + detail.getValue());
		}
		String runId = createTestRun(planName + " " + timenow, testplanID, TestPoints);
		String resultIDbody = getTestResultIDBody(runId);
		String resultid = "";
		for (int i = 0; i < TestPoints.size(); i++) {
			resultid = JsonPath.read(resultIDbody, "$.value.[?(@.testPoint.id == '" + TestPoints.get(i) + "')].id")
					.toString();
			resultid = resultid.replace("]", "").replace("[", "").trim();
			Testresults.put(resultid, TestresultStatus.get(i));
		}

		updateTestStatus(runId, Testresults);
		TestPoints.clear();
		TestresultStatus.clear();
		Testresults.clear();
	}

	public static String APIRequest(String URL, String method, String payload, String contenttype) throws IOException {
		String basicAuthorization =  ":" + System.getProperty("pat"); 
		String responsebody = "";
		URL url = new URL(URL);
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization",
					"Basic " + new String(Base64.encodeBase64(basicAuthorization.getBytes())));
			switch (method) {
			case "POST": {
				connection.setDoOutput(true);
				connection.setRequestMethod(method);
				connection.setRequestProperty("Content-Type", contenttype);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(payload);
				out.flush();
				out.close();
				break;
			}
			case "PATCH": {
				allowMethods("PATCH");
				connection.setDoOutput(true);
				connection.setRequestMethod(method);
				connection.setRequestProperty("Content-Type", contenttype);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(payload);
				out.flush();
				out.close();
				break;
			}
			case "GET": {
				connection.setRequestMethod(method);
				break;
			}
			}
			InputStreamReader reader = new InputStreamReader(connection.getInputStream());
			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[2048];
			int num;
			while (-1 != (num = reader.read(cbuf))) {
				buf.append(cbuf, 0, num);
			}
			responsebody = buf.toString();

		} catch (IOException ex) {
			LOG.error(ex.getMessage());

		} finally {
			connection.disconnect();
		}
		return responsebody;
	}

	public static String payload(String title) throws IOException {

		String payload = "[\r\n" + "  {\r\n" + "    \"op\": \"add\",\r\n"
				+ "    \"path\": \"/fields/System.Title\",\r\n" + "    \"from\": null,\r\n" + "    \"value\": \""
				+ title + "\"\r\n" + "  }]";

		return payload;
	}

	public static String getTestPlanID(String planName) {
		String planID = null;
		try {
			String URL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
					+ System.getProperty("project") + "/_apis/test/plans?api-version=5.0";
			String response = APIRequest(URL, "GET", "", "");
			String jsonpath = "$.value.[?(@.name == '" + planName + "')].id";
			planID = JsonPath.read(response, jsonpath).toString();
			planID = planID.replace("]", "").replace("[", "").replace("\"", "");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return planID;
	}

	public static String getSuiteID(String planID, String suitename) {
		String suiteID = null;
		try {
			String URL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
					+ System.getProperty("project") + "/_apis/test/plans/" + planID + "/suites?api-version=5.0";
			String response = APIRequest(URL, "GET", "", "");
			String jsonpath = "$.value.[?(@.name == '" + suitename + "')].id";
			suiteID = JsonPath.read(response, jsonpath).toString();
			suiteID = suiteID.replace("]", "").replace("[", "").replace("\"", "");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return suiteID;
	}

	public static String getTestCaseID(String planID, String suiteID, String testcasename) {
		String testcaseID = null;
		try {
			String URL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
					+ System.getProperty("project") + "/_apis/test/plans/" + planID + "/suites/" + suiteID
					+ "/points?api-version=5.0";
			String response = APIRequest(URL, "GET", "", "");
			String jsonpath = "$..[?(@.name == '" + testcasename + "')].id";
			testcaseID = JsonPath.read(response, jsonpath).toString();
			testcaseID = testcaseID.replace("]", "").replace("[", "").replace("\"", "");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return testcaseID;
	}

	public static String getTestResultIDBody(String runID) {
		String response = null;
		try {
			String URL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
					+ System.getProperty("project") + "/_apis/test/runs/" + runID
					+ "/results?api-version=6.0-preview.6";
			response = APIRequest(URL, "GET", "", "");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return response;
	}

	public static String getTestPointID(String planID, String suiteID, String tcID) {
		String pointID = null;
		try {
			String URL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
					+ System.getProperty("project") + "/_apis/test/plans/" + planID + "/suites/" + suiteID
					+ "/points?testCaseId=" + tcID + "&api-version=5.0";
			String response = APIRequest(URL, "GET", "", "");
			String jsonpath = "$.value.[*].id";
			pointID = JsonPath.read(response, jsonpath).toString();
			pointID = pointID.replace("]", "").replace("[", "").replace("\"", "");
			System.out.println(pointID);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return pointID;
	}

	public static String createTestRun(String RunName, String planID, ArrayList<String> Points) {
		String runID = null;
		try {
			String URL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
					+ System.getProperty("project") + "/_apis/test/runs?api-version=5.0";
			String testpoints = "";
			for (String Point : Points) {
				testpoints += Point + ",\n";
			}
			testpoints = testpoints.substring(0, testpoints.lastIndexOf(",\n"));
			String payload = "{\r\n" + "  \"name\": \"" + RunName + "\",\r\n" + "  \"plan\": {\r\n" + "    \"id\": \""
					+ planID + "\"\r\n" + "  },\r\n" + "  \"pointIds\": [\r\n" + "   " + testpoints + "\r\n" + "  ]\r\n"
					+ "}";
			String response = APIRequest(URL, "POST", payload, "application/json");
			String jsonpath = "$.id";
			runID = JsonPath.read(response, jsonpath).toString();
			runID = runID.replace("]", "").replace("[", "").replace("\"", "");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return runID;
	}

	public static void updateTestStatus(String runID, Map<String, String> Results) {
		try {
			String URL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
					+ System.getProperty("project") + "/_apis/test/runs/" + runID
					+ "/results?api-version=6.0-preview.6";
			String testpoints = "";
			for (Entry<String, String> result : Results.entrySet()) {
				testpoints += testpointspayload(result.getKey(), result.getValue()) + ",";
			}
			String payload = "[" + testpoints.substring(0, testpoints.lastIndexOf(",")) + "]";
			System.out.println(payload);
			APIRequest(URL, "PATCH", payload, "application/json");
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	private static String testpointspayload(String id, String result) throws IOException {
		String status = result.split(":")[1];
		String testcasename = result.split(":")[0];
		String payload = "";
		System.out.println("================="+testcasename+"=================");
		if (status.equalsIgnoreCase("PASSED")) {
			System.out.println(testcasename + " - Passed");
			closeBug(testcasename);
			payload = "{\r\n" + "    \"id\": " + id + ",\r\n" + "    \"outcome\": \"" + status + "\",\r\n"
					+ "    \"state\": \"Completed\",\r\n" + "    \"comment\": \"Execution passed\",\r\n" + "  }";
		} else {
			String bugid = createBug(testcasename + "- Failed");
			linkBugwithTestCase(testcasename,bugid);
			payload = "{\r\n" + "    \"id\": " + id + ",\r\n" + "    \"outcome\": \"" + status + "\",\r\n"
					+ "    \"state\": \"Completed\",\r\n" + "    \"comment\": \"Execution failed\",\r\n"
					+ "    \"associatedBugs\": [\r\n" + "      {\r\n" + "        \"id\":" + bugid + "      }\r\n"
					+ "    ]" + "  }";
		}
		System.out.println("=============================================");
		return payload;
		
	}

	private static String createBug(String title) throws IOException {
		
		String URL = "https://dev.azure.com/" + System.getProperty("organization") + "/" + System.getProperty("project")
				+ "/_apis/wit/workitems/$bug?api-version=5.0";
		String payload = payload(title);
		String response = APIRequest(URL, "POST", payload, "application/json-patch+json");
		String jsonpath = "$.id";
		String bugID = JsonPath.read(response, jsonpath).toString();
		System.out.println("Creating Bug for "+title + ". Bug ID is "+bugID);
		return bugID;
	}

	private static void closeBug(String title) throws IOException {
		String queryURL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
				+ System.getProperty("project") + "/_apis/wit/wiql?api-version=6.0";
		String querypayload = "{\r\n"
				+ "  \"query\": \"Select [System.Id] From WorkItems Where [System.WorkItemType] = 'Bug' AND [State] = 'New' AND [System.Title] = '"
				+ title + "- Failed' AND [Area Path] = '" + System.getProperty("project") + "'\" \r\n" + "}";
		System.out.println(querypayload);
		String bugresponse = APIRequest(queryURL, "POST", querypayload, "application/json");
		System.out.println(bugresponse);
		System.out.println("Work Item Payload: "+JsonPath.read(bugresponse, "$.workItems").toString());
		if (!JsonPath.read(bugresponse, "$.workItems").toString().equals("[]")) {
			String bugid = JsonPath.read(bugresponse, "$.workItems[0].id").toString();
			System.out.println("Bug ID to be closed " + bugid);
			String UpdateURL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
					+ System.getProperty("project") + "/_apis/wit/workitems/" + bugid + "?api-version=6.0";
			String payload = "[{\"op\":\"test\",\"path\":\"/rev\",\"value\":2},{\"op\":\"add\",\"path\":\"/fields/System.State\",\"value\":\"Done\"}]";
			APIRequest(UpdateURL, "PATCH", payload, "application/json-patch+json");
		}
	}

	private static void linkBugwithTestCase(String testcasename, String bugID) throws IOException {
		
    	String queryURL = "https://dev.azure.com/" + System.getProperty("organization") + "/"
				+ System.getProperty("project") + "/_apis/wit/wiql?api-version=6.0";
		String querypayload = "{\r\n"
				+ "  \"query\": \"Select [System.Id] From WorkItems Where [System.WorkItemType] = 'Test Case' AND [System.Title] = '"
				+ testcasename + "' AND [Area Path] = '" + System.getProperty("project") + "'\" \r\n" + "}";
		
		String testcaseresponse = APIRequest(queryURL, "POST", querypayload, "application/json");
		String jsonpath = "$.workItems[0].id";
		String testcaseID = JsonPath.read(testcaseresponse, jsonpath).toString();
		
        String linkpayload = "[{\"op\":\"test\",\"path\":\"/rev\",\"value\":1},{\"op\":\"add\",\"path\":\"/relations/-\",\"value\":{\"rel\":\"System.LinkTypes.Hierarchy-Reverse\",\"url\":\"https://dev.azure.com/"+System.getProperty("project")+"/_apis/wit/workItems/"+testcaseID+"\",\"attributes\":{\"comment\":\"Making a new link for the dependency\"}}}]";
        String linkURL = "https://dev.azure.com/"+ System.getProperty("organization") +"/"+ System.getProperty("project") +"/_apis/wit/workitems/"+ bugID +"?api-version=6.0-preview.3";
        APIRequest(linkURL, "PATCH", linkpayload, "application/json-patch+json"); 
	}

	private static void allowMethods(String... methods) {
		try {
			Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);
			methodsField.setAccessible(true);
			String[] oldMethods = (String[]) methodsField.get(null);
			Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
			methodsSet.addAll(Arrays.asList(methods));
			String[] newMethods = methodsSet.toArray(new String[0]);
			methodsField.set(null, newMethods);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

}
