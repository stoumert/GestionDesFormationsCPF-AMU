package amu.ter.cpf.mapper;

import amu.ter.cpf.model.entity.Contact;
import amu.ter.cpf.model.xml.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AdresseMapper {

    @Mapping(source = "codeContact", target = "numero")
    @Mapping(source = "contact", target = "geolocalisation")
    @Mapping(source = "contact", target = "extras")
    AdresseXML toAdresseXML(Contact contact);

    default GeolocalisationXML toGeolocalisationXML(Contact contact) {
        GeolocalisationXML geolocalisation = new GeolocalisationXML();
        if (contact.getLongitude() != null && contact.getLatitude() != null) {
            geolocalisation.setLatitude(contact.getLatitude().toString());
            geolocalisation.setLongitude(contact.getLongitude().toString());
            return geolocalisation;
        }
        return null;
    }
    default ExtrasAdresseXML toExtrasAdresseXML(Contact contact) {
        ExtrasAdresseXML extrasAdresseXML = new ExtrasAdresseXML();
        List<Extra> extras = new ArrayList<>();

        if (contact.getAdresseLigne2() != null && !contact.getAdresseLigne2().isBlank()) {
            extras.add(new ExtraXML("ligne5-adresse", contact.getAdresseLigne2()));
        }
        if (contact.getNumeroVoie() != null && !contact.getNumeroVoie().isBlank()) {
            extras.add(new ExtraXML("numero-voie", contact.getNumeroVoie()));
        }
        if (contact.getCodeIndiceRepetition() != null && !contact.getCodeIndiceRepetition().isBlank()) {
            extras.add(new ExtraXML("code-indice-repetition", contact.getCodeIndiceRepetition()));
        }
        if (contact.getCodeNatureVoie() != null && !contact.getCodeNatureVoie().isBlank()) {
            extras.add(new ExtraXML("code-nature-voie", contact.getCodeNatureVoie()));
        }
        if (contact.getLibelleVoie() != null && !contact.getLibelleVoie().isBlank()) {
            extras.add(new ExtraXML("libelle-voie", contact.getLibelleVoie()));
        }
        extras.add(new ExtraXML("conformite-reglementaire", contact.getConformiteReglementaire().toString()));

        extrasAdresseXML.setExtras(extras);
        return extrasAdresseXML;
    }
}
