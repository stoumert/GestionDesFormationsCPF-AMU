package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class GeolocalisationXML {
    @XmlElement(name = "latitude")
    public String latitude;
    @XmlElement(name = "longitude")
    public String longitude;

}
