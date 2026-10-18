package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ExtrasActionXML {
    @XmlAttribute(name = "info")
    private String info = "action";

    @XmlElements({
            @XmlElement(name = "extra", type = ExtraXML.class),
            @XmlElement(name = "extra", type = ExtraNestedXML.class)
    })
    private List<Extra> extras;
}
