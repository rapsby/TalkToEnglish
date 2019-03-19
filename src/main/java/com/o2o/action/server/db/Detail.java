package com.o2o.action.server.db;

import javax.persistence.*;

@Entity
public class Detail {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    Category category;

    Type itemType;

    @Column(nullable = false)
    String subTitle;
    String linkURL;

    public Detail() {

    }

    public enum Type {
        CUSTOMER_INFO, PRODUCT
    }
}
