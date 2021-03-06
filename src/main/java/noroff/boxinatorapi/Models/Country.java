package noroff.boxinatorapi.Models;

import com.fasterxml.jackson.annotation.JsonGetter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 60)
    @Column(unique = true, nullable = false, length = 60)
    private String name;

    @NotBlank
    @Size(max = 2)
    @Column(nullable = false)
    private int multiplier;

    @OneToMany(mappedBy = "destinationCountry")
    private List<Shipment> shipments;

    @JsonGetter("shipments")
    public List<String> shipments() {
        if (shipments != null) {
           return shipments.stream()
                   .map(shipment -> "/api/shipments/" + shipment.getId()).collect(Collectors.toList());
        }
        return null;
    }

    public Country () {}

    public Country(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }
}
