package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtrasCoordonnesXML {
    @XmlAttribute(name = "info")
    private String info = "coordonnees";

    @XmlElements({
            @XmlElement(name = "extra", type = ExtraXML.class),
            @XmlElement(name = "extra", type = ExtraNestedXML.class)
    })
    private List<Extra> extras;
}
