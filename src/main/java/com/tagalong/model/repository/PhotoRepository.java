package com.tagalong.model.repository;

import com.tagalong.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, String> {
//	Page<Photo> findByAlbumId(Long albumId, Pageable pageable);
}
