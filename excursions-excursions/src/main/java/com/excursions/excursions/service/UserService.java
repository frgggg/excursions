package com.excursions.excursions.service;

public interface UserService {
    void coinsUpByExcursion(Long id, Long coins);
    void coinsDownByExcursion(Long id, Long coins);
}
