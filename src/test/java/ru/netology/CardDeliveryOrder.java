package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.Keys;


import java.time.*;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;


public class CardDeliveryOrder {
    private String date = generateDate(3);
//    private String message = "Встреча успешно забронирована на";
//    private String errorNull = "Поле обязательно для заполнения";
//    private String errorCity = "Доставка в выбранный город недоступна";
//    private String errorName = "Имя и Фамилия указаные неверно.";
//    private String errorPhone = "Телефон указан неверно.";
//    private String errorDate = "Заказ на выбранную дату невозможен";

    public String generateDate (int shift){
        String date;
        LocalDate localDate = LocalDate.now().plusDays(shift);
        date = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(localDate);
        return date;
    }

    @BeforeEach
    public void setUp() {
//        Configuration.holdBrowserOpen = true; //не закрывает браузер по оканчанию теста
//        Configuration.browserSize = "800x800"; //размер открывающегося окна
        open("http://localhost:9999/");
    }

    @ParameterizedTest
    @CsvSource({
            "uppercase, Москва",
            "lower case, иркутск",
            "hyphen, Улан-удэ",
    })
    public void shouldCorrectFillingForm(String text, String name) {
        $("[data-test-id='city'] input").setValue(name);
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(date));
        $("[data-test-id='name'] input").setValue("Андрей-Петровский Иван");
        $("[data-test-id='phone'] input").setValue("+79265432654");
        $("[data-test-id='agreement']").click();
        $(".button").click();

        $(".notification__content").shouldBe(visible, Duration.ofSeconds(15)); // Duration.ofSeconds(15)-выставляем ожидание отклика на определенное время
//        $x("//*[contains(text(),'Встреча успешно забронирована на')]").should(appear, Duration.ofSeconds(15)); //выражение xpath, contains(Text(),"")-поиск по тексту
//        $(withText("Встреча успешно забронирована на")).shouldHave(visible, Duration.ofSeconds(15));
    }

    @ParameterizedTest
    @CsvSource({
            "English, Moskva",
            "spec Simbol, Москв@",
            "underscore, Улан_удэ",
            "space, Горно Алтайск",
            "not available, Братск",
            "typo, Налчик",
    })
    public void shouldTestIncorrectEnterCity(String text, String name) {
        $$x("//input[@placeholder='Город']").exclude(hidden).first().setValue(name);
//        exclude-исключает (hidden)- не видемые first()-используем первый из оставшихся
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(date));
        $("[data-test-id='name'] input").setValue("Анна-петровна");
        $("[data-test-id='phone'] input").setValue("+79305698778");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=city].input_invalid .input__sub").shouldBe(visible);
        $$x("//*[contains(text(),'Доставка в выбранный город недоступна')]").filter(visible).last().shouldBe(visible); // не работает при пустом значении- другой текст "Поле обязательно для заполнения"
//    filter- фильтруем по (visible)- видимым на странице last()-выбираем последний элемент по отфильтрованным
    }

    @Test
    public void shouldTestEnterNullCity() {
        $x("//input[@placeholder='Город']").setValue("");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(date));
        $("[data-test-id='name'] input").setValue("Анна-петровна");
        $("[data-test-id='phone'] input").setValue("+79305698778");
        $("[data-test-id=agreement]").click();
        $(".button").click();

//        $("[data-test-id=city].input_invalid .input__sub").shouldBe(visible);
        $x("//*[contains(text(),'Поле обязательно для заполнения')]").shouldBe(visible); // не работает при пустом значении- другой текст "Поле обязательно для заполнения"
    }

    @ParameterizedTest
    @CsvSource({
            "English, Maik",
            "spec Simbol, Петр Машк&вич",
            "underscore, Анна_Сергеевна",
    })
    public void shouldTestIncorrectEnterName(String text, String name) {
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(date));
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue("+79305698778");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=name].input_invalid .input__sub").shouldBe(visible);
        $x("//*[contains(text(),'Имя и Фамилия указаные неверно.')]").shouldBe(visible); // не работает при пустом значении- другой текст "Поле обязательно для заполнения"
    }

    @Test
    public void shouldTestEnterNullName() {
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(date));
        $("[data-test-id='name'] input").setValue("");
        $("[data-test-id='phone'] input").setValue("+79305698778");
        $("[data-test-id=agreement]").click();
        $(".button").click();

