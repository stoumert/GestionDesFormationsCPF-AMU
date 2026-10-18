package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitmentScope {
    MUNICIPALITY(1, "Commune"),
    DEPARTMENT(2, "Départment"),
    REGION(3, "Région"),
    INTERREGION(4, "Interrégion"),
    NATIONAL(5, "National"),
    INTERNATIONAL(6, "International"),
    OTHER(7, "Autres");
    private final int code;
    private final String value;
}
