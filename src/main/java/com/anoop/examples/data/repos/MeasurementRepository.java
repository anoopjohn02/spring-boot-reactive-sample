package com.anoop.examples.data.repos;


import com.anoop.examples.data.entities.MeasurementEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeasurementRepository extends MongoRepository<MeasurementEntity, ObjectId> {
    List<MeasurementEntity> findByDeviceId(String deviceId);
}
