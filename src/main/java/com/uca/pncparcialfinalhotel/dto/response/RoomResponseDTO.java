package com.uca.pncparcialfinalhotel.dto.response;

public class RoomResponseDTO {
    private Long id;
    private Long hotelId;
    private String hotelName; // Extraemos el nombre para que el Frontend no tenga que hacer otro fetch
    private String type;
    private Double price;
    private Boolean isAvailable;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}