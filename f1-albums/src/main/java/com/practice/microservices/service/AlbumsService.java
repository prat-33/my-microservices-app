package com.practice.microservices.service;

import java.util.List;

import com.practice.microservices.entity.AlbumEntity;

public interface AlbumsService {
    List<AlbumEntity> getAlbums(String userId);
}
