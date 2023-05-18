package com.anoop.examples.data.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GenericModel {
    private Date createdDate;
    private Date modifiedDate;

    public GenericModel() {
        createdDate = new Date();
        modifiedDate = new Date();
    }

}
