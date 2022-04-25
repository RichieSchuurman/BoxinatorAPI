package noroff.boxinatorapi.Models;

import com.fasterxml.jackson.annotation.JsonGetter;

import javax.persistence.*;

@Entity
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String receiverName;

    @Column
    private Integer weightOption;

    @Column
    private String boxColor;

    @Column
    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account sender;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country destinationCountry;

    @JsonGetter("sender")
    public String sender() {
        if (sender != null) {
            return "api/account/" + sender.getKeyCloakUserId();
        }
        return null;
    }

    @JsonGetter("destinationCountry")
    public String destinationCountry() {
        if (destinationCountry != null) {
            return "api/settings/countries/" + destinationCountry.getId();
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Integer getWeightOption() {
        return weightOption;
    }

    public void setWeightOption(Integer weightOption) {
        this.weightOption = weightOption;
    }

    public String getBoxColor() {
        return boxColor;
    }

    public void setBoxColor(String boxColor) {
        this.boxColor = boxColor;
    }

    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(ShipmentStatus shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Country getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(Country destinationCountry) {
        this.destinationCountry = destinationCountry;
    }
}
