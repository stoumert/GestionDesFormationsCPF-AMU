package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "coordonnees")
@Data
public class CoordonneesXML {

    @XmlAttribute(name = "numero")
    private String numero;

    @XmlElement(name = "nom")
    private String nom;

    @XmlElement(name = "prenom")
    private String prenom;

    @XmlElement(name = "adresse")
    private AdresseXML adresse;

    @XmlElement(name = "telfixe")
    private Telfixe telfix;

    @XmlElement(name = "courriel")
    private String courriel;

    @XmlElement(name = "extras")
    private ExtrasCoordonnesXML extras;
}
