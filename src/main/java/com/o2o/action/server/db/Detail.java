package com.o2o.action.server.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Detail {
    @OneToOne
    @MapsId
    @JsonIgnore
    Category category;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Type itemType;
    String subTitle;
    String linkURL;
    @Column()
    long cost = 0;
    String relatedKeyword;
    @Id
    private Long id;

    public Detail() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Type getItemType() {
        return itemType;
    }

    public void setItemType(Type itemType) {
        this.itemType = itemType;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public enum Type {
        CUSTOMER_INFO, PRODUCT
    }
}
