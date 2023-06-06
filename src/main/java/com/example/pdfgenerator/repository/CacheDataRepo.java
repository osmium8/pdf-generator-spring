package com.example.pdfgenerator.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.pdfgenerator.entity.CacheData;


@Repository
public interface CacheDataRepo extends CrudRepository<CacheData, String> {
    
}
