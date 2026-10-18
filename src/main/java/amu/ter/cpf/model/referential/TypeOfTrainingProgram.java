package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TypeOfTrainingProgram {
    COLLECTIVE(1,96213,"Collectif"),
    INDIVIDUALISED(2,96211, "Individualisé"),
    MODULAR(3,96212, "Modulaire"),
    MIXED(4, 96214,"Mixte");

    private final int type;
    private final int code;
    private final String libelle;
}
