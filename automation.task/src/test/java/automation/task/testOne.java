package automation.task;

import java.time.Duration;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

public class testOne {

	/* I have automated the process for the provided requirements and
	 * the products that do not have 'Table' in its title, I've printed 
	 * them on the console. And at the end to verify my cart is empty, 
	 * I used an assertion to make sure I'm getting a message that says
	 * your cart is empty after emptying my cart.
	 */
	
	@Test
	public void myTest() throws InterruptedException {
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.webstaurantstore.com/");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(20));

		WebElement searchBox = driver.findElement(By.id("searchval"));
		WebElement submitButton = driver.findElement(By.xpath("//*[@value='Search']"));
		searchBox.sendKeys("stainless work table");

		By productListingContainerLocator = By.xpath("//div[@id='product_listing']");
		ExpectedCondition<WebElement> productListingContainerExpectedCondition = ExpectedConditions
				.presenceOfElementLocated(productListingContainerLocator);

		submitButton.click();

		webDriverWait.until(productListingContainerExpectedCondition); // waits until all products loads
		By nextPageLocator = By.xpath("//div[@id='paging']/*/ul/li");
		List<WebElement> pagesList = driver.findElements(nextPageLocator);
		boolean moreThanOnePageExists = pagesList.size() > 0;
		int lastPageCounter = 0;
		int currentPage = 1;
		By lastPageLocator = By.xpath("//div[@id='paging']/*/ul/li/a[contains(@aria-label,'last page')]");
		WebElement lastPageElement = driver.findElement(lastPageLocator);
		String lastPageCounterExtractor = lastPageElement.getAttribute("aria-label");

		List<WebElement> searchResultProductList;
		int numberOfProducts_PerPage = 0;
		searchResultProductList = driver
				.findElements(By.xpath("//div[@id='product_listing']/div[@id='ProductBoxContainer']"));
		numberOfProducts_PerPage = searchResultProductList.size();

		System.out.println("is more than one page available " + moreThanOnePageExists);
		if (moreThanOnePageExists) {
			lastPageCounter = Integer.parseInt(lastPageCounterExtractor
					.substring(lastPageCounterExtractor.lastIndexOf(" "), lastPageCounterExtractor.length()).trim());
		}
		System.out.println("Total number of pages to navigate " + lastPageCounter);

		By eachProductLocator = By.xpath("//div[@id='product_listing']/div[@id='ProductBoxContainer']");
		List<WebElement> productList;

		if (numberOfProducts_PerPage > 0) {
			do {
				System.out.println("Navigating page number " + currentPage);
				pagesList = driver.findElements(nextPageLocator);// redefine element since dom changed to avoid statle
																	// element exception
				pagesList.get(pagesList.size() - 1).click();
				webDriverWait.until(productListingContainerExpectedCondition); // waits until all products loads
				productList = driver.findElements(eachProductLocator);
				verifyProductTitle(productList, "Table", currentPage);
				currentPage++;
			} while (currentPage <= lastPageCounter);

			WebElement lastProductInPage = productList.get(productList.size() - 1);
			String xpathAddToCart = "//a[text()='Advance Tabco TH2G-366 Wood Top Work Table with Galvanized Base - 36\" x 72\"']/parent::div/following::div[@class='add-to-cart']";
			System.out.println(
					"Last prodcut in the search result is " + System.lineSeparator() + lastProductInPage.getText());

			driver.findElement(By.xpath(xpathAddToCart)).click();

			WebElement cartConfirmation = driver.findElement(By.xpath("//a[text()='View Cart']"));
			Thread.sleep(3000);
			cartConfirmation.click();

			WebElement cartButton = driver.findElement(By.xpath("//span[text()='Cart']"));

			cartButton.click();

			WebElement emptyCartButton = driver.findElement(By.xpath("//button[text()='Empty Cart']"));

			emptyCartButton.click();

			WebElement emptyCartConfirmation = driver.findElement(By.xpath("//footer/button[text()='Empty']"));
			emptyCartConfirmation.click();

			WebElement emptyCartMessage = driver.findElement(By.xpath("//p[text()='Your cart is empty.']"));
			String message = emptyCartMessage.getText();
			Assert.assertEquals("Your cart is empty.", message);

			driver.close();

		}

	}

	private void verifyProductTitle(List<WebElement> productList, String expectedTitle, int pageNumber) {
		boolean doesContainExpectedTitle = false;
		for (WebElement product : productList) {
			doesContainExpectedTitle = product.getText().contains(expectedTitle);
			if (!doesContainExpectedTitle)
				System.out.println("This product does not have the expectedTitle for page number " + pageNumber
						+ " and product " + System.lineSeparator() + product.getText());

		}

	}

}
