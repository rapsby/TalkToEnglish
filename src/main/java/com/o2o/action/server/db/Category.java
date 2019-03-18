package com.o2o.action.server.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(indexes = {@Index(name = "aog_category_name_idx", columnList = "name")})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    Category parent;
    @Column(nullable = false)
    String name;
    String synonyms;
    String imagePath;
    String imageAltText;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private List<Category> children;

    public Category() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
