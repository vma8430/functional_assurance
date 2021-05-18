package runner;

import cucumber.api.CucumberOptions;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.junit.Cucumber;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import stepdefinitions.CukeHooks;
import runner.TestPlan;

@RunWith(Cucumber.class)
@CucumberOptions(strict = true, monochrome = true, features = "src/test/resources/features/", tags = "@UITest", glue = {
		"stepdefinitions" }, plugin = { "junit:target/junitreport.xml", "json:target/jsonreport.json",
				"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:" }

)
public class TestsRunner {

	private TestsRunner() {

	}

	protected static final Logger LOG = LoggerFactory.getLogger(TestsRunner.class);
	protected static Map<String, String> UITest = new HashMap<String, String>();
	protected static Integer passedCount;
	protected static Integer failedCount;
	protected static String finalOutput = "{\"moduleName\":regressiontest_ui/regressiontest_api/regressiontest_rwd_ui,\"pass\":passCount,\"fail\":failCount,\"status\":In Progress/Pass/Fail};";

	@BeforeClass
	public static void beforeClass() {
		LOG.info("================ Inside Before Class ================");
		CukeHooks.passedCount = 0;
		CukeHooks.failedCount = 0;

		String functionalType = getFunctionalType();
		String strFunctionalType = "functionalType : " + functionalType;
		LOG.info(strFunctionalType);
		String status = "PASS";

		JSONObject sampleObject = new JSONObject();
		sampleObject.put("moduleName", functionalType);
		sampleObject.put("pass", 0);
		sampleObject.put("fail", 0);
		sampleObject.put("status", "In Progress");
	/*	String strDashboardHost;

		// String strDashboardIP = System.getenv("backendPrivateIP");
		String strDashboardIP = System.getProperty("dashboardHost");
		
		 * if (strDashboardIP == null || strDashboardIP.isEmpty()) { strDashboardHost =
		 * System.getProperty("dashboardHost");
		 * LOG.info("Dashboard URL Fetched from POM Settings"); } else {
		 * strDashboardHost = strDashboardIP;
		 * LOG.info("Dashboard URL Fetched from AWS ENVIRONMENT"); }
		 
		strDashboardHost = strDashboardIP;
		LOG.info("Dashboard URL Fetched from AZURE ENVIRONMENT");
		String strDashboardHostURL = "http://" + strDashboardHost
				+ ":3337/api/v1/azure_dashboard_report/moudule/update";
		String strAppurl = "API URL : " + strDashboardHostURL;
		LOG.info(strAppurl);

		LOG.info("================ DASHBOARD OBJECT ================");
		LOG.info(String.valueOf(sampleObject));
		try {
			Response response = RestAssured.given().contentType("application/json").body(sampleObject)
					.post(strDashboardHostURL);
			LOG.info(response.body().prettyPrint());
		} catch (Exception e) {
			LOG.info("UNABLE TO MAKE POST REQUEST TO THE DASHBOARD");
			LOG.info("================ START ERROR MESSAGE ================");
			LOG.info(e.getMessage());
			LOG.info("================ END ERROR MESSAGE ================");

		}
		*/
	}

	private static String getFunctionalType() {
		String functionalType = System.getProperty("cucumber.options").trim().toUpperCase();
		System.out.println("Tags:::::::::::::::: "+ functionalType);
		String functionalTypeLocal = "regressiontest_ui";
		return functionalTypeLocal;
	}

	@AfterClass
	public static void afterClass() throws IOException {
		LOG.info("================ Inside After Class ================");
		System.out.println("Features :" + CukeHooks.features);
		System.out.println(" Updating Test Plan ...");
		if (CukeHooks.features.toString().contains("UITest")) {
			for (Entry<String, String> set : CukeHooks.features.entrySet()) {
				if (set.getKey().contains("UITest"))
					UITest.put(set.getKey().split(":")[1] + ":" + set.getKey().split(":")[2], set.getValue());
			}
		}
		if (!UITest.isEmpty()) {
			TestPlan.updateTestResults("UITest", UITest);
		}
		LOG.info("================ Test Plan updated ================");
		String functionalType = getFunctionalType();
		String strFunctionalType = "functionalType : " + functionalType;
		LOG.info(strFunctionalType);
		String status = "PASS";
		if (CukeHooks.failedCount > 0) {
			status = "FAIL";
		}

		JSONObject sampleObject = new JSONObject();
		sampleObject.put("moduleName", functionalType);
		sampleObject.put("pass", CukeHooks.passedCount);
		sampleObject.put("fail", CukeHooks.failedCount);
		sampleObject.put("status", status);
		/*String strDashboardHost;

		// String strDashboardIP = System.getenv("backendPrivateIP");
		String strDashboardIP = System.getProperty("dashboardHost");
		
		 * if (strDashboardIP == null || strDashboardIP.isEmpty()) { strDashboardHost =
		 * System.getProperty("dashboardHost");
		 * LOG.info("Dashboard URL Fetched from POM Settings"); } else {
		 * strDashboardHost = strDashboardIP;
		 * LOG.info("Dashboard URL Fetched from Azure ENVIRONMENT"); }
		 
		strDashboardHost = strDashboardIP;
		LOG.info("Dashboard URL Fetched from AZURE ENVIRONMENT");
		String strDashboardHostURL = "http://" + strDashboardHost
				+ ":3337/api/v1/azure_dashboard_report/moudule/update";
		String strAppurl = "API URL : " + strDashboardHostURL;
		LOG.info(strAppurl);

		LOG.info("================ DASHBOARD OBJECT ================");
		LOG.info(String.valueOf(sampleObject));
		try {
			Response response = RestAssured.given().contentType("application/json").body(sampleObject)
					.post(strDashboardHostURL);
			LOG.info(response.body().prettyPrint());
		} catch (Exception e) {
			LOG.info("UNABLE TO MAKE POST REQUEST TO THE DASHBOARD");
			LOG.info("================ START ERROR MESSAGE ================");
			LOG.info(e.getMessage());
			LOG.info("================ END ERROR MESSAGE ================");

		}
		*/
	}

}
