package com.liquiBase;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TC01_VerifyUserInfo  {
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
		
		//Click on Settings icon top left corner
		WebElement settingsButton = driver.findElement(By.xpath("//*[text()='settings']/parent::div/.."));
		settingsButton.click();
		
		//Click on User Info
		WebElement userInfoButton = driver.findElement(By.xpath("//*[text()='User Info']/parent::div"));
		userInfoButton.click();
		
		//Creating fake user name and last name
		Faker faker =new Faker();
		String firstName = faker.name().firstName();
		String lastName = faker.name().lastName();
//		System.out.println(firstName+" "+lastName);
		waitFor(1);
		
		WebElement firstNameField = driver.findElement(By.xpath("//*[text()='First Name']/ancestor::div[contains(@class, '-infix')]/input"));
		WebElement lastNameField = driver.findElement(By.xpath("//*[text()='Last Name']/ancestor::div[contains(@class, '-infix')]/input"));
		WebElement biographyField = driver.findElement(By.xpath("//*[text()='Bio']/ancestor::div[contains(@class, \"-infix\")]/textarea"));
		WebElement updateProfileButton = driver.findElement(By.xpath("//*[text()='Update Profile']/parent::button"));
		
		firstNameField.clear(); //clear the field
		firstNameField.sendKeys(firstName); //pass randomly generated name
		lastNameField.clear();
		lastNameField.sendKeys(lastName);
		biographyField.clear();
		biographyField.sendKeys(faker.chuckNorris().fact());
		updateProfileButton.click();
		
		//after saving wait until update profile button is disabled 
		WebElement updateProfileButtonDisabled = driver.findElement(By.xpath("//*[text()='Update Profile']/parent::button[@disabled=\"true\"]"));
		waitForVisibility(updateProfileButtonDisabled, 10);
		
		waitFor(1);
		
		//Create assertions for the above fields, We can click different tab and come back then assert the fields
		// making sure newly inserted fields saved successfully
		WebElement apiKeysButton = driver.findElement(By.xpath("//*[text()='API Keys']/parent::div"));
		apiKeysButton.click();
		waitForPageToLoad(5);
		waitFor(1);
		
		//Click again User Info and Assert the fields
		userInfoButton.click();
		waitFor(1);
		
		String firstNameField2 = driver.findElement(By.xpath("//*[text()='First Name']/ancestor::div[contains(@class, '-infix')]/input")).getAttribute("value");
		String lastNameField2 = driver.findElement(By.xpath("//*[text()='Last Name']/ancestor::div[contains(@class, '-infix')]/input")).getAttribute("value");
		
		//Verifying first name
		Assert.assertEquals(firstNameField2, firstName, "First Name is not correct, not saved from previous input");
		Assert.assertEquals(lastNameField2, lastName, "Last Name is not correct, not saved from previous input");
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
