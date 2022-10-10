package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.*;

import java.io.IOException;
import java.nio.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        Connection connection = Jsoup.connect(PAGE_LINK);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            String vacancyNameBadCoding = titleElement.text();
            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(vacancyNameBadCoding);
            String vacancyName = new String(byteBuffer.array(), StandardCharsets.UTF_8);
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            Element titleDate = row.select(".vacancy-card__date").first();
            Element linkDateElement = titleDate.child(0);
            String linkDate = String.format("%s", linkDateElement.attr("datetime"));
            System.out.printf("%s %s %s%n", vacancyName, link, linkDate);
        });
    }
}
