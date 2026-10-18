package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EconomicModel {
    ECONOMIC_MODEL_1(1,"Assiduité : calcul en heure, demi-journée, jour"),
    ECONOMIC_MODEL_2(2,"Forfait : calcul forfaitaire");
    private final int code;
    private final String description;
}
