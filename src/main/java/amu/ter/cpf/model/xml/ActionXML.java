package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "action")
@Data
public class ActionXML {
    @XmlAttribute(name = "numero")
    public String numero;

    @XmlAttribute(name = "datemaj")
    public String datemaj;

    @XmlAttribute(name = "datecrea")
    public String datecrea;

    @XmlElement(name = "niveau-entree-obligatoire")
    public String niveauEntreeObligatoire;

    @XmlElement(name = "modalites-enseignement")
    public String modalitesEnseignement;

    @XmlElement(name = "conditions-specifiques")
    public String conditionsSpecifiques;

    @XmlElement(name = "lieu-de-formation")
    public LieuDeFormation lieuDeFormation;

    @XmlElement(name = "modalites-entrees-sorties")
    public String modalitesEntreesSorties;

    @XmlElement(name = "url-action")
    public UrlAction urlAction;

    @XmlElement(name = "session")
    public List<SessionXML> sessions;

    @XmlElement(name = "adresse-information")
    public AdresseInformation adresseInformation;

    //date-information

    @XmlElement(name = "restauration")
    public String restauration;

    @XmlElement(name = "hebergement")
    public String hebergement;

    @XmlElement(name = "transport")
    public String transport;

    @XmlElement(name = "acces-handicapes")
    public String accessHandicapes;

    @XmlElement(name = "langue-formation")
    public String langueFormation;

    @XmlElement(name = "modalites-recrutement")
    public String modalitesRecrutement;

    @XmlElement(name = "modalites-pedagogiques")
    public String modalitesPedagogiques;

    @XmlElement(name = "code-perimetre-recrutement")
    public String codePerimetreRecrutement;

    @XmlElement(name = "infos-perimetre-recrutement")
    public String infosPerimetreRecrutement;

    @XmlElement(name = "nombre-heures-centre")
    public String nombreHeuresCentre;

    @XmlElement(name = "nombre-heures-entreprise")
    public String nombreHeuresEntreprise;

    @XmlElement(name = "extras")
    public ExtrasActionXML extras;

}
