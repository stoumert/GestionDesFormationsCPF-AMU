package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@XmlRootElement(name = "formation")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class FormationXML {

    @XmlAttribute(name = "numero")
    public String numero;

    @XmlAttribute(name = "datemaj")
    public String datemaj;

    @XmlAttribute(name = "datecrea")
    public String datecrea;

    @XmlElement(name = "intitule-formation")
    public String intitule;

    @XmlElement(name = "objectif-formation")
    public String objectif;

    @XmlElement(name = "resultats-attendus")
    public String resultatsAttendus;

    @XmlElement(name = "contenu-formation")
    public String contenuFormation;

    @XmlElement(name = "parcours-de-formation")
    public String parcoursDeFormation;

    @XmlElement(name = "objectif-general-formation")
    public String objectifGeneralFormation;

    @XmlElement(name = "certification")
    public CertificationXML certification;

    @XmlElement(name = "action")
    private List<ActionXML> action;

    @XmlElement(name = "organisme-formation-responsable")
    public OrganismeFormationResponsable organismeFormationResponsable;

    @XmlElement(name = "extras")
    private ExtrasFormationXML extras;


}