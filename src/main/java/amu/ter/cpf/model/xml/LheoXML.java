package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement(name = "lheo")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LheoXML {
    @XmlElementWrapper(name = "offres")
    @XmlElement(name = "formation")
    private List<FormationXML> offres;

}
