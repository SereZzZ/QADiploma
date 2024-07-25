package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import org.junit.jupiter.api.Test;
import ru.netology.page.DebitCard;
import ru.netology.page.OfferPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.getPaymentStatus;
import static ru.netology.data.DataGenerator.*;

 public class OfferPageDebitTest {

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
     public void shouldAcceptPurchaseWithApprovedCard() {   // покупка с потвержденной карты
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
         buy.waitForNotificationOK();
         assertEquals("APPROVED", getPaymentStatus());
     }

     @Test
     public void shouldDenyPurchaseWithDeclinedCard() {
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getDeclinedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
         buy.waitForNotificationERROR();
         assertEquals("DECLINED", getPaymentStatus());
     }

     @Test
     public void shouldBeErrorsAfterSendingEmptyRequest() {   //отправка пустого запроса
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(null, null, null, null, null);
         buy.getCardNumberError("Неверный формат");
         buy.getMonthError("Неверный формат");
         buy.getYearError("Неверный формат");
         buy.getCardholderError("Поле обязательно для заполнения");
         buy.getCVCError("Неверный формат");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithoutCardNumber() { // отправка без номера карты
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(null, getMonth(), getYear(), getNameOfCardholder(), getCVC());
         buy.MonthError();
         buy.YearError();
         buy.CardHolderError();
         buy.CVCError();
         buy.getCardNumberError("Неверный формат");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithoutYear() { // должна быть ошибка отправки
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), null, getNameOfCardholder(), getCVC());
         buy.CardNumberError();
         buy.MonthError();
         buy.CardHolderError();
         buy.CVCError();
         buy.getYearError("Неверный формат");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithoutCardHolder() { // отправка без имени владельца
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), null, getCVC());
         buy.CardNumberError();
         buy.MonthError();
         buy.YearError();
         buy.CVCError();
         buy.getCardholderError("Поле обязательно для заполнения");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithoutCVC() { // отправка запроса без CVC
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), null);
         buy.CardNumberError();
         buy.MonthError();
         buy.YearError();
         buy.CardHolderError();
         buy.getCVCError("Неверный формат");
     }

     @Test
     public void shouldNotAcceptLettersInCardNumberField() { // не принимать в поле ввода к.
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.FillFields(getNameOfCardholder(), null, null, null, null);
         assertEquals("", buy.getValueFromCardNumber());
     }

     @Test
     public void shouldNotAcceptSymbolsInCardNumberField() { //не принимать символы
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.FillFields(getTextOfSymbols(), null, null, null, null);
         assertEquals("", buy.getValueFromCardNumber());
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithShortCardNumber() { //отправка запроса с коротким № карты
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getShortCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
         buy.MonthError();
         buy.YearError();
         buy.CardHolderError();
         buy.CVCError();
         buy.getCardNumberError("Неверный формат");
     }

     @Test
     public void shouldFillFieldCardNumberMax16Digits() { // максимальное количество символов в карте
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.FillFields(getLongCardNumber(), null, null, null, null);
         assertEquals("4444 4444 4444 4441", buy.getValueFromCardNumber());
     }

     @Test
     public void shouldBeErrorNotificationAfterSendingRequestWithRandomCardNumber() {
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getRandomCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
         buy.waitForNotificationERROR();
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithInvalidCardNumber() { //отправка запроса с случайным № карты
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getInvalidCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVC());
         buy.MonthError();
         buy.YearError();
         buy.CardHolderError();
         buy.CVCError();
         buy.getCardNumberError("Неверный формат");
     }

     @Test
     public void shouldNotAcceptLettersInMonthField() { //не должно принимать
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.FillFields(null, getNameOfCardholder(), null, null, null);
         assertEquals("", buy.getValueFromMonth());
     }

     @Test
     public void shouldNotAcceptSymbolsInMonthField() { // не принимает символы в месячном поле
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.FillFields(null, getTextOfSymbols(), null, null, null);
         assertEquals("", buy.getValueFromMonth());
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithInvalidMothBelow() { //должна появиться ошибка о неверном сроке действия карты
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getInvalidMonthBelow(), getYear(), getNameOfCardholder(), getCVC());
         buy.getMonthError("Неверно указан срок действия карты");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithInvalidMothAbove() { //Отправки Запроса с Указанным Выше Значением
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getInvalidMonthAbove(), getYear(), getNameOfCardholder(), getCVC());
         buy.getMonthError("Неверно указан срок действия карты");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithInvalidMothOf1Digit() { //Должно выдать ошибку неверный формат
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getInvalidMonthOf1Symbol(), getYear(), getNameOfCardholder(), getCVC());
         buy.getMonthError("Неверный формат");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithPreviousDate() { //Отправки запроса с предыдущей датой
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonthFromPreviousDate(), getYearFromPreviousDate(), getNameOfCardholder(), getCVC());
         buy.getMonthError("Неверно указан срок действия карты");
     }

     @Test
     public void shouldNotAcceptLettersInYearField() { //не должно принимать будущие з.
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.FillFields(null, null, getNameOfCardholder(), null, null);
         assertEquals("", buy.getValueFromYear());
     }

     @Test
     public void shouldNotAcceptSymbolsInYearField() { //не принимать символы в поле год
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.FillFields(null, null, getTextOfSymbols(), null, null);
         assertEquals("", buy.getValueFromYear());
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithPreviousYear() { //должна быть ошибка после отправки запроса с предыдущим годом
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getPreviousYear(), getNameOfCardholder(), getCVC());
         buy.getYearError("Истёк срок действия карты");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithNextYear() {
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getNextYear(), getNameOfCardholder(), getCVC());
         buy.getYearError("Неверно указан срок действия карты");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithDigitsInCardholderField() {
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getCVC(), getCVC());
         buy.getCardholderError("Поле должно содержать латинские буквы, допустимы дефис и пробел");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithSymbolsInCardholderField() {
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getTextOfSymbols(), getCVC());
         buy.getCardholderError("Поле должно содержать латинские буквы, допустимы дефис и пробел");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithTextInRussianInCardholderField() {
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getTextInRussian(), getCVC());
         buy.getCardholderError("Поле должно содержать латинские буквы, допустимы дефис и пробел");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithShortNameInCardholderField() {
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getShortName(), getCVC());
         buy.getCardholderError("Введите данные в диапазоне от 4 до 60 символов");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithLongNameInCardholderField() { //Должен быть запрос на отправку запроса с длинным именем в поле "Владелец карточки
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getLongName(), getCVC());
         buy.getCardholderError("Введите данные в диапазоне от 4 до 60 символов");
     }

     @Test
     public void shouldNotAcceptLettersInCVCField() {  //не принимать в поле CVC
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.FillFields(null, null, null, null, getNameOfCardholder());
         assertEquals("", buy.getValueFromCVC());
     }

     @Test
     public void shouldNotAcceptSymbolsInCVCField() { //Не следует принимать символы в поле ввода
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.FillFields(null, null, null, null, getTextOfSymbols());
         assertEquals("", buy.getValueFromCVC());
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithCVCOf1Digit() {
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVCOf1Digit());
         buy.getCVCError("Неверный формат");
     }

     @Test
     public void shouldBeErrorAfterSendingRequestWithCVCOf2Digits() { //должна возникнуть Ошибка После отправки Запроса С CVC ...
         OfferPage offerPage = new OfferPage();
         offerPage.openByWithDebitCard();
         var buy = new DebitCard();
         buy.shouldFillFieldsAndSendRequest(getApprovedCardNumber(), getMonth(), getYear(), getNameOfCardholder(), getCVCOf2Digits());
         buy.getCVCError("Неверный формат");
     }
 }

