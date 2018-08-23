package testlogic;
import frameworklibrary.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;




/**
 * @author 609684083
 *
 */
public class WebPage {
	public static WebDriver driver;

	public WebPage(){
		driver = WebDriverFactory.getWebDriverObject();
	}

	//************************* UserDefined Keywords **************************************

	public static void submit(Locator locator){
		locateElement(locator).submit();
	}

	public static void refreshPage(){
		driver.navigate().refresh();
		waitForPageToLoad();
	}

	public static void navigateToPreviousPage(){
		driver.navigate().back();
		waitForPageToLoad();
	}

	public static void snooze(long secs){	
		try {
			Thread.sleep(secs);
		} catch (InterruptedException e) {
			System.out.println("Sleep Interrupted (Exception Caused)");
			e.printStackTrace();
		}
	}


	public static void clickOnEleByTC(Locator locator){

		String loctype =locator.key,
				locvalue = locator.value;
		WebElement ele;

		if(loctype.equalsIgnoreCase("cssSelector")){
			try {
				ele= driver.findElement(By.cssSelector(locvalue));
				Thread.sleep(6000);
				ele.click();
			} catch(Exception e){
				Report.updateTestStepLog("Exception", e.getMessage(), "Fail");
			}

		} else if(loctype.equalsIgnoreCase("xpath")){
			try {
				ele= driver.findElement(By.xpath(locvalue));
				snooze(500);
				ele.click();
			} catch(Exception e){
				Report.updateTestStepLog("Exception", e.getMessage(), "Fail");
			}
		}



	}

	public static void waitForPageToLoad() {

		if(driver instanceof ChromeDriver){
			int secs = 0;
			while(secs<=30){
				snooze(1000);
				secs++;
				if(((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete"))
					break;
				else
					continue;
			}
		} else {
			ExpectedCondition<Boolean> expectation = new
					ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {
					return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
				}
			};

			WebDriverWait wait = new WebDriverWait(driver,30);
			try {
				wait.until(expectation);
			} catch(Throwable error) {
				System.out.println("Exception: Timeout waiting for Page Load Request to complete.");
				error.printStackTrace();
			}
		}
	} 

	public static void switchToBrowserTab(int browserTab){
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());

		if(tabs.size()>0){
			driver.switchTo().window(tabs.get(browserTab));	
		} else
			Report.updateTestStepLog("Switching to other tab", "There is no other window opened", "Warning");
	}

	// **********************************Action Class methods Start **********************************

	public static void moveToElement(Locator locator){
		Actions actions = new Actions(driver);
		actions.moveToElement(locateElement(locator)).perform();
		snooze(5000);
	}

	public static void moveToElement(Locator locator, String target, String replacement){
		Actions actions = new Actions(driver);
		actions.moveToElement(locateElement(locator,target,replacement)).perform();
		snooze(5000);
	}

	// **********************************Action Class methods End**********************************


	// *****************************  Third party website methods *************************************
	
	
	}
	
	public static void selectDropDownValueByText(Locator locator, String text){
		Select select = new Select(locateElement(locator));
		select.selectByVisibleText(text);
	}

	public static void selectDropDownValueByVal(Locator locator, String value){
		Select select = new Select(locateElement(locator));
		select.selectByValue(value);
	}
	

	public static void selectCheckBox(Locator locator){

		WebElement element = locateElement(locator);
		if(!element.isSelected())
			element.click();
	}


	public static void deSelectCheckBox(Locator locator){
		WebElement element = locateElement(locator);
		if(element.isSelected())
			element.click();
	}

	

	public static String getTextOf(Locator locator, String target, String replacement){
		return locateElement(locator, target, replacement).getText();
	}

	public static String getHiddenTextOf(Locator locator, String target, String replacement){
		return (String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", 
				locateElement(locator, target, replacement));
	}

	public static String getHiddenTextOf(Locator locator){
		return (String)((JavascriptExecutor)driver).executeScript("return arguments[0].innerHTML;", 
				locateElement(locator));
	}


	public static void waitForVisibleElement(Locator locator, String target, String replacement, int timeOutInSeconds){

		if(driver instanceof InternetExplorerDriver){
			int secs = 0;
			while(secs<=timeOutInSeconds){
				snooze(1000);
				secs++;
				if(isDisplayed(locator,target, replacement))
					break;
				else
					continue;
			}
		} else {
			Wait wait = new WebDriverWait(driver, timeOutInSeconds);
			wait.until(ExpectedConditions.visibilityOf(locateElement(locator, target, replacement)));
		}
	}


	public static String getTextOf(Locator locator, String target1, String replacement1, String target2, String replacement2){
		return locateElement(locator, target1, replacement1, target2, replacement2).getText();

	}


	//********************************************For Regular Expressions***********************************************


	public static int getIndex(String string, String regex){

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string);
		int i=0;

		while(m.find()){	
			i = m.start();
			break;  // because I wanted only first occurrence. if we want the last occurrence remove break  
		}
		return i ;
	}

	public static int getEndIndex(String string, String regex){

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string);
		int i=0;

		while(m.find()){	
			i = m.end();
			break;  // because I wanted only first occurrence. if we want the last occurrence remove break  
		}
		return i ;
	}


	//***************************************************	Regular expressions **********************************
	//	Regular expression to match everything after a word = (?<=word).*
	//	Regular expression to match everything before a word = ^(.+?)word


	public static void switchToNewBrowserTab(){
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		int tabCount = tabs.size();
		--tabCount;
		if(tabCount>0){
			driver.switchTo().window(tabs.get(tabCount));	
			snooze(4000);
		} else
			Report.updateTestStepLog("Switching to other tab", "There is no other window opened", "Warning");
	}

	public static boolean isAlertPresent() { 
		try { 
			driver.switchTo().alert(); 
			return true; 
		}   
		catch (NoAlertPresentException Ex) { 
			return false; 
		}   
	} 

	public static void printMethodName(String message){
		Report.updateTestStepLog("<b><font color = \"blue\">Method Name</font></b>", "<b><font color = \"blue\">"+message+"</font></b>", "Information");
	}





}//EOC
