package com.liquiBase;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TC02_DropdownZeroResult  {
	 static WebDriver driver; // static because other methods could see it
	
	@BeforeTest
	public void setUp(){
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://hub-staging.liquibase.com/landing-page");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}
	@Test
	public void VerifyUserInfo(){
		// Locating WebElements and Variables
		String userName = "tavarali"; //usually it stored in the .properties file, by config reader we can user it
		String password = "Qwerty123!";
		
		//Click Login Button
		WebElement loginButton = driver.findElement(By.xpath("//*[text()='Log In']/parent::button"));
		waitForVisibility(loginButton, 10);
		loginButton.click();
		waitForPageToLoad(10);
		
		//Verifying that we are on sign-in page
		String signInPageText = driver.findElement(By.cssSelector(".visible-lg .textDescription-customizable")).getText();
		Assert.assertEquals(signInPageText, "Sign in with your username and password");
		
		//Input User name and password and Sign in Click
		WebElement userNameField = driver.findElement(By.cssSelector(".visible-lg [name='username']"));
		WebElement passwordField = driver.findElement(By.cssSelector(".visible-lg [name='password']"));
		WebElement signInButton = driver.findElement(By.cssSelector(".visible-lg [name='signInSubmitButton']"));
		userNameField.sendKeys(userName);
		passwordField.sendKeys(password);
		signInButton.click();
		waitForPageToLoad(10);
		
		//Condition if getting started page appears clicks exit
		waitFor(1);
		Boolean welcomeHeader = driver.findElement(By.cssSelector(".welcome__heading__close>button")).isDisplayed();
		if(welcomeHeader) {
			System.out.println("Getting Started with Liquibase Hub displayed");
			driver.findElement(By.cssSelector(".welcome__heading__close>button")).click();
		}
		//Verifying that we are home page, asserting the user title
		String personalOrganize = driver.findElement(By.cssSelector(".organization-info__name")).getText();
		Assert.assertTrue(personalOrganize.contains(userName), "Search Result displays model number entered");
		
		
		//TC02_Click on the second option (Projects) in the left navigation
		WebElement projectsButton = driver.findElement(By.xpath("//*[text()='account_tree']/parent::div/.."));
		projectsButton.click();
		
		//Click to expand the default project.
		WebElement expandProject = driver.findElement(By.cssSelector(".projects-tree .mat-button-base"));
		expandProject.click();
		
		//Click on the Operations tab under this project.
		driver.findElement(By.xpath("//*[text()=\"Operations\"]/parent::div")).click();
		waitFor(1);
		
		//In the filter options, select Result = Pass.
		driver.findElement(By.xpath("//*[text()='Result']/ancestor::div[contains(@class, '-field-infix')]")).click();
		
		driver.findElement(By.xpath("//*[text()=' Pass ']")).click();
		waitFor(1);
		
		//Expand the dropdown No Operations to display should expect to have zero results.
		driver.findElement(By.xpath("//*[text()=' No Operations to display. ']/ancestor::div[contains(@class, 'empty-expandable')]")).click();
		
		String drpText = driver.findElement(By.cssSelector(".mat-expansion-panel-content .mat-expansion-panel-body")).getText();
		
		//Verifying drop down has no result
		Assert.assertTrue(drpText.contains("don't have any Operations"), "Drop down is empty and has correct text");
		waitFor(2);
		
		
	}
	
	@AfterClass
	public void tearDown() {
		driver.quit();
	}
	
	
	/* Useful Methods, could be in utilities package as BrowserUtil.class
	 * since we are creating one page i am inserting here
	 */
	
	// will wait until certain element is visible on the page, by passing 2 parameters 
	public static WebElement waitForVisibility(WebElement element, int timeToWaitInSec) {
		WebDriverWait wait = new WebDriverWait(driver, timeToWaitInSec);
		return wait.until(ExpectedConditions.visibilityOf(element));
	}
	
	public static void waitForPageToLoad(long timeOutInSeconds) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};
		try {
			System.out.println("Waiting for page to load...");
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
			wait.until(expectation);
		} catch (Throwable error) {
			System.out.println(
					"Timeout waiting for Page Load Request to complete after " + timeOutInSeconds + " seconds");
		}
	}
	
	public static void waitFor(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
} 
