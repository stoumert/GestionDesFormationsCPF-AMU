package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class ExtrasSession {

    @XmlAttribute(name = "info")
    private String info = "session";

    @XmlElements({
            @XmlElement(name = "extra", type = ExtraXML.class),
            @XmlElement(name = "extra", type = ExtraNestedXML.class)
    })
    private List<Extra> extras;
}
