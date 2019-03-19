package com.o2o.action.server.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(indexes = {@Index(name = "aog_category_keycode_idx", columnList = "keycode")})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    Category parent;
    @Column(nullable = true)
    String keycode;
    Type catType;
    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL)
    private Detail detail;
    int dispOrder;
    @Column(nullable = false)
    String title;
    String description;
    String synonyms;
    String imagePath;
    String imageAltText;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private List<Category> children;

    public Category() {

    }

    public Category(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public String getKeycode() {
        return keycode;
    }

    public void setKeycode(String keycode) {
        this.keycode = keycode;
    }

    public Type getCatType() {
        return catType;
    }

    public void setCatType(Type catType) {
        this.catType = catType;
    }

    public int getDispOrder() {
        return dispOrder;
    }

    public void setDispOrder(int dispOrder) {
        this.dispOrder = dispOrder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
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

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public enum Type {
        CATEGORY, ITEM
    }
}
