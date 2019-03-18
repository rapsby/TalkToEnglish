package com.o2o.action.server.db;

import javax.persistence.*;

@Entity
public class AoGItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    Category category;

    @Column(nullable = false)
    String name;
    String title;
    String subTitle;
    String description;
    String linkURL;
    String imagePath;
    String imageAltText;

    public AoGItem() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageAltText() {
        return imageAltText;
    }

    public void setImageAltText(String imageAltText) {
        this.imageAltText = imageAltText;
    }

    public enum Type {
        CUSTOMER_SERVICE, PRODUCT
    }
}
