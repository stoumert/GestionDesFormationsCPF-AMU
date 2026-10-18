package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "organisme-formation-responsable")
@Data
public class OrganismeFormationResponsable {
    @XmlElement(name = "SIRET-organisme-formation")
    public SiretOrganismeFormation siretOrganismeFormation;

}
