package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeachingMethod {
    FACE_TO_FACE(0,"Formation présentielle"),
    MIXED(1,"Formation mixte"),
    REMOTE(2, " Formation entièrement à distance");

    private final int code;
    private final String description;
}
