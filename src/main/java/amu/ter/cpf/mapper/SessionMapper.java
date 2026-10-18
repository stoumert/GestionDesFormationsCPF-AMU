package amu.ter.cpf.mapper;

import amu.ter.cpf.model.entity.Session;
import amu.ter.cpf.model.xml.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(source = "codeSession", target = "numero")
    @Mapping(source = "dateCreation", target = "datecrea")
    @Mapping(source = "dateMaj", target = "datemaj")
    @Mapping(source = "session", target = "periode")
    SessionXML toSessionXML(Session session);

    default String formatDate(LocalDate date) {
        return date == null ? null
                : date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    default Periode getPeriode(Session session) {
        if (session.getDateDebut() != null && session.getDateFin() != null) {
            return new Periode(session.getDateDebut().format(DateTimeFormatter.ofPattern("yyyyMMdd")) ,
                    session.getDateFin().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        return null;
    }
}
