package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.RoomRequestDTO;
import com.uca.pncparcialfinalhotel.dto.response.RoomResponseDTO;
import com.uca.pncparcialfinalhotel.entity.Hotel;
import com.uca.pncparcialfinalhotel.entity.Room;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.HotelRepository;
import com.uca.pncparcialfinalhotel.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    @Transactional(readOnly = true)
    public List<RoomResponseDTO> listarHabitaciones(Long hotelId, Boolean available) {
        return roomRepository.findRoomsByFilters(hotelId, available).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomResponseDTO obtenerHabitacionPorId(Long id) {
        return mapearADTO(roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada")));
    }

    @Transactional
    public RoomResponseDTO crearHabitacion(RoomRequestDTO request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("El Hotel especificado no existe"));

        Room room = new Room();
        room.setHotel(hotel);
        room.setType(request.getType());
        room.setPrice(request.getPrice());
        room.setIsAvailable(request.getIsAvailable());

        return mapearADTO(roomRepository.save(room));
    }

    @Transactional
    public RoomResponseDTO actualizarHabitacion(Long id, RoomRequestDTO request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("El Hotel especificado no existe"));

        room.setHotel(hotel);
        room.setType(request.getType());
        room.setPrice(request.getPrice());
        room.setIsAvailable(request.getIsAvailable());

        return mapearADTO(roomRepository.save(room));
    }

    @Transactional
    public void eliminarHabitacion(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Habitación no encontrada");
        }
        roomRepository.deleteById(id);
    }

    // Método utilitario (Mapper interno)
    private RoomResponseDTO mapearADTO(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(room.getId());
        dto.setHotelId(room.getHotel().getId());
        dto.setHotelName(room.getHotel().getName());
        dto.setType(room.getType());
        dto.setPrice(room.getPrice());
        dto.setIsAvailable(room.getIsAvailable());
        return dto;
    }
}