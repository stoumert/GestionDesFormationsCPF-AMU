package amu.ter.cpf.mapper;

import amu.ter.cpf.model.entity.Formation;
import amu.ter.cpf.model.xml.*;
import amu.ter.cpf.model.xml.CertificationXML;
import amu.ter.cpf.model.xml.ExtrasFormationXML;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FormationMapper {

    @Mapping(source = "codeFormation", target = "numero")
    @Mapping(source = "intitule", target = "intitule")
    @Mapping(source = "objectifFormation", target = "objectif")
    @Mapping(source = "dateMaj", target = "datemaj")
    @Mapping(source = "dateCreation", target = "datecrea")
    @Mapping(source = "resultatsAttendus", target = "resultatsAttendus")
    @Mapping(source = "contenuFormation", target = "contenuFormation")
    @Mapping(source = "parcoursDeFormation", target = "parcoursDeFormation")
    @Mapping(source = "objectifGeneral", target = "objectifGeneralFormation")
    @Mapping(source = "formation", target = "certification")
    @Mapping(source = "formation", target = "extras")
    FormationXML toFormationXML(Formation formation);
    default CertificationXML toCertificationXML(Formation formation) {
        CertificationXML cert = new CertificationXML();

        String type = formation.getTypeCertification();
        String code = formation.getCodeCertification();
        switch (type) {
            case "RNCP" -> cert.setCodeRNCP(code);
            case "RS"   -> cert.setCodeRS(code);
            case "CPF"  -> cert.setCodeCPF(code);
        }
        return cert;
    }
    default String formatDate(LocalDate date) {
        return date == null ? null
                : date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    default ExtrasFormationXML toExtrasFormationXML(Formation formation) {
        List<Extra> extras = new ArrayList<>();

        // 1. categorisation-offre (CPF200)
        if ("CPF200".equals(formation.getCodeCertification())) {
            extras.add(new ExtraXML("categorisation-offre", formation.getCategorisationOffre()));
        }

        // 2. resume-contenu
        extras.add(new ExtraXML("resume-contenu", formation.getResumeContenu()));

        // 3. blocs-competences (imbriqué)
        if (formation.getBlocsCompetences() != null
                && formation.getBlocsCompetences() == 1
                && formation.getCodeBloc() != null
                && !formation.getCodeBloc().isEmpty()) {
            List<ExtraXML> codes = new ArrayList<>();
            for (String code : formation.getCodeBloc()) {
                codes.add(new ExtraXML("code-bloc", code));
            }
            extras.add(new ExtraNestedXML("blocs-competences", codes));
        }

        // 4. blocs-modifiables
        if (formation.getBlocsModifiables() != null) {
            extras.add(new ExtraXML("blocs-modifiables", formation.getBlocsModifiables().toString()));
        }

        ExtrasFormationXML result = new ExtrasFormationXML();
        result.setExtras(extras);
        return result;
    }
}