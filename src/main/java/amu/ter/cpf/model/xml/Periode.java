package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@Data
public class Periode {
    @XmlElement(name = "debut")
    private String debut;
    @XmlElement(name = "fin")
    private String fin;
}
