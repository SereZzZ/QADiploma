package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.page.CreditCard;
import ru.netology.page.OfferPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataGenerator.*;
import static ru.netology.data.DataHelper.getCreditStatus;

public class OfferPageCreditTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void setup() {
        open("http://localhost:8080");
    }

    @AfterEach
    void teardown() {
        DataHelper.cleanTables();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    public void shouldAcceptPurchaseWithApprovedCard() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
        buy.waitForNotificationOK();
        assertEquals("APPROVED", getCreditStatus());
    }

    @Test
    public void shouldDenyPurchaseWithDeclinedCard() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getDeclinedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
        buy.waitForNotificationERROR();
        assertEquals("DECLINED", getCreditStatus());
    }

    @Test
    public void shouldBeErrorsAfterSendingEmptyRequest() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(null, null, null, null, null);
        buy.getCardNumberError("Неверный формат");
        buy.getMonthError("Неверный формат");
        buy.getYearError("Неверный формат");
        buy.getCardholderError("Поле обязательно для заполнения");
        buy.getCVCError("Неверный формат");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithoutCardNumber() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(null, getMonth(), getYear(), getNameOfCardholder(), getCVC());
        buy.findMonthError();
        buy.findYearError();
        buy.findCardHolderError();
        buy.findCVCError();
        buy.getCardNumberError("Неверный ключ");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithoutMonth() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), null, getYear(), getNameOfCardholder(), getCVC());
        buy.findCardNumberError();
        buy.findYearError();
        buy.findCardHolderError();
        buy.findCVCError();
        buy.getMonthError("Неверный формат");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithoutYear() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), null, getNameOfCardholder(), getCVC());
        buy.findCardNumberError();
        buy.findMonthError();
        buy.findCardHolderError();
        buy.findCVCError();
        buy.getYearError("Неверный формат");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithoutCardHolder() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), null, getCVC());
        buy.findCardNumberError();
        buy.findMonthError();
        buy.findYearError();
        buy.findCVCError();
        buy.getCardholderError("Поле обязательно для заполнения");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithoutCVC() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), null);
        buy.findCardNumberError();
        buy.findMonthError();
        buy.findYearError();
        buy.findCardHolderError();
        buy.getCVCError("Неверный формат");
    }

    @Test
    public void shouldNotAcceptLettersInCardNumberField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFields(getNameOfCardholder(), null, null, null, null);
        assertEquals("", buy.getValueFromCardNumber());
    }

    @Test
    public void shouldNotAcceptSymbolsInCardNumberField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFields(getTextOfSymbols(), null, null, null, null);
        assertEquals("", buy.getValueFromCardNumber());
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithShortCardNumber() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getShortCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
        buy.findMonthError();
        buy.findYearError();
        buy.findCardHolderError();
        buy.findCVCError();
        buy.getCardNumberError("Неверный формат");
    }

    @Test
    public void shouldFillFieldCardNumberMax16Digits() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFields(getLongCardNumber(), null, null, null, null);
        assertEquals("4444 4444 4444 4441", buy.getValueFromCardNumber());
    }

    @Test
    public void shouldBeErrorNotificationAfterSendingRequestWithRandomCardNumber() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getRandomCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
        buy.waitForNotificationERROR();
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithInvalidCardNumber() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getInvalidCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
        buy.findMonthError();
        buy.findYearError();
        buy.findCardHolderError();
        buy.findCVCError();
        buy.getCardNumberError("Неверный формат");
    }

    @Test
    public void shouldNotAcceptLettersInMonthField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFields(null, getNameOfCardholder(), null, null, null);
        assertEquals("", buy.getValueFromMonth());
    }

    @Test
    public void shouldNotAcceptSymbolsInMonthField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFields(null, getTextOfSymbols(), null, null, null);
        assertEquals("", buy.getValueFromMonth());
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithInvalidMothBelow() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getInvalidMonthBelow(), getYear(), getNameOfCardholder(), getCVC());
        buy.getMonthError("Неверно указан срок действия карты");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithInvalidMothAbove() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getInvalidMonthAbove(), getYear(), getNameOfCardholder(), getCVC());
        buy.getMonthError("Неверно указан срок действия карты");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithInvalidMothOf1Digit() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getInvalidMonthOf1Symbol(), getYear(), getNameOfCardholder(), getCVC());
        buy.getMonthError("Неверный формат");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithPreviousDate() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonthFromPreviousDate(), getYearFromPreviousDate(), getNameOfCardholder(), getCVC());
        buy.getMonthError("Неверно указан срок действия карты");
    }

    @Test
    public void shouldNotAcceptLettersInYearField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFields(null, null, getNameOfCardholder(), null, null);
        assertEquals("", buy.getValueFromYear());
    }

    @Test
    public void shouldNotAcceptSymbolsInYearField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFields(null, null, getTextOfSymbols(), null, null);
        assertEquals("", buy.getValueFromYear());
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithPreviousYear() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getPreviousYear(), getNameOfCardholder(), getCVC());
        buy.getYearError("Истёк срок действия карты");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithNextYear() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getNextYear(), getNameOfCardholder(), getCVC());
        buy.getYearError("Неверно указан срок действия карты");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithDigitsInCardholderField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getCVC(), getCVC());
        buy.getCardholderError("Поле должно содержать латинские буквы, допустимы дефис и пробел");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithSymbolsInCardholderField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getTextOfSymbols(), getCVC());
        buy.getCardholderError("Поле должно содержать латинские буквы, допустимы дефис и пробел");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithTextInRussianInCardholderField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getTextInRussian(), getCVC());
        buy.getCardholderError("Поле должно содержать латинские буквы, допустимы дефис и пробел");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithShortNameInCardholderField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getShortName(), getCVC());
        buy.getCardholderError("Введите данные в диапазоне от 4 до 60 символов");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithLongNameInCardholderField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getLongName(), getCVC());
        buy.getCardholderError("Введите данные в диапазоне от 4 до 60 символов");
    }

    @Test
    public void shouldNotAcceptLettersInCVCField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFields(null, null, null, null, getNameOfCardholder());
        assertEquals("", buy.getValueFromCVC());
    }

    @Test
    public void shouldNotAcceptSymbolsInCVCField() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFields(null, null, null, null, getTextOfSymbols());
        assertEquals("", buy.getValueFromCVC());
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithCVCOf1Digit() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVCOf1Digit());
        buy.getCVCError("Неверный формат");
    }

    @Test
    public void shouldBeErrorAfterSendingRequestWithCVCOf2Digits() {
        OfferPage offerPage = new OfferPage();
        offerPage.openByWithCreditCard();
        var buy = new CreditCard();
        buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVCOf2Digits());
        buy.getCVCError("Неверный формат");
    }
}