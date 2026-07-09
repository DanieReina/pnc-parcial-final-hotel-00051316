package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.request.RoomRequestDTO;
import com.uca.pncparcialfinalhotel.dto.response.RoomResponseDTO;
import com.uca.pncparcialfinalhotel.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Permitimos que cualquier usuario (incluso no autenticado, si el negocio lo requiere) vea la disponibilidad
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAvailableRooms(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) Boolean available) {

        return ResponseEntity.ok(roomService.listarHabitaciones(hotelId, available));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.obtenerHabitacionPorId(id));
    }

    // Solo el Administrador puede crear, modificar o eliminar habitaciones del inventario
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponseDTO> createRoom(@Valid @RequestBody RoomRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.crearHabitacion(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponseDTO> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequestDTO request) {

        return ResponseEntity.ok(roomService.actualizarHabitacion(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.eliminarHabitacion(id);
        return ResponseEntity.noContent().build();
    }
}
