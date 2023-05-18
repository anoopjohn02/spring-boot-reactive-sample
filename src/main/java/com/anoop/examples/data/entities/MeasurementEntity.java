package com.anoop.examples.data.entities;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "measurements")
public class MeasurementEntity extends GenericModel{
    @Id
    private ObjectId _id;
    private String deviceId;
    private String type;
    private long value;
    private String unit;
    private Date start;
    private Date end;
    private long timeDiffInMillis;
}
