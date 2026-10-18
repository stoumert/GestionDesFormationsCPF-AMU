package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GeneralTrainingObjective {
    OBJECTIVE_2(1,"Perfectionnement, élargissement des compétences"),
    OBJECTIVE_4(4,"Création d'entreprise"),
    OBJECTIVE_5(5, "Remise à niveau, maîtrise des savoirs de base, initiation"),
    OBJECTIVE_6(6, "Certification"),
    OBJECTIVE_7(7, "Professionnalisation"),
    OBJECTIVE_8(8, "Préparation à la qualification"),
    OBJECTIVE_9(9,"(Re)mobilisation, aide à l'élaboration de projet professionnel");

    private final int code;
    private final String description;
}
