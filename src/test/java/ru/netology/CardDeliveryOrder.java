package ru.netology;


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
    private final String date = generateDate(3);
    private final String message = "Встреча успешно забронирована на";
    private final String errorDate = "Заказ на выбранную дату невозможен";

    public String generateDate (int shift){
        String date;
        LocalDate localDate = LocalDate.now().plusDays(shift);
        date = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(localDate);
        return date;
    }

    @BeforeEach
    public void setUp() {
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
        $("[data-test-id='date'] input").setValue(this.date);
        $("[data-test-id='name'] input").setValue("Андрей-Петровский Иван");
        $("[data-test-id='phone'] input").setValue("+79265432654");
        $("[data-test-id='agreement']").click();
        $(".button").click();

        $(withText(this.message)).shouldHave(visible, Duration.ofSeconds(15)).shouldBe(text(this.date));
    }

    @ParameterizedTest
    @CsvSource({
            "English, Moskva, Доставка в выбранный город недоступна",
            "spec Simbol, Москв@, Доставка в выбранный город недоступна",
            "underscore, Улан_удэ, Доставка в выбранный город недоступна",
            "space, Горно Алтайск, Доставка в выбранный город недоступна",
            "not available, Братск, Доставка в выбранный город недоступна",
            "typo, Налчик, Доставка в выбранный город недоступна",
            "null,'', Поле обязательно для заполнения",
    })
    public void shouldTestIncorrectEnterCity(String text, String name, String error) {
        $$x("//input[@placeholder='Город']").exclude(hidden).first().setValue(name);
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(this.date);
        $("[data-test-id='name'] input").setValue("Анна-петровна");
        $("[data-test-id='phone'] input").setValue("+79305698778");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=city].input_invalid .input__sub").shouldBe(visible).should(text(error));
    }

    @ParameterizedTest
    @CsvSource({
            "English, Maik, Имя и Фамилия указаные неверно.",
            "spec Simbol, Петр Машк&вич, Имя и Фамилия указаные неверно.",
            "underscore, Анна_Сергеевна, Имя и Фамилия указаные неверно.",
            "null,'', Поле обязательно для заполнения",
    })
    public void shouldTestIncorrectEnterName(String text, String name, String error) {
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(this.date);
        $("[data-test-id='name'] input").setValue(name);
        $("[data-test-id='phone'] input").setValue("+79305698778");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=name].input_invalid .input__sub").shouldBe(visible).shouldHave(text(error));
      }

    @ParameterizedTest
    @CsvSource({
            "not plus, 79025647891, Телефон указан неверно.",
            "over limit, +798541236547, Телефон указан неверно.",
            "less than limit, +7984562356, Телефон указан неверно.",
            "spec simbol, +7&963256485, Телефон указан неверно.",
            "text phone number, +7e965463269, Телефон указан неверно.",
            "null,'',Поле обязательно для заполнения",
    })
    public void shouldTestIncorrectEnterPhone(String text, String phone, String error) {
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(this.date);
        $("[data-test-id='name'] input").setValue("Аннф-Петровна");
        $("[data-test-id='phone'] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=phone].input_invalid .input__sub").shouldBe(visible).shouldHave(text(error));
    }

    @Test
    public void shouldNotClickCheckbox() {
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(this.date);
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
        $("[data-test-id='date'] input").setValue(dateZerro);
        $("[data-test-id='name'] input").setValue("Андрей-Петровский Иван");
        $("[data-test-id='phone'] input").setValue("+79265432654");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $("[data-test-id=date] .input_invalid .input__sub").shouldBe(visible).shouldHave(text(this.errorDate));
    }

    @Test
    public void shouldIncorrectEnterDateWeek() {
        String dateWeek = generateDate(7);
        $("[data-test-id='city'] input").setValue("Иркутск");
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(dateWeek);
        $("[data-test-id='name'] input").setValue("Андрей-Петровский Иван");
        $("[data-test-id='phone'] input").setValue("+79265432654");
        $("[data-test-id=agreement]").click();
        $(".button").click();

        $(withText(this.message)).shouldHave(visible, Duration.ofSeconds(15)).shouldHave(text(dateWeek));
    }

    @Test
    public void shouldCorrectCityTwhoSymbol() {
        $x("//input[@placeholder='Город']").setValue("Аб").sendKeys(Keys.DOWN,Keys.ENTER);
        $("[data-test-id='date'] input").sendKeys( Keys.CONTROL +"A",Keys.DELETE);
        $("[data-test-id='date'] input").setValue(this.date);
        $("[data-test-id='name'] input").setValue("Андрей-Петровский Иван");
        $("[data-test-id='phone'] input").setValue("+79265432654");
        $("[data-test-id='agreement']").click();
        $(".button").click();

        $(withText(this.message)).shouldHave(visible, Duration.ofSeconds(15)).shouldHave(text(this.date));

    }
}
