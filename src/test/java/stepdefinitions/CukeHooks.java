package stepdefinitions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class CukeHooks extends MasterStepDefs {

	public static Integer passedCount;
	public static Integer failedCount;
	public static Map<String, String> status = new HashMap<String, String>();
	public static Map<String, String> features = new HashMap<String, String>();
	public static UUID uuid = UUID.randomUUID();
	@Before
	public static void setUp(Scenario scenario) {
		System.out.println("Starting - " + scenario.getName());
	}

	@After
	public static void tearDown(Scenario scenario) {
		System.out.println("=================== Inside After Hook ================ ");
		String scId = scenario.getId();
		String feature = scId.substring(scId.lastIndexOf('/')+1).split(".feature")[0];
		if (scenario.getSourceTagNames().contains("@UITest"))
			features.put("UITest:" + feature +":"+ scenario.getName(),scenario.getStatus().toString());
		else if (scenario.getSourceTagNames().contains("@APITest"))
			features.put("APITest:" + feature +":"+ scenario.getName(),scenario.getStatus().toString());
		else
			features.put("UIRWDTest:" + feature +":"+ scenario.getName(),scenario.getStatus().toString());
		System.out.println(scenario.getName() + " Status - " + scenario.getStatus());
		if (scenario.getStatus().toString().equalsIgnoreCase("failed")) {
			CukeHooks.failedCount += 1;
			System.out.println("=================== Count of Failed ===================");
			System.out.println(String.valueOf(CukeHooks.failedCount));
			
		} else if(scenario.getStatus().toString().equalsIgnoreCase("passed")) {
			CukeHooks.passedCount += 1;
			System.out.println("=================== Count of Passed ===================");
			System.out.println(String.valueOf(CukeHooks.passedCount));
			
		}
		
	}
}