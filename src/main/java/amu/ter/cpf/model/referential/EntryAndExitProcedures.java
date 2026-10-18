package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EntryAndExitProcedures {
    WITH_FIXED_DATE(0,"A dates fixes"),
    PERMANENT(1,"Permanentes (sans dates fixes)");

    private final int code;
    private final String description;
}
