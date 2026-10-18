package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class ExtraNestedXML implements Extra {
    @XmlAttribute(name = "info")
    private String info;
    @XmlElement(name = "extra")
    private List<ExtraXML> enfants;
    @XmlElement(name = "coordonnees")
    private CoordonneesXML coordonnees;

    public ExtraNestedXML(String info, List<ExtraXML> enfants) {
        this.info = info;
        this.enfants = enfants;
    }
    public ExtraNestedXML(String info, CoordonneesXML coordonneesXML) {
        this.info = info;
        this.coordonnees = coordonneesXML;
    }

}