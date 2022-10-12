package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.*;
import java.util.*;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    private static final int PAGE_COUNT = 1;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) {
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Element row = document.selectFirst(".style-ugc");
            return row.text();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Post getPost(Element row) {
        Element cardTitle = row.select(".vacancy-card__title").first();
        Element vacancyLinkElement = cardTitle.child(0);
        String vacancyName = cardTitle.text();
        String vacancyDescriptionLink = String.format("%s%s", SOURCE_LINK, vacancyLinkElement.attr("href"));
        Element cardDate = row.select(".vacancy-card__date").first();
        Element dateElement = cardDate.child(0);
        String vacancyDate = String.format("%s", dateElement.attr("datetime"));
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        return new Post(vacancyName, vacancyDescriptionLink,
                retrieveDescription(vacancyDescriptionLink),
                habrCareerDateTimeParser.parse(vacancyDate)
        );
    }

    @Override
    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        try {
            for (int i = 1; i <= PAGE_COUNT; i++) {
                String pageLink = String.format(String.format("%s%s", link, i));
                Connection connection = Jsoup.connect(pageLink);
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> postList.add(getPost(row)));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return postList;
    }

    @Override
    public String getPageLink() {
        return PAGE_LINK;
    }
}
