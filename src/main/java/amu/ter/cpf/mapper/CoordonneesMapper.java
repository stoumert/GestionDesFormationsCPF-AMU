package amu.ter.cpf.mapper;

import amu.ter.cpf.model.entity.Contact;
import amu.ter.cpf.model.xml.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CoordonneesMapper {
    @Mapping(source = "codeContact", target = "numero")
    @Mapping(source = "contact", target = "telfix")
    @Mapping(source = "contact", target = "extras")
    CoordonneesXML toCoordonnesXML(Contact contact);

    default Telfixe toTelfixe(Contact contact) {
        Telfixe telfixe = new Telfixe();
        if (contact.getTelephone() != null) {
            telfixe.setNumtel(contact.getTelephone());
        }
        return telfixe;
    }
    default ExtrasCoordonnesXML toExtrasCoordonnes(Contact contact) {
        ExtrasCoordonnesXML extrasCoordonnes = new ExtrasCoordonnesXML();
        List<Extra> extras = new ArrayList<>();
        if (contact.getFonction() != null) {
            extras.add(new ExtraXML("fonction", contact.getFonction()));
        }
        extrasCoordonnes.setExtras(extras);
        return extrasCoordonnes;
    }
}
