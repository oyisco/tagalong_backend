package com.tagalong.controller;

import com.tagalong.dto.PhotoRequestDTO;
import com.tagalong.model.Photo;
import com.tagalong.model.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/upload-image")
@RequiredArgsConstructor
public class UploadImageController {
    private final PhotoRepository photoRepository;

    @PostMapping
    public ResponseEntity<Photo> saveImage(@Valid @RequestBody PhotoRequestDTO photo) {
        Photo photo1 = new Photo();
        photo1.setImage(photo.getImage());
        photo1.setEmail(photo.getEmail());
        return ResponseEntity.ok( this.photoRepository.save(photo1));
    }


    @PutMapping
    public ResponseEntity<PhotoRequestDTO> updateImage(@Valid @RequestBody PhotoRequestDTO photo) {
        Optional<Photo> photo1 = this.photoRepository.findById(photo.getEmail());
        if (photo1.isPresent()) {
            Photo photo2 = photo1.get();
            return ResponseEntity.ok(convertEntityToDto(this.photoRepository.save(photo2)));
        } else throw new RuntimeException("RECORD CAN NOT BE UPDATED OR EMAIL ID DOES NOT EXIST");


    }


    public Photo convertDtoToEntity(PhotoRequestDTO photoRequestDTO) {
        Photo photo = new Photo();
        BeanUtils.copyProperties(photoRequestDTO, photo);
        return photo;
    }

    public PhotoRequestDTO convertEntityToDto(Photo photo) {
        PhotoRequestDTO photoRequestDTO = new PhotoRequestDTO();
        BeanUtils.copyProperties(photo, photoRequestDTO);
        return photoRequestDTO;
    }

    @GetMapping
    public ResponseEntity<List<Photo>> getAllPhoto() {
        return ResponseEntity.ok(this.photoRepository.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Photo> getPhoto(@PathVariable(name = "id") String email) {
        Optional<Photo> photo1 = this.photoRepository.findById(email);
        if (photo1.isPresent()) {
            Photo photo2 = photo1.get();
            return ResponseEntity.ok(photo2);
        } else throw new RuntimeException("RECORD CAN NOT BE UPDATED OR EMAIL ID DOES NOT EXIST");

    }

}
