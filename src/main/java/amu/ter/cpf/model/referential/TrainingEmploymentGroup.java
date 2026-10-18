package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrainingEmploymentGroup {
    TRAINING_EMPLOYMENT_GROUP_1(1,"Agriculture"),
    TRAINING_EMPLOYMENT_GROUP_2(2,"Pêche, Mer"),
    TRAINING_EMPLOYMENT_GROUP_3(3,"Bâtiment : gros oeuvre, travaux publics"),
    TRAINING_EMPLOYMENT_GROUP_4(4,"Bâtiment : second oeuvre"),
    TRAINING_EMPLOYMENT_GROUP_5(5,"Travail des métaux"),
    TRAINING_EMPLOYMENT_GROUP_6(6,"Mécanique"),
    TRAINING_EMPLOYMENT_GROUP_7(7,"Electromécanique"),
    TRAINING_EMPLOYMENT_GROUP_8(8,"Electricité, énergie"),
    TRAINING_EMPLOYMENT_GROUP_9(9, "Appui à la production des industries"),
    TRAINING_EMPLOYMENT_GROUP_10(10,"Transformation des matériaux, procédés"),
    TRAINING_EMPLOYMENT_GROUP_11(11,"Production alimentaire"),
    TRAINING_EMPLOYMENT_GROUP_12(12,"Textile, habillement, cuir"),
    TRAINING_EMPLOYMENT_GROUP_13(13,"Travail du bois, matériaux de synthèse"),
    TRAINING_EMPLOYMENT_GROUP_14(14,"Techniques graphiques, impression"),
    TRAINING_EMPLOYMENT_GROUP_15(15,"Transport, logistique"),
    TRAINING_EMPLOYMENT_GROUP_16(16,"Gestion et traitement de l'information"),
    TRAINING_EMPLOYMENT_GROUP_17(17,"Commerce"),
    TRAINING_EMPLOYMENT_GROUP_18(18,"Santé, social, soins personnels"),
    TRAINING_EMPLOYMENT_GROUP_19(19,"Hôtellerie, tourisme, loisirs"),
    TRAINING_EMPLOYMENT_GROUP_20(20, "Environnement, nettoyage, sécurité"),
    TRAINING_EMPLOYMENT_GROUP_21(21, "Communication, médias"),
    TRAINING_EMPLOYMENT_GROUP_22(22, "Création et production artistique"),
    TRAINING_EMPLOYMENT_GROUP_23(23, "Autres professions, professions d'enseignement et de la magistrature"),
    TRAINING_EMPLOYMENT_GROUP_24(24, "Formation d'insertion");

    private final int code;
    private final String description;
}
