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

    private void getPage(String pageLink) throws IOException {
        Connection connection = Jsoup.connect(pageLink);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            String vacancyName = titleElement.text();
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            Element titleDate = row.select(".vacancy-card__date").first();
            Element linkDateElement = titleDate.child(0);
            String linkDate = String.format("%s", linkDateElement.attr("datetime"));
            System.out.printf("%s %s %s%n", vacancyName, link, linkDate);
        });
    }

    private String retrieveDescription(String link) throws IOException {
        String res = "";
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements vacancyDescription = document.getElementsByClass("vacancy-description__text");
        for (Element element: vacancyDescription) {
            res += element.text();
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse();
        System.out.println(habrCareerParse.retrieveDescription("https://career.habr.com/vacancies/1000102160"));
        /**
        for (int i = 1; i <= 5; i++) {
            String pageLink = String.format(String.format("%s?page=%s", PAGE_LINK, i));
            System.out.println(pageLink);
            habrCareerParse.getPage(pageLink);
            System.out.println();
        }
         */
    }
}
