package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IndiceRepetition {

  INDICE_REPETITION_1("B","bis"),
  INDICE_REPETITION_2("C","quinquies"),
  INDICE_REPETITION_3("Q","quarter"),
  INDICE_REPETITION_4("T","ter");

  private final String code;
  private final String indiceRepetition;

  public static boolean isValidIndex(String index) {
    for (IndiceRepetition value : values()) {
      if (value.code.equals(index)) {
        return true;
      }
    }
    return false;
  }

}
