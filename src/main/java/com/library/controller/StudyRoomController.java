package com.library.controller;

import com.library.dto.StudyRoomDto;
import com.library.model.StudyRoom;
import com.library.repository.StudyRoomRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/studyrooms")
public class StudyRoomController {

    @Autowired
    private StudyRoomRepository studyRoomRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public Page<StudyRoomDto> getAllRooms(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studyRoomRepository.findAll(pageable).map(this::convertToDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<StudyRoomDto> createRoom(@RequestBody StudyRoom room) {
        return new ResponseEntity<>(convertToDto(studyRoomRepository.save(room)), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<StudyRoomDto> getRoom(@PathVariable Long id) {
        return studyRoomRepository.findById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudyRoomDto> updateRoom(@PathVariable Long id, @RequestBody StudyRoom updated) {
        StudyRoom room = studyRoomRepository.findById(id).orElseThrow();
        room.setCapacity(updated.getCapacity());
        return ResponseEntity.ok(convertToDto(studyRoomRepository.save(room)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        studyRoomRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private StudyRoomDto convertToDto(StudyRoom room) {
        StudyRoomDto dto = new StudyRoomDto();
        BeanUtils.copyProperties(room, dto);
        return dto;
    }
}