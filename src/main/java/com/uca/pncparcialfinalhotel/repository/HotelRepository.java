package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
