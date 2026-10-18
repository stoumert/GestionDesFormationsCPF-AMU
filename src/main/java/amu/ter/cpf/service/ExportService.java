package amu.ter.cpf.service;

import amu.ter.cpf.mapper.*;
import amu.ter.cpf.model.entity.*;
import amu.ter.cpf.model.xml.*;
import amu.ter.cpf.model.xml.OrganismeFormationResponsable;
import amu.ter.cpf.model.xml.SiretOrganismeFormation;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final FormationService formationService;
    private final FormationMapper formationMapper;
    private final ActionMapper actionMapper;
    private final SessionMapper sessionMapper;
    private final CoordonneesMapper coordonneesMapper;
    private final AdresseMapper adresseMapper;
    private final static String SIRET = "13001533200013";

    @PreAuthorize("hasRole('ADMIN')")
    public byte[] export() throws JAXBException {
        List<Formation> formations = formationService.getAllActif();
        List<FormationXML> formationXMLS = formations.stream().map(
                formation -> {
                    FormationXML formationXML = formationMapper.toFormationXML(formation);
                    formationXML.setOrganismeFormationResponsable(buildOrganisme());
                    List<Action> actions = formation.getActions();
                    List<ActionXML> actionXMLS = actions.stream().map(
                            action ->{
                                ActionXML actionXML = actionMapper.toActionXML(action);
                                actionXML.setExtras(buildExtrasAction(action));
                                actionXML.setLieuDeFormation(buildLieuDeFormation(formation));
                                actionXML.setAdresseInformation(buildAdresseInfo(formation));
                                List<Session> sessions = action.getSessions();
                                List<SessionXML> sessionXMLS = sessions.stream().map(
                                        session -> {
                                            SessionXML sessionXML = sessionMapper.toSessionXML(session);
                                            sessionXML.setAdresse(buildAdresseInscriptionXML(formation));
                                            sessionXML.setExtras(buildExtrasSession(session));
                                            return sessionXML;
                                        }
                                ).toList();
                                actionXML.setSessions(sessionXMLS);
                                return actionXML;
                            }).toList();
                    formationXML.setAction(actionXMLS);
                    return formationXML;
                }).toList();

        LheoXML lheoXML = new LheoXML(formationXMLS);

        JAXBContext context = JAXBContext.newInstance(LheoXML.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        marshaller.marshal(lheoXML, out);
        return out.toByteArray();
    }
    private OrganismeFormationResponsable buildOrganisme() {
        SiretOrganismeFormation s = new SiretOrganismeFormation();
        s.setSiret(ExportService.SIRET);
        OrganismeFormationResponsable o = new OrganismeFormationResponsable();
        o.setSiretOrganismeFormation(s);
        return o;
    }
    private LieuDeFormation buildLieuDeFormation(Formation formation) {
        CoordonneesXML coordonneesXML = buildCoordonneesXMLWithAdresseBloc(formation);
        LieuDeFormation lieuDeFormation = new LieuDeFormation();
        lieuDeFormation.setCoordonnees(coordonneesXML);
        return lieuDeFormation;
    }
    private ExtrasSession buildExtrasSession(Session session) {
        ExtrasSession extrasSession = new ExtrasSession();
        List<Extra> extras = new ArrayList<>();

        CoordonneesXML coordonneesXML = buildCoordonneesXMLWithoutAdresseBloc(session.getAction().getFormation());
        ExtraNestedXML extraNestedXML = new ExtraNestedXML("contact-inscription", coordonneesXML);
        extras.add(extraNestedXML);

        if (session.getGarantie() != null) {
            extras.add(new ExtraXML("garantie",session.getGarantie().toString()));
        }
        if (session.getModalitesParticulieres() != null) {
            extras.add(new ExtraXML("modalites-particulieres", session.getModalitesParticulieres()));
        }
        extrasSession.setExtras(extras);
        return extrasSession;
    }
    private ExtrasActionXML buildExtrasAction(Action action) {
        ExtrasActionXML extrasActionXML = new ExtrasActionXML();
        List<Extra> extraXMLS = new ArrayList<>();
        CoordonneesXML coordonneesXML = buildCoordonneesXMLWithoutAdresseBloc(action.getFormation());

        extraXMLS.add(new ExtraNestedXML("contact-information", coordonneesXML));
        if (action.getModalitesHandicap() != null) {
            extraXMLS.add(new ExtraXML("modalites-handicap", action.getModalitesHandicap()));
        }
        if (action.getInfoAdmission() != null) {
            extraXMLS.add(new ExtraXML("info-admission", action.getInfoAdmission()));
        }
        extraXMLS.add(new ExtraNestedXML("code-modalite-admission", getCodeModaliteAdmission(action)));

        if (action.getModalitesHandicap() != null) {
            extraXMLS.add(new ExtraXML("modalites-handicap", action.getModalitesHandicap()));
        }
        if (action.getCodeGfe() != null) {
            extraXMLS.add(new ExtraXML("code-gfe", action.getCodeGfe().toString()));
        }
        if (action.getCodeTypeHoraires() != null) {
            extraXMLS.add(new ExtraXML("code-type-horaires", action.getCodeTypeHoraires().toString()));
        }
        if (action.getDureeApprentissage() != null) {
            extraXMLS.add(new ExtraXML("duree-apprentissage", action.getDureeApprentissage().toString()));
        }
        extraXMLS.add(new ExtraNestedXML("codes-rythme-formation", getCodesRythmeFormation(action)));

        if (action.getFraisAnpec() != null) {
            extraXMLS.add(new ExtraXML("frais-anpec", action.getFraisAnpec().toString()));
        }
        extraXMLS.add(new ExtraXML("frais-certif-inclus-frais-anpec", action.getFraisCertifInclusFraisAnpec().toString()));

        if (action.getDetailFraisAnpec() != null) {
            extraXMLS.add(new ExtraXML("detail-frais-anpec", action.getDetailFraisAnpec()));
        }
        if (action.getCodeModeleEconomique() != null) {
            extraXMLS.add(new ExtraXML("code-modele-economique", action.getCodeModeleEconomique().toString()));
        }
        if (action.getAutresServices() != null) {
            extraXMLS.add(new ExtraXML("autres-services", action.getAutresServices()));
        }
        extraXMLS.add(new ExtraNestedXML("frais-pedagogiques",getFrais(action)));

        if (action.getExistencePrerequis() != null){
            extraXMLS.add(new ExtraXML("existence-prerequis", action.getExistencePrerequis().toString()));
        }
        extrasActionXML.setExtras(extraXMLS);
        return extrasActionXML;
    }

    private static List<ExtraXML> getCodeModaliteAdmission(Action action) {
        List<ExtraXML> codeModaliteAdmission = new ArrayList<>();
        for (Integer code : action.getCodeModaliteAdmissions()){
            codeModaliteAdmission.add(new ExtraXML("code-modalite-admission", code.toString()));
        }
        return codeModaliteAdmission;
    }
    private static List<ExtraXML> getCodesRythmeFormation (Action action) {
        List<ExtraXML> codesRythmeFormation = new ArrayList<>();
        for (Integer codeRythmeFormation : action.getCodeRythmeFormations()){
            codesRythmeFormation.add(new ExtraXML("code-rythme-formation", codeRythmeFormation.toString()));
        }
        return codesRythmeFormation;
    }
    private static List<ExtraXML> getFrais(Action action) {
        List<ExtraXML> frais = new ArrayList<>();
        frais.add(new ExtraXML("taux-tva", action.getTauxTva().toString()));
        frais.add(new ExtraXML("frais-ht", action.getFraisHt().toString()));
        frais.add(new ExtraXML("frais-ttc",action.getFraisTtc().toString()));
        return frais;
    }

    private CoordonneesXML buildCoordonneesXMLWithAdresseBloc(Formation formation) {
        Contact contact = formation.getContact();
        CoordonneesXML coordonneesXML = coordonneesMapper.toCoordonnesXML(contact);
        if (contact.getAdresseLigne1() != null && !contact.getAdresseLigne1().isBlank()) {
            AdresseXML adresseXML = buildAdressXML(contact);
            coordonneesXML.setAdresse(adresseXML);
            return coordonneesXML;
        }
        return coordonneesXML;

    }
    private CoordonneesXML buildCoordonneesXMLWithoutAdresseBloc(Formation formation) {
        Contact contact = formation.getContact();
        return coordonneesMapper.toCoordonnesXML(contact);
    }
    private AdresseInformation buildAdresseInfo(Formation formation) {
        AdresseInformation adresseInformation = new AdresseInformation();
        Contact contact = formation.getContact();
        AdresseXML adresseXML = buildAdressXML(contact);
        adresseInformation.setAdresse(adresseXML);
        return adresseInformation;
    }

    private AdresseXML buildAdressXML(Contact contact) {
        AdresseXML adresseXML = adresseMapper.toAdresseXML(contact);
        adresseXML.setLigne(buildLignes(contact));
        return adresseXML;
    }
    private AdresseInscription buildAdresseInscriptionXML(Formation formation) {
        Contact contact = formation.getContact();
        AdresseXML adresseXML = buildAdressXML(contact);
        AdresseInscription adresseInscription = new AdresseInscription();
        adresseInscription.setAdresseInscription(adresseXML);
        return adresseInscription;
    }

    private List<String> buildLignes(Contact contact) {
        List<String> lignes = new ArrayList<>();
        if (contact.getAdresseLigne1() != null && !contact.getAdresseLigne1().isBlank()){
            lignes.add(contact.getAdresseLigne1());
        }
        if (contact.getAdresseLigne2() != null && !contact.getAdresseLigne2().isBlank()){
            lignes.add(contact.getAdresseLigne2());
        }
        return lignes;
    }
}
