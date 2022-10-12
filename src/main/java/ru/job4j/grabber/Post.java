package ru.job4j.grabber;

import java.time.*;
import java.util.*;

/**
 *  - id типа int - идентификатор вакансии (берется из нашей базы данных);
 *  - title типа String - название вакансии;
 *  - link типа String - ссылка на описание вакансии;
 *  - description типа String - описание вакансии;
 *  - created типа LocalDateTime - дата создания вакансии.
 */
public class Post {

    private int id;

    private String name;

    private String link;

    private String description;

    private LocalDateTime created;

    public Post() {
    }

    public Post(String name, String link, String description, LocalDateTime created) {
        this.name = name;
        if (link.length() > 255) {
            this.link = link.substring(0, 255);
        } else {
            this.link = link;
        }
        if (description.length() > 255) {
            this.description = description.substring(0, 255);
        } else {
            this.description = description;
        }
        this.created = created;
    }

    public Post(int id, String name, String link, String description, LocalDateTime created) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.description = description;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id == post.id && Objects.equals(link, post.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, link);
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", link='" + link + '\''
                + ", description='" + description + '\''
                + ", created=" + created
                + '}';
    }
}
