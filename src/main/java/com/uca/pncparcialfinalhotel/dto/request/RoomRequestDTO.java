package com.uca.pncparcialfinalhotel.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoomRequestDTO {

    @NotNull(message = "El ID de la sucursal (hotel) es obligatorio")
    private Long hotelId;

    @NotBlank(message = "El tipo de habitación es obligatorio (ej. SIMPLE, DOBLE, SUITE)")
    private String type;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Double price;

    @NotNull(message = "Debe indicar si la habitación está disponible")
    private Boolean isAvailable;

    // Getters y Setters
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}