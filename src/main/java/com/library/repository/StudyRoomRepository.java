package com.library.repository;

import com.library.model.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    List<StudyRoom> findByCapacityGreaterThanEqual(int capacity);

    @Query("""
    SELECT r FROM StudyRoom r
    WHERE r.capacity >= :groupSize AND r.id NOT IN (
        SELECT res.studyRoom.id FROM Reservation res
        WHERE res.startDateTime < :end AND res.endDateTime > :start
    )
""")
    List<StudyRoom> findAvailableRooms(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("groupSize") int groupSize);

}
