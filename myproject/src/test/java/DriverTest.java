import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import io.github.bonigarcia.wdm.WebDriverManager;
// Для логирования
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DriverTest {

    private static final Logger logger = LogManager.getLogger(DriverTest.class);

    static WebDriver driver;

    private String baseUrl = "";
    private String browser = "";

    // Конфигурация ожиданий
    private final Duration IMPLICIT_WAIT = Duration.ofSeconds(10);
    private final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(30);

    // Известные элементы
    private String xpathUsername = "//*[@id='username']";
    private String xpathEmail = "//*[@id='email']";
    private String xpathPassword = "//*[@id='password']";
    private String xpathPasswordConfirm = "//*[@id='confirm_password']";
    private String xpathBirthDate = "//*[@id='birthdate']";

    private String xpathLanguageLevelDropdown = "//*[@id='language_level']";
    private String xpathLanguageLevelDropdownOptionAdvanced = xpathLanguageLevelDropdown + "/option[text()='Продвинутый']";

    private String xpathButtonSubmitForm = "//*[@id='registrationForm']/input[@type='submit']";

    private String xpathOutputRegistrationData = "//*[@id='output']";

    @BeforeAll
    public static void driver_setup() {
        logger.info("Установка драйвера для авто-тестов");
        if (System.getProperty("browser") == null) {
            logger.error("Укажите браузер для запуска в -Dbrowser");
        } else {
            try {
                if (System.getProperty("browser").equals("edge")) {
                    logger.info("Выбран драйвер edge");
                    WebDriverManager.edgedriver().setup();
                }
                if (System.getProperty("browser").equals("chrome")) {
                    logger.info("Выбран драйвер chrome");
                    WebDriverManager.chromedriver().setup();
                }
            } catch (Exception e) {
                logger.error("Возникла ошибка: {}", e.getMessage());
                System.out.println(e);
                System.exit(1);
            }
        }
    }

    @BeforeEach
    public void driver_start() {
        browser = System.getProperty("browser");
        logger.info("Настраиваем драйвер перед запуском");
        if (browser.equals("edge")) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments(System.getProperty("launchFlag", "--start-maximized")); // --kiosk --headless --start-maximized
            driver = new EdgeDriver(options);
        }
        if (browser.equals("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments(System.getProperty("launchFlag", "--start-maximized")); // --kiosk --headless --start-maximized
            driver = new ChromeDriver(options);
        }

        // НАСТРОЙКА ОЖИДАНИЙ
        configureWaits();

        // ПОЛУЧЕНИЕ СТАРТОВОЙ СТРАНИЦЫ
        this.baseUrl = System.getProperty("baseUrl", "https://otus.home.kartushin.su/form.html");
        logger.info("Переходим по базовому URL: {}", this.baseUrl);
        driver.get(this.baseUrl);
    }

    /** * Настройка явных и неявных ожиданий */
    private void configureWaits() {
        // НЕЯВНОЕ ОЖИДАНИЕ - применяется ко всем findElement операциям
        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT);
        // Таймаут загрузки страницы
        driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT);
    }

    @AfterEach
    public void close_and_quit() {
        if (driver != null) {
            logger.info("Закрытие браузера после теста");
            driver.close();
            driver.quit();
            logger.debug("Браузер успешно закрыт");
        }
    }

    @Test
    public void testFillingFormAndSend() {
        logger.info("Тест: Заполнение формы и отправка");

        String text_username = System.getProperty("login", "Test1");
        WebElement input_username = driver.findElement(By.xpath(this.xpathUsername));
        input_username.sendKeys(text_username);
        assertEquals(text_username, input_username.getAttribute("value"), "Имя пользователя должно совпадать");
        logger.info("Заполнил поле 'Имя пользователя' значением: {}", text_username);

        String text_email = "test@example.com";
        WebElement input_email = driver.findElement(By.xpath(this.xpathEmail));
        input_email.sendKeys(text_email);
        assertEquals(text_email, input_email.getAttribute("value"), "Email должен совпадать");
        logger.info("Заполнил поле 'Электронная почта' значением: {}", text_email);

        String text_password = System.getProperty("password", "test_password");
        WebElement input_password = driver.findElement(By.xpath(this.xpathPassword));
        input_password.sendKeys(text_password);
        logger.info("Заполнил поле 'Пароль' значением: {}", text_password);

        WebElement input_password_confirm = driver.findElement(By.xpath(this.xpathPasswordConfirm));
        input_password_confirm.sendKeys(text_password);
        logger.info("Заполнил поле 'Подтвердите пароль' значением: {}", text_password);

        String text_birth_date = "11.11.2025";
        String text_btith_date_for_check = "2025-11-11";
        WebElement input_birth_date = driver.findElement(By.xpath(this.xpathBirthDate));
        input_birth_date.sendKeys(text_birth_date);
        assertEquals(text_btith_date_for_check, input_birth_date.getAttribute("value"), "Дата рождения должна совпадать");
        logger.info("Заполнил поле 'Дата рождения' значением: {}", text_birth_date);

        logger.info("Находим и раскрываем выпадающий список с уровнями алвдения языка");
        WebElement select_language_level = driver.findElement(By.xpath(this.xpathLanguageLevelDropdown));
        select_language_level.click();
        logger.info("Находим уровень 'Продвинутый' и выбираем его");
        WebElement option = driver.findElement(By.xpath(xpathLanguageLevelDropdownOptionAdvanced));
        option.click();
        logger.info("Нажимаю на выпадающий список для его закрытия");
        select_language_level.click();

        logger.info("Нажимаю на кнопку 'Зарегистрироваться'");
        WebElement input_submit = driver.findElement(By.xpath(this.xpathButtonSubmitForm));
        input_submit.click();

        
        WebElement output_data = driver.findElement(By.xpath(this.xpathOutputRegistrationData));
        String output_data_inner_text = output_data.getText();
        logger.info("Данные в поле output: {}", output_data_inner_text);
        logger.info("Проверяю, что логин пользоватля верно указан");
        
        assertTrue(output_data_inner_text.contains(String.format("Имя пользователя: %s", text_username)), "Неверное имя пользвоателя в окне с выводом данных");
        assertTrue(output_data_inner_text.contains(String.format("Электронная почта: %s", text_email)), "Неверное электронная почта пользвоателя в окне с выводом данных");
        assertTrue(output_data_inner_text.contains(String.format("Дата рождения: %s", text_btith_date_for_check)), "Неверная дата рождения пользвоателя в окне с выводом данных");
        assertTrue(output_data_inner_text.contains(String.format("Уровень языка: %s", option.getAttribute("value"))), "Не верный уровень языка пользвоателя в окне с выводом данных");
        
        logger.info("Тест успешно завершен");
    }
}