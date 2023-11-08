package demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class TestCases {

    private ChromeDriver driver;
    public TestCases() {
        System.out.println("Constructor: TestCases");
        WebDriverManager.chromedriver().timeout(30).setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    public void endTest() {
        System.out.println("End Test: TestCases");
        if (driver != null) {
            driver.quit();
        }
    }

    public static String readFileAsString(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("D:\\codes\\codes made on local\\selenium_starter\\src\\main\\testdata.json"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public void testCase01() {
        try {
            // Read JSON data from file
            String filePath = "testdata.json"; // Update the file path accordingly
            String jsonString = readFileAsString(filePath);
            JSONArray expectedData = new JSONArray(jsonString);

            // Open the web application
            driver.get("https://testpages.herokuapp.com/styled/tag/dynamic-table.html");

            // Click on the Table Data button
            WebElement tableDataButton = driver.findElement(By.xpath("//summary[text()='Table Data']"));
            tableDataButton.click();
            Thread.sleep(3000);

            // Insert the provided JSON data into the input text box
            WebElement inputField = driver.findElement(By.xpath("//div[@class='centered']//textarea[1]"));
            Thread.sleep(3000);
            inputField.clear();
            inputField.sendKeys(expectedData.toString());

            // Click on the Refresh Table button
            WebElement refreshButton = driver.findElement(By.tagName("button"));
            refreshButton.click();
            Thread.sleep(3000);

            // Fetch the data from the UI table
            List<JSONObject> tableDataFromUI = new ArrayList<>();
            List<WebElement> rows = driver.findElements(By.xpath("//table[@id='dynamictable']//tr"));
            for (int i = 1; i < rows.size(); i++) {
                List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
                if (cells.size() == 3) {
                    JSONObject obj = new JSONObject();
                    obj.put("gender", cells.get(0).getText());
                    obj.put("name", cells.get(1).getText());
                    obj.put("age", Integer.parseInt(cells.get(2).getText()));
                    tableDataFromUI.add(obj);
                }
            }

            // Assert if the data matches
            boolean isDataMatched = true;
            Map<String, String> expectedColumnNameMapping = new HashMap<>();
            expectedColumnNameMapping.put("gender", "gender");
            expectedColumnNameMapping.put("name", "name");
            expectedColumnNameMapping.put("age", "age");

            for (int i = 0; i < expectedData.length(); i++) {
                JSONObject expectedObject = expectedData.getJSONObject(i);
                JSONObject actualObject = tableDataFromUI.get(i);

                for (String expectedColumnName : expectedColumnNameMapping.keySet()) {
                    if (expectedColumnName.equals("age")) {
                        int expectedValue = expectedObject.getInt(expectedColumnName);
                        int actualValue = actualObject.getInt(expectedColumnNameMapping.get(expectedColumnName));
                        assert expectedValue == actualValue : "Data does not match";
                    } else {
                        String expectedValue = expectedObject.getString(expectedColumnName);
                        String actualValue = actualObject.getString(expectedColumnNameMapping.get(expectedColumnName));
                        assert expectedValue.equals(actualValue) : "Data does not match";
                    }
                }
            }
            System.out.println("Data stored and populated in the UI table match.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTest();
        }
    }
}
