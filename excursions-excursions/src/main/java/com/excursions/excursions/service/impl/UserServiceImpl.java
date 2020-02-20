package com.excursions.excursions.service.impl;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.repository.UserRepository;
import com.excursions.excursions.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.excursions.excursions.log.message.UserServiceLogMessages.USER_SERVICE_LOG_DOWN_BY_EXCURSION;
import static com.excursions.excursions.log.message.UserServiceLogMessages.USER_SERVICE_LOG_UP_BY_EXCURSION;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private String SERVICE_NAME = "UserServiceImpl";

    private UserRepository userRepository;

    @Autowired
    protected UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void coinsDownByExcursion(Long id, Long coins) {
        try {
            userRepository.coinsDownByExcursion(id, coins);
        } catch (IllegalStateException e) {
            throw new ServiceException(SERVICE_NAME, e.getMessage());
        }

        log.info(SERVICE_NAME, USER_SERVICE_LOG_DOWN_BY_EXCURSION);
    }

    @Override
    public void coinsUpByExcursion(Long id, Long coins) {
        try {
            userRepository.coinsUpByExcursion(id, coins);
        } catch (IllegalStateException e) {
            throw new ServiceException(SERVICE_NAME, e.getMessage());
        }

        log.info(SERVICE_NAME, USER_SERVICE_LOG_UP_BY_EXCURSION);
    }
}
