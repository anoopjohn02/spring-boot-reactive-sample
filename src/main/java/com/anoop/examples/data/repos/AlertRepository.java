package com.anoop.examples.data.repos;


import com.anoop.examples.data.entities.AlertEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends MongoRepository<AlertEntity, ObjectId> {
    List<AlertEntity> findByDeviceId(String deviceId);
}
