package amu.ter.cpf.model.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CertificationXML {

    @XmlElement(name = "code-RNCP")
    private String codeRNCP;
    @XmlElement(name = "code-CPF")
    private String codeCPF;
    @XmlElement(name = "code-RS")
    private String codeRS;
}
