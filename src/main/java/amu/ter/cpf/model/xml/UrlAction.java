package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
public class UrlAction {
    @XmlElement(name = "urlweb")
    private String url;
}
