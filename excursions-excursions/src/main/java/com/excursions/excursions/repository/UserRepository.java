package com.excursions.excursions.repository;

public interface UserRepository {
    void coinsUpByExcursion(Long id, Long coins) throws IllegalStateException;
    void coinsDownByExcursion(Long id, Long coins) throws IllegalStateException;
}
