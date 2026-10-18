package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "adresse")
@Data
public class AdresseXML {

    @XmlAttribute(name = "numero")
    private String numero;

    @XmlElement(name = "ligne")
    private List<String> ligne;

    @XmlElement(name = "codepostal")
    private String codepostal;

    @XmlElement(name = "ville")
    private String ville;

    @XmlElement(name = "pays")
    private String pays;

    @XmlElement(name = "geolocalisation")
    private GeolocalisationXML geolocalisation;

    @XmlElement(name = "extras")
    private ExtrasAdresseXML extras;
}
