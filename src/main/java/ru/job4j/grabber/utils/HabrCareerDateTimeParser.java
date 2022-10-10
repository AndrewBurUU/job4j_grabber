package ru.job4j.grabber.utils;

import java.time.*;
import java.time.format.*;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        return LocalDateTime.parse(parse.substring(0, 19));
    }

}
