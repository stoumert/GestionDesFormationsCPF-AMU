package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Guarantee {

  NO(0, "Non"),
  YES(1, "Oui");

  private final int code;
  private final String description;
}