//        $("[data-test-id=name].input_invalid .input__sub").shouldBe(visible);
        $x("//*[contains(text(),'Поле обязательно для заполнения')]").shouldBe(visible); // не работает при пустом значении- другой текст "Поле обязательно для заполнения"
    }

    @ParameterizedTest
    @CsvSource({
            "not plus, 79025647891",
            "over limit, +798541236547",
            "less than limit, +7984562356",
            "spec simbol, +7&963256485",
            "text phone number, +7e965463269",
    })
    public void shouldTestIncorrectEnterPhone(String text, String phone) {
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(date));
        $("[data-test-id='name'] input").setValue("Аннф-Петровна");
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=phone].input_invalid .input__sub").shouldBe(visible);
        $x("//*[contains(text(),'Телефон указан неверно.')]").should(visible); // не работает при пустом значении- другой текст "Поле обязательно для заполнения"
    }

    @Test
    public void shouldTesttEnterNullPhone() {
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(date));
        $("[data-test-id='name'] input").setValue("Аннф-Петровна");
        $("[data-test-id='phone'] input").setValue("");
        $("[data-test-id=agreement]").click();
        $(".button").click();

//        $("[data-test-id=phone].input_invalid .input__sub").shouldBe(visible);
        $x("//*[contains(text(),'Поле обязательно для заполнения')]").shouldBe(visible); // не работает при пустом значении- другой текст "Поле обязательно для заполнения"
    }

    @Test
    public void shouldNotClickCheckbox() {
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(date));
        $("[data-test-id='name'] input").setValue("Андрей-Петровский Иван");
        $("[data-test-id='phone'] input").setValue("+79265432654");
        $(".button").click();

        $("[data-test-id=agreement].input_invalid .checkbox__text").shouldBe(visible);
    }

    @Test
    public void shouldIncorrectEnterDate() {
        String dateZerro = generateDate(2);
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(dateZerro));
        $("[data-test-id='name'] input").setValue("Андрей-Петровский Иван");
        $("[data-test-id='phone'] input").setValue("+79265432654");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=date] .input_invalid .input__sub").shouldBe(visible);
        $x("//*[contains(text(),'Заказ на выбранную дату невозможен')]").shouldBe(visible);
    }

    @Test
    public void shouldIncorrectEnterDateWeek() {
        String dateZerro = generateDate(7);
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(String.valueOf(dateZerro));
        $("[data-test-id='name'] input").setValue("Андрей-Петровский Иван");
        $("[data-test-id='phone'] input").setValue("+79265432654");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $(".notification__content").shouldBe(visible, Duration.ofSeconds(15)); // Duration.ofSeconds(15)-выставляем ожидание отклика на определенное время
//        $x("//*[contains(text(),'Встреча успешно забронирована на')]").should(appear, Duration.ofSeconds(15)); //выражение xpath, contains(Text(),"")-поиск по тексту
//        $(withText("Встреча успешно забронирована на")).shouldHave(visible, Duration.ofSeconds(15));
    }

//    @Test
//    public void shouldCorrectCityTwhoSymbol() {
//        $x("//input[@placeholder='Город']").setValue("Аб");
//        $$x("//*[@class='menu-item__control']").filter(visible).first(); //exclude(hidden)
//        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
//        $("[data-test-id='date'] input").setValue(String.valueOf(date));
//        $("[data-test-id='name'] input").setValue("Андрей-Петровский Иван");
//        $("[data-test-id='phone'] input").setValue("+79265432654");
//        $("[data-test-id='agreement']").click();
//        $(".button").click();
//
//        $(".notification__content").shouldBe(visible, Duration.ofSeconds(15)); // Duration.ofSeconds(15)-выставляем ожидание отклика на определенное время
//        $x("//*[contains(text(),'Встреча успешно забронирована на')]").should(appear, Duration.ofSeconds(15)); //выражение xpath, contains(Text(),"")-поиск по тексту
//        $(withText("Встреча успешно забронирована на")).shouldHave(visible, Duration.ofSeconds(15));
//    }
}
