package com.tagalong.controller;

import com.tagalong.model.Request;
import com.tagalong.model.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/request")
@RequiredArgsConstructor
public class RequestController {
    private final RequestRepository requestRepository;

    @PutMapping
    @PostMapping
    public ResponseEntity<Request> create(@RequestBody Request request,@RequestHeader("Authorization") String Authorization) {

        return ResponseEntity.ok(this.requestRepository.save(request));
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllRequest(@RequestHeader("Authorization") String Authorization) {

        return ResponseEntity.ok(this.requestRepository.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequest(@PathVariable(name = "id") Long id,@RequestHeader("Authorization") String Authorization) {
        Optional<Request> request = this.requestRepository.findById(id);
        if (request.isPresent()) {
            Request request1 = request.get();
            return ResponseEntity.ok(request1);
        } else throw new RuntimeException("RECORD NOT FOUND");

    }

    @GetMapping("{email}")
    public ResponseEntity<List<Request>> getAllRequestByUsername(@PathVariable String email,@RequestHeader("Authorization") String Authorization) {

        return ResponseEntity.ok(this.requestRepository.findByEmail(email));
    }

    @GetMapping("{userId}/{driverId}")
    public ResponseEntity<List<Request>> getAllRequestByUsername(@PathVariable Long userId, @PathVariable Long driverId,@RequestHeader("Authorization") String Authorization) {

        return ResponseEntity.ok(this.requestRepository.findByUserIdAndDriverId(userId, driverId));
    }


}
