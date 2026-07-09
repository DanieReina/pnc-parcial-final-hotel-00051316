package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE " +
            "(:hotelId IS NULL OR r.hotel.id = :hotelId) AND " +
            "(:isAvailable IS NULL OR r.isAvailable = :isAvailable)")
    List<Room> findRoomsByFilters(@Param("hotelId") Long hotelId, @Param("isAvailable") Boolean isAvailable);
}
