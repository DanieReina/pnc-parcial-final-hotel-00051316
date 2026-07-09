package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.ReservationRequestDTO;
import com.uca.pncparcialfinalhotel.dto.response.ReservationResponseDTO;
import com.uca.pncparcialfinalhotel.entity.Reservation;
import com.uca.pncparcialfinalhotel.entity.Room;
import com.uca.pncparcialfinalhotel.entity.User;
import com.uca.pncparcialfinalhotel.exception.BusinessRuleException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.ReservationRepository;
import com.uca.pncparcialfinalhotel.repository.RoomRepository;
import com.uca.pncparcialfinalhotel.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> obtenerReservasPorUsuario(String username) {
        User usuarioActivo = obtenerUsuarioActivo(username);
        String rol = usuarioActivo.getRole().getName();

        List<Reservation> reservas;

        if (rol.equals("ROLE_ADMIN")) {
            reservas = reservationRepository.findAll();
        } else if (rol.equals("ROLE_RECEPTIONIST")) {
            // Opción B: Trae solo las reservas del hotel del recepcionista
            reservas = reservationRepository.findByRoomHotelId(usuarioActivo.getHotel().getId());
        } else {
            // Huésped: Trae solo sus propias reservas
            reservas = reservationRepository.findByGuestId(usuarioActivo.getId());
        }

        return reservas.stream().map(this::mapearADTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResponseDTO obtenerReservaPorId(Long reservaId, String username) {
        Reservation reserva = reservationRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        validarPropiedadDeReserva(reserva, obtenerUsuarioActivo(username));

        return mapearADTO(reserva);
    }

    @Transactional
    public ReservationResponseDTO crearReserva(ReservationRequestDTO request, String username) {
        User usuarioActivo = obtenerUsuarioActivo(username);
        Room habitacion = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        if (!habitacion.getIsAvailable()) {
            throw new BusinessRuleException("La habitación no está disponible para reserva.");
        }

        // Opción B - Recepcionista: Solo puede crear reservas en su hotel
        if (usuarioActivo.getRole().getName().equals("ROLE_RECEPTIONIST")) {
            if (!habitacion.getHotel().getId().equals(usuarioActivo.getHotel().getId())) {
                throw new BusinessRuleException("Solo puedes crear reservas para tu propia sucursal.");
            }
        }

        Reservation reserva = new Reservation();
        reserva.setRoom(habitacion);
        reserva.setGuest(usuarioActivo); // Por simplicidad, asumimos que el usuario autenticado es el huésped
        reserva.setStartDate(request.getStartDate());
        reserva.setEndDate(request.getEndDate());
        reserva.setStatus("CONFIRMED");

        // Al reservar, marcamos la habitación como no disponible
        habitacion.setIsAvailable(false);
        roomRepository.save(habitacion);

        return mapearADTO(reservationRepository.save(reserva));
    }

    @Transactional
    public ReservationResponseDTO actualizarReserva(Long reservaId, ReservationRequestDTO request, String username) {
        Reservation reserva = reservationRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        User usuarioActivo = obtenerUsuarioActivo(username);
        validarPropiedadDeReserva(reserva, usuarioActivo);

        Room nuevaHabitacion = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        // Lógica para liberar la antigua habitación y reservar la nueva si son diferentes
        if (!reserva.getRoom().getId().equals(nuevaHabitacion.getId())) {
            if (!nuevaHabitacion.getIsAvailable()) {
                throw new BusinessRuleException("La nueva habitación seleccionada no está disponible.");
            }
            reserva.getRoom().setIsAvailable(true);
            nuevaHabitacion.setIsAvailable(false);
            roomRepository.save(reserva.getRoom());
            roomRepository.save(nuevaHabitacion);
        }

        reserva.setRoom(nuevaHabitacion);
        reserva.setStartDate(request.getStartDate());
        reserva.setEndDate(request.getEndDate());

        return mapearADTO(reservationRepository.save(reserva));
    }

    @Transactional
    public void cancelarReserva(Long reservaId, String username) {
        Reservation reserva = reservationRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        User usuarioActivo = obtenerUsuarioActivo(username);
        validarPropiedadDeReserva(reserva, usuarioActivo);

        if (reserva.getStatus().equals("CANCELLED")) {
            throw new BusinessRuleException("La reserva ya se encuentra cancelada.");
        }

        reserva.setStatus("CANCELLED");

        // Liberamos la habitación
        Room habitacion = reserva.getRoom();
        habitacion.setIsAvailable(true);
        roomRepository.save(habitacion);

        reservationRepository.save(reserva);
    }

    // --- MÉTODOS DE APOYO INTERNOS (SOLID / Clean Code) ---

    private User obtenerUsuarioActivo(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    // Centraliza la regla de negocio fundamental (Opción B)
    private void validarPropiedadDeReserva(Reservation reserva, User usuarioActivo) {
        String rol = usuarioActivo.getRole().getName();

        if (rol.equals("ROLE_RECEPTIONIST")) {
            Long sucursalReserva = reserva.getRoom().getHotel().getId();
            Long sucursalRecepcionista = usuarioActivo.getHotel().getId();
            if (!sucursalReserva.equals(sucursalRecepcionista)) {
                throw new BusinessRuleException("Acceso denegado: Solo puedes gestionar reservas de tu propia sucursal.");
            }
        } else if (rol.equals("ROLE_GUEST")) {
            if (!reserva.getGuest().getId().equals(usuarioActivo.getId())) {
                throw new BusinessRuleException("Acceso denegado: Esta reserva no te pertenece.");
            }
        }
    }

    private ReservationResponseDTO mapearADTO(Reservation reserva) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reserva.getId());
        dto.setRoomId(reserva.getRoom().getId());
        dto.setRoomType(reserva.getRoom().getType());
        dto.setHotelName(reserva.getRoom().getHotel().getName());
        dto.setGuestUsername(reserva.getGuest().getUsername());
        dto.setStartDate(reserva.getStartDate());
        dto.setEndDate(reserva.getEndDate());
        dto.setStatus(reserva.getStatus());
        return dto;
    }
}