package noroff.boxinatorapi.Config;

import noroff.boxinatorapi.Models.ShipmentStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;

public class StringToEnumConverter implements Converter<String, ShipmentStatus> {
    @Override
    public ShipmentStatus convert(String source) {
        try {
            return ShipmentStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
