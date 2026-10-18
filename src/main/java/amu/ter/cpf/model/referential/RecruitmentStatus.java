package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecruitmentStatus {
    OPEN(1,"Ouvert"),
    CLOSED(2,"Fermé");

    private final int code;
    private final String description;
}
