package com.saysimple.catalogs.service;

import com.saysimple.catalogs.jpa.CatalogEntity;

public interface CatalogService {
    Iterable<CatalogEntity> getAllCatalogs();
}
