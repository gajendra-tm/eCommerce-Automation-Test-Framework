🧪 __eCommerce Automation Test Framework__

**Note:** This framework is still under active development.

🚧 __Project Status:__ In Progress

This automation testing framework is currently under active development. Several core components and best practices have been implemented, while additional features and enhancements are in progress. Contributions and feedback are welcome as the project evolves.

A hybrid automation testing framework built with __Java + Selenium + TestNG + Allure,__ following modern design principles and best practices.


📌 __Project Highlights__

- __Test Framework:__ TestNG (v7.8.0)

- __Design Pattern:__ Hybrid (PageFactory + Utility-based)

- __Reporting:__ Allure Reports with integrated log and screenshot attachments

- __Logging:__ Log4j2-based centralized logging

- __Cross-browser testing:__ Parameterized via testng.xml

- __Retry logic:__ Custom RetryAnalyzer with dynamic retry via Config.properties

- __Data-driven testing:__ Supports Excel, JSON, CSV, Database

- __CI-ready:__ GitHub integration ready, Docker-compatible (to be implemented)

🔧 __Features Implemented__

✅ __Core Utilities__
- __ConfigReader:__ Loads environment-based config properties

- __TimeConstants / AppConstants:__ Centralized constants for reusable configuration

✅ __Page Object Model__
- Pages implemented using __@FindBy__ and __PageFactory__

- Wrapper classes for Button, Dropdown, InputField

✅ __Test Execution__
- __BaseTest:__ Parameterized browser setup (driven via XML)

- __DriverManager:__ Thread-safe driver instantiation

- __RetryAnalyzer:__ Retry logic wired through config

✅ __Reporting__
- __Allure Integration:__ Logs, screenshots, and test steps

- __Custom LogUtils:__ Attaches logs to Allure

✅ __Data Providers__
- __ExcelUtils, JsonUtils:__ Read structured test data

- __CSV / Database support:__ Extensible architecture

✅ __Robust Waits & Element Handling__
- __WaitUtils:__ Custom wait logic using FluentWait & constants

- __WebElementUtils:__ Abstracted element actions

__Installation & Setup__

__1. Prerequisites:__
- __Java JDK:__ Install Java 8 or higher
- __Maven:__ Install Maven for building and running tests
- __IDE:__ (Optional) Use an IDE like IntelliJ IDEA or Eclipse for development.
- __Browsers/WebDrivers:__ Chrome, Firefox, or other browsers plus corresponding WebDriver binaries (managed by WebDriverManager or manually placed on PATH).
- __Allure reporting:__ For generating Allure reports

__2. Project Setup:__
- Clone the repository: __git clone git@github.com:gajendra-tm/eCommerce-Automation-Test-Framework.git__
- Import the project in your IDE as a Maven project, or run __mvn clean install__ from the project root to download dependencies.
- Ensure __src/main/resources__ or configuration folders contain the necessary properties (e.g. __config.properties,__ test data files).

__3. Configure Environments:__
- Edit __ConfigReader.java__ or property files to point to the correct environments (URLs, credentials).
- Verify browser drivers or Selenium Grid endpoints if using remote execution (to be implemented).
  
__4. Reports Folder (for CI/CD):__
- Test results (Allure reports) by default go under __target/allure-results.__ Configure your CI to archive these artifacts or publish them via an Allure plugin (to be implemented).

__Running Tests__
- __Via Maven:__ Use Maven commands to execute tests. For example:
  - __mvn clean test__ – Runs all tests in the default suite (TestNG’s __testng.xml__).
  - __mvn clean test -DsuiteXmlFile=testng.xml__ – Specify a particular TestNG suite file or set parameters like parallel mode and thread count via Maven properties.

- __Parallel Execution:__ Enable parallel runs by setting TestNG __parallel__ attribute in __testng.xml__ or via Maven. This utilizes TestNG’s multithreading (e.g., __<suite parallel="test/true/methods" thread-count="4">__). Which reduce total execution time​.
 
- __Allure Reporting:__ After tests complete, generate the Allure report:
  - __allure serve target/allure-results__ (opens a local web server with the report)​.
  - Or __allure generate target/allure-results --clean -o target/allure-report__ to create static HTML files.

Continuous Integration pipelines can run these commands and automatically publish the Allure report as a build artifact or dashboard.

__Conclusion__

This project provides a robust framework for __automating eCommerce application tests__ using best practices in software testing and test automation. It leverages the power of __TestNG, Allure reporting,__ and a clean modular approach with __reusable utilities,__ ensuring efficient and maintainable tests.

__License__

This project is licensed under the __MIT License__ – see the LICENSE file for details. All third-party dependencies comply with open-source licensing as documented in __pom.xml.__

