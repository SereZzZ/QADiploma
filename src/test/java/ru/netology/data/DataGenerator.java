package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataGenerator {

    private DataGenerator() {
    }

    @Value
    public static class CardInfo {
        private String number;
        private String month;
        private String year;
        private String cardholder;
        private String cvc;
    }

    public static String getApprovedCardNumber() {
        return "4444 4444 4444 4441";
    }

    public static String getDeclinedCardNumber() {
        return "4444 4444 4444 4442";
    }

    public static String getLongCardNumber() {
        return "4444 4444 4444 4441 1";
    }

    public static String getShortCardNumber() {
        Faker faker = new Faker();
        return faker.number().digits(15);
    }

    public static String getRandomCardNumber() {
        Faker faker = new Faker();
        return faker.number().digits(16);
    }

    public static String getInvalidCardNumber() {
        return "0000 0000 0000 0000";
    }

    public static String getMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getInvalidMonthBelow() {
        return "00";
    }

    public static String getInvalidMonthAbove() {
        return "13";
    }

    public static String getInvalidMonthOf1Symbol() {
        Faker faker = new Faker();
        return faker.number().digits(1);
    }

    public static String getYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String getPreviousYear() {
        int year = Integer.parseInt(getYear());
        int previousYear = year - 1;
        return String.valueOf(previousYear);
    }

    public static String getNextYear() {
        int year = Integer.parseInt(getYear());
        int nextYear = year + 6;
        return String.valueOf(nextYear);
    }

    public static LocalDate getPreviousDate() {
        LocalDate localDate = LocalDate.now();
        return localDate.minusMonths(1);
    }

    public static String getMonthFromPreviousDate() {
        return getPreviousDate().format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getYearFromPreviousDate() {
        return getPreviousDate().format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String getTextOfSymbols() {
        return "!@#$%^";
    }

    public static String getTextInRussian() {
        Faker faker = new Faker(new Locale("RU"));
        return faker.name().lastName();
    }

    public static String getNameOfCardholder() {
        Faker faker = new Faker(new Locale("EN"));
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    public static String getShortName() {
        return "Mrs";
    }

    public static String getLongName() {
        return "SerezSerezSerezSerezSerezSerezSerezSerezSerezSerezSerez";
    }

    public static String getCVC() {
        Faker faker = new Faker();
        return faker.number().digits(3);
    }

    public static String getCVCOf1Digit() {
        Faker faker = new Faker();
        return faker.number().digits(1);
    }

    public static String getCVCOf2Digits() {
        Faker faker = new Faker();
        return faker.number().digits(2);
    }
}