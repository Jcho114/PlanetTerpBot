package gradle.planet.terp.scraper.bot.scraper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class PlanetTerpWebScraper {
	private WebDriver driver;
	private boolean error = false;
	
	{
		WebDriverManager.chromedriver().setup();
	}
	
	public PlanetTerpWebScraper(String professorName) {
		// Make it so that selenium doesn't open another chrome instance
		// It just runs on the background instead
		final ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		driver = new ChromeDriver(chromeOptions);
		
		try {
			driver.get("https://planetterp.com/grades");
			driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
			driver.get(getURL(professorName));
		} catch (Exception e) {
			error = true;
		}
	}
	
	public ProfessorProfile createProfile() {
		String professorName = "";
		double professorRating = 0.0;
		Set<String> professorCourses = new TreeSet<>();
		try {
			professorName = driver.findElement(By.className("professor-header")).getText();
			professorRating = Double.valueOf(driver.findElement(By.id("professor_rating")).getText());
	
			List<WebElement> professorCoursesBeforeFilter = driver.findElements(By.tagName("a"));
			List<String> courseListFilter = getCourseList();
			
			for (WebElement element : professorCoursesBeforeFilter) {
				String elementText = element.getText();
				
				if (elementText.length() >= 4 && courseListFilter.contains(elementText.substring(0, 4))) {
					professorCourses.add(elementText);
				}
			}
		} catch (Exception e) {
			error = true;
		}
		
		return new ProfessorProfile(professorName, professorRating, professorCourses);
	}
	
	public void close() {
		driver.close();
	}
	
	public boolean hasError() {
		return error;
	}
	
	private String getURL(String professorName) {
		String url;
		
		WebElement professorSearch = driver.findElement(By.id("professor-search"));
		professorSearch.sendKeys(professorName);
			
		WebElement dropDownMenu = driver.findElement(By.id("ui-id-2"));
		List<WebElement> options = dropDownMenu.findElements(By.className("ui-menu-item-wrapper"));
			
		for (WebElement option : options) {
			if (option.getText().toLowerCase().equals(professorName.toLowerCase())) {
				option.click();
			}
		}
			
		url = driver.findElement(By.id("grades-information")).findElement(By.tagName("a")).getAttribute("href");
			
		if (url == null || url.isEmpty()) {
			System.out.println(options);
			url = "https://planetterp.com/grades";
		}
		
		return url;
	}
	
	private List<String> getCourseList() {
		File file = new File("src/main/resources/course-list");
		List<String> courseList = new ArrayList<>();
		
		try {
			Scanner scan = new Scanner(file);
			
			while (scan.hasNextLine()) {
				courseList.add(scan.nextLine());
			}
			
			scan.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
		
		return courseList;
	}
}
