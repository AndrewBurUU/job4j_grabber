package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.*;

import java.io.IOException;
import java.util.*;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    private static final int PAGE_COUNT = 5;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

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
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element row = document.selectFirst(".style-ugc");
        return row.text();
    }

    private Post parsingPost(Element element) {
        return null;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        try {
            for (int i = 1; i <= PAGE_COUNT; i++) {
                String pageLink = String.format(String.format("%s?page=%s", PAGE_LINK, i));
                System.out.println(pageLink);

                Connection connection = Jsoup.connect(pageLink);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    Element titleElement = row.select(".vacancy-card__title").first();
                    Element linkElement = titleElement.child(0);
                    String vacancyName = titleElement.text();
                    String linkHref = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                    Element titleDate = row.select(".vacancy-card__date").first();
                    Element linkDateElement = titleDate.child(0);
                    String linkDate = String.format("%s", linkDateElement.attr("datetime"));
                    HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
                    HabrCareerParse habrCareerParse = new HabrCareerParse(habrCareerDateTimeParser);
                    String elementDescription = "";
                    try {
                        elementDescription = habrCareerParse.retrieveDescription(pageLink);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("%s %s %s%n", vacancyName, linkHref, linkDate);
                    Post post = new Post(vacancyName,
                            linkHref,
                            elementDescription,
                            habrCareerDateTimeParser.parse(linkDate)
                            );
                    postList.add(post);
                });
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return postList;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(habrCareerDateTimeParser);
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
