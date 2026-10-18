package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class AdresseInscription {
    @XmlElement(name = "adresse")
    private AdresseXML adresseInscription;
}
