package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtraXML implements Extra{
    @XmlAttribute(name = "info")
    private String info;
    @XmlValue
    private String value;
}