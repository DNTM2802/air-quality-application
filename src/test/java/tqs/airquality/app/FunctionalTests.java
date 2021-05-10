package tqs.airquality.app;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SeleniumJupiter.class)
public class FunctionalTests {

    HtmlUnitDriver driver;
    Map<String, Object> vars;
    JavascriptExecutor js;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    FunctionalTests(HtmlUnitDriver driver) {
        this.driver = driver;
        js = driver;
        vars = new HashMap<>();
    }

    @Test
    public void checkCache() {
        driver.get("http://localhost:8080/cache");
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is("Cache Information"));
        {
            List<WebElement> elements = driver.findElements(By.xpath("//td"));
            assert(elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.xpath("//table[2]/tbody/tr/td"));
            assert(elements.size() > 0);
        }
        {
            List<WebElement> elements = driver.findElements(By.xpath("//table[3]/tbody/tr/td"));
            assert(elements.size() > 0);
        }
    }

    @Test
    public void searchInvalidAddressForecast() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("textSearch")).click();
        driver.findElement(By.id("textSearch")).sendKeys("tqstqstqstqstqs");
        driver.findElement(By.id("scope")).click();
        {
            WebElement dropdown = driver.findElement(By.id("scope"));
            dropdown.findElement(By.xpath("//option[. = 'Forecast']")).click();
        }
        driver.findElement(By.cssSelector("option:nth-child(2)")).click();
        driver.findElement(By.cssSelector("button")).click();
        {
            List<WebElement> elements = driver.findElements(By.cssSelector("span:nth-child(3)"));
            assert(elements.size() > 0);
        }
    }

    @Test
    public void searchInvalidAddressHistorical() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("textSearch")).click();
        driver.findElement(By.id("textSearch")).sendKeys("tqstqstqstqstqs");
        driver.findElement(By.id("scope")).click();
        {
            WebElement dropdown = driver.findElement(By.id("scope"));
            dropdown.findElement(By.xpath("//option[. = 'Historical']")).click();
        }
        driver.findElement(By.id("datepickerEndDate")).sendKeys("08/04/2021");
        driver.findElement(By.id("datepickerStartDate")).sendKeys("01/04/2021");
        driver.findElement(By.cssSelector("option:nth-child(3)")).click();
        driver.findElement(By.cssSelector("button")).click();
        {
            List<WebElement> elements = driver.findElements(By.cssSelector("span:nth-child(3)"));
            assert(elements.size() > 0);
        }
    }

    @Test
    public void searchInvalidAddressToday() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("textSearch")).click();
        driver.findElement(By.id("textSearch")).sendKeys("tqstqstqstqstqs");
        driver.findElement(By.cssSelector("button")).click();
        {
            List<WebElement> elements = driver.findElements(By.cssSelector("span:nth-child(3)"));
            assert(elements.size() > 0);
        }
    }

    @Test
    public void searchValidAddressForecast() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("scope")).click();
        {
            WebElement dropdown = driver.findElement(By.id("scope"));
            dropdown.findElement(By.xpath("//option[. = 'Forecast']")).click();
        }
        driver.findElement(By.cssSelector("option:nth-child(2)")).click();
        driver.findElement(By.id("textSearch")).click();
        driver.findElement(By.id("textSearch")).sendKeys("Murtosa");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is("Forecast Data"));
        {
            List<WebElement> elements = driver.findElements(By.cssSelector("tr:nth-child(5) > td:nth-child(9)"));
            assert(elements.size() > 0);
        }
    }

    @Test
    public void searchValidAddressHistorical() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("textSearch")).click();
        driver.findElement(By.id("textSearch")).sendKeys("Murtosa");
        driver.findElement(By.id("scope")).click();
        {
            WebElement dropdown = driver.findElement(By.id("scope"));
            dropdown.findElement(By.xpath("//option[. = 'Historical']")).click();
        }
        driver.findElement(By.cssSelector("option:nth-child(3)")).click();
        driver.findElement(By.id("datepickerStartDate")).click();
        driver.findElement(By.id("datepickerStartDate")).sendKeys("01/04/2021");
        driver.findElement(By.id("datepickerEndDate")).click();
        driver.findElement(By.id("datepickerEndDate")).sendKeys("08/04/2021");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.findElement(By.cssSelector("h2")).getText(), is("Historical Data"));
        assertThat(driver.findElement(By.cssSelector("tr:nth-child(1) > td:nth-child(9)")).getText(), is("2021-04-01"));
        assertThat(driver.findElement(By.cssSelector("tr:nth-child(8) > td:nth-child(9)")).getText(), is("2021-04-08"));
    }


    @Test
    public void searchValidAddressToday() {
        driver.get("http://localhost:8080/");
        driver.findElement(By.id("textSearch")).click();
        driver.findElement(By.id("textSearch")).sendKeys("Murtosa");
        driver.findElement(By.cssSelector("button")).click();
        assertThat(driver.findElement(By.cssSelector("body > h3")).getText(), is("Today Data"));
        assertThat(driver.findElement(By.cssSelector("p:nth-child(18)")).getText(), is(LocalDate.now().format(formatter)));
    }

}
