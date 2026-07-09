package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.request.ReservationRequestDTO;
import com.uca.pncparcialfinalhotel.dto.response.ReservationResponseDTO;
import com.uca.pncparcialfinalhotel.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Todos los roles autenticados pueden crear reservas (la lógica interna valida los detalles)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'GUEST')")
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO request,
            Principal principal) {

        ReservationResponseDTO response = reservationService.crearReserva(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'GUEST')")
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations(Principal principal) {
        // El servicio filtrará qué reservas devolver dependiendo de quién está preguntando
        return ResponseEntity.ok(reservationService.obtenerReservasPorUsuario(principal.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'GUEST')")
    public ResponseEntity<ReservationResponseDTO> getReservationById(
            @PathVariable Long id,
            Principal principal) {

        return ResponseEntity.ok(reservationService.obtenerReservaPorId(id, principal.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDTO request,
            Principal principal) {

        // Huéspedes no pueden modificar reservas directamente, solo cancelar o crear
        return ResponseEntity.ok(reservationService.actualizarReserva(id, request, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'GUEST')")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id, Principal principal) {
        // En el servicio se aplica la Opción B y la regla del Huésped
        reservationService.cancelarReserva(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}