package amu.ter.cpf.service;

import amu.ter.cpf.mapper.*;
import amu.ter.cpf.model.entity.*;
import amu.ter.cpf.model.xml.*;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock private FormationService formationService;
    @Mock private FormationMapper formationMapper;
    @Mock private ActionMapper actionMapper;
    @Mock private SessionMapper sessionMapper;
    @Mock private CoordonneesMapper coordonneesMapper;
    @Mock private AdresseMapper adresseMapper;

    @InjectMocks
    private ExportService exportService;

    private Formation buildFormationWithGraph() {

        Contact contact = new Contact();
        contact.setAdresseLigne1("L1");
        contact.setAdresseLigne2("L2");

        Formation formation = new Formation();
        formation.setContact(contact);

        Action action = new Action();
        action.setFormation(formation);

        action.setModalitesHandicap("handicap");
        action.setInfoAdmission("info");
        action.setCodeGfe(10);
        action.setCodeTypeHoraires(2);
        action.setDureeApprentissage(100);
        action.setCodeRythmeFormations(List.of(1, 2));
        action.setCodeModaliteAdmissions(List.of(3));
        action.setFraisAnpec(BigDecimal.TEN);
        action.setFraisCertifInclusFraisAnpec(1);
        action.setTauxTva(BigDecimal.valueOf(20));
        action.setFraisHt(BigDecimal.valueOf(100));
        action.setFraisTtc(BigDecimal.valueOf(120));

        Session session = new Session();
        session.setGarantie(1);
        session.setModalitesParticulieres("MP");


        session.setAction(action);
        action.setSessions(List.of(session));

        formation.setActions(List.of(action));

        return formation;
    }



    @Test
    void shouldExportFullXml() throws Exception {

        Formation formation = buildFormationWithGraph();

        when(formationService.getAllActif()).thenReturn(List.of(formation));

        when(formationMapper.toFormationXML(any())).thenReturn(new FormationXML());
        when(actionMapper.toActionXML(any())).thenReturn(new ActionXML());
        when(sessionMapper.toSessionXML(any())).thenReturn(new SessionXML());

        when(coordonneesMapper.toCoordonnesXML(any())).thenReturn(new CoordonneesXML());
        when(adresseMapper.toAdresseXML(any())).thenReturn(new AdresseXML());

        byte[] result = exportService.export();

        assertNotNull(result);
        assertTrue(result.length > 0);

        verify(formationService).getAllActif();
    }


    @Test
    void shouldHandleEmptyFormationList() throws Exception {

        when(formationService.getAllActif()).thenReturn(List.of());

        byte[] result = exportService.export();

        assertNotNull(result);
    }
}