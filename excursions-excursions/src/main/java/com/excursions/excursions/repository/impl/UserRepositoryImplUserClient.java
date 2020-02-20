package com.excursions.excursions.repository.impl;

import com.excursions.excursions.client.UserClient;
import com.excursions.excursions.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryImplUserClient implements UserRepository {

    private UserClient userClient;

    @Autowired
    protected UserRepositoryImplUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public void coinsUpByExcursion(Long id, Long coins) {
        ResponseEntity<String> response;
        try {
            response = userClient.coinsUpByExcursion(id, coins);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        if(response.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new IllegalStateException(response.getBody());
        }
    }

    @Override
    public void coinsDownByExcursion(Long id, Long coins) {
        ResponseEntity<String> response;
        try {
            response = userClient.coinsDownByExcursion(id, coins);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        if(response.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new IllegalStateException(response.getBody());
        }
    }
}
