package ru.job4j.grabber;

import ru.job4j.grabber.utils.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        try (Connection cn = DriverManager.getConnection(
                cfg.getProperty("url"),
                cfg.getProperty("username"),
                cfg.getProperty("password"))
        ) {
            cnn = cn;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement(
                "insert into post(name, text, link, created) values(?, ?, ?, ?)"
                        + " on conflict (link) do nothing")) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement ps = cnn.prepareStatement(
                "select * from post")) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(getPostDB(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement ps = cnn.prepareStatement(
                "select * from post where id = ?")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    post = getPostDB(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    private Post getPostDB(ResultSet resultSet) throws Exception {
        Post post = new Post();
        post.setId(resultSet.getInt("id"));
        post.setName(resultSet.getString("name"));
        post.setDescription(resultSet.getString("text"));
        post.setLink(resultSet.getString("link"));
        post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("post.properties")) {
            Properties config = new Properties();
            config.load(in);
            PsqlStore psqlStore = new PsqlStore(config);
            HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
            psqlStore.save(new Post(
                    "Team lead Android-разработки (VK Teams)",
                    "https://career.habr.com/vacancies/1000105222",
                    "Разработчик мобильных приложений, Ведущий (Lead)",
                    habrCareerDateTimeParser.parse("2022-10-11T17:50:44+03:00")
            ));
            psqlStore.save(new Post(
                    "Старший Java-разработчик",
                    "https://career.habr.com/vacancies/1000105220",
                    "Бэкенд разработчик, Старший (Senior)",
                    habrCareerDateTimeParser.parse("2022-10-11T17:50:18+03:00")
            ));
            psqlStore.getAll().forEach(post -> System.out.println(post));
            System.out.println(psqlStore.findById(1));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
