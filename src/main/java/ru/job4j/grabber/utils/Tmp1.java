package ru.job4j.grabber.utils;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.*;

public class Tmp1 {

    public static void main(String[] args) throws IOException {
        String htmlVacancyDescription = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>VacancyDescription Example</title>"
                + "</head>"
                + "<body>"
                + "<div class=\"appearance-vacancy-description\">"
                + "<div class=\"vacancy-description__text\">"
                + "<div class=\"style-ugc\">"
                + "<p></p>"
                + "<p>"
                + "<em>Text for description..."
                + "</em><br>"
                + "</p>"
                + "<p><strong>Tasks:</strong></p>"
                + "<ul><li>Support and develop...</li>"
                + "<li>Optimization...</li>"
                + "<li>Persectives ...</li>"
                + "</ul>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        Document document = Jsoup.parse(htmlVacancyDescription);
        Elements classElements = document.getElementsByClass("vacancy-description__text");
        String res = "";
        for (Element element: classElements) {
            res += element.text();
        }
        System.out.println(res);
    }
}
