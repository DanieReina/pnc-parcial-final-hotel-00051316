package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Trae solo las reservas de un huésped en específico
    List<Reservation> findByGuestId(Long guestId);

    // Trae las reservas que pertenecen a cualquier habitación de un hotel (sucursal) en específico
    List<Reservation> findByRoomHotelId(Long hotelId);
}