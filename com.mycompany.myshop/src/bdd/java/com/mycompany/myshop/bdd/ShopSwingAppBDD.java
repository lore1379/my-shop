package com.mycompany.myshop.bdd;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/bdd/resources", monochrome = true)
public class ShopSwingAppBDD {
	
	@ClassRule
	public static final MongoDBContainer mongo =
		new MongoDBContainer("mongo:4.4.3");
	
	public static String getContainerIpAddress() {
		return mongo.getHost();
	}
	
	public static Integer getMappedPort() {
		return mongo.getFirstMappedPort();
	}
	
	@BeforeClass
	public static void setUpOnce() {
		FailOnThreadViolationRepaintManager.install();
	}

}
