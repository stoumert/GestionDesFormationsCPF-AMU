package amu.ter.cpf.mapper;

import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.xml.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ActionMapper {
    @Mapping(source = "codeAction", target = "numero")
    @Mapping(source = "dateCreation", target = "datecrea")
    @Mapping(source = "dateMaj", target = "datemaj")
    @Mapping(source = "conditionSpecifiques", target = "conditionsSpecifiques")
    @Mapping(source = "accesHandicapes", target = "accessHandicapes")
    @Mapping(source = "action", target = "urlAction")
    ActionXML toActionXML(Action action);


    default UrlAction toUrlAction(Action action) {
        if(action.getUrlAction() != null) {
            return new UrlAction(action.getUrlAction());
        }
        return null;
    }

}
