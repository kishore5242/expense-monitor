package com.kishore.em;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class Tester {

    public static void main(String[] args) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("yyyy/MMM/dd").toFormatter();
        System.out.println(LocalDate.parse("2022/JAN/06", formatter));
    }
}
