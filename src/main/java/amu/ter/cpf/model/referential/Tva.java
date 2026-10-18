package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Tva {
    TVA_1(0.0,"Net de TVA"),
    TVA_2(5.5,"Soumis au taux de 5,5 %"),
    TVA_3(20.0, "Soumis au taux de 20 %");

    private final Double value;
    private final String description;
}
