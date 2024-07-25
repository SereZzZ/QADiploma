package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class OfferPage {

    private final SelenideElement heading = $(byText("Путешествие дня"));
    private final SelenideElement buttonToBuy = $$("button").find(exactText("Купить"));
    private final SelenideElement buttonToBuyOnCredit = $$("button").find(exactText("Купить в кредит"));

    public OfferPage() {
        heading.shouldBe(visible);
    }

    public void openByWithDebitCard() {
        buttonToBuy.click();
    }

    public void openByWithCreditCard() {
        buttonToBuyOnCredit.click();
    }
}