package com.anoop.examples.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotoUser {

    private String userId;
    private String userName;
    private String clientId;
    private String companyId;
    private boolean companyUser;
    private List<String> roles;
    private String manufacturerId;
}
