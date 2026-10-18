package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "session")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SessionXML {

    @XmlAttribute(name = "numero")
    public String numero;

    @XmlAttribute(name = "datemaj")
    public String datemaj;

    @XmlAttribute(name = "datecrea")
    public String datecrea;

    @XmlElement(name = "periode")
    public Periode periode;

    @XmlElement(name = "adresse-inscription")
    public AdresseInscription adresse;

    @XmlElement(name = "etat-recrutement")
    public String etatRecrutement;

    @XmlElement(name = "extras")
    private ExtrasSession extras;


}
