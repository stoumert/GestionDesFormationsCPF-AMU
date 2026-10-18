package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.*;
import amu.ter.cpf.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
class FormationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockitoBean
    private FormationService formationService;

    @MockitoBean
    private ComposanteService composanteService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ContactService contactService;

    private User manager;
    private Formation formation;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        manager = new User();
        manager.setEmail("manager@test.com");
        manager.setRole(Role.MANAGER);

        Composante comp = new Composante();
        comp.setCodeComposante("C1");

        manager.setComposante(comp);

        formation = new Formation();
        formation.setCodeFormation("FORM1");
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldShowCreateForm() throws Exception {

        Composante comp = new Composante();
        comp.setCodeComposante("C1");

        manager.setComposante(comp);

        when(userService.getUserByEmail("manager@test.com")).thenReturn(manager);
        when(composanteService.getAllComposantes()).thenReturn(List.of(comp));

        mockMvc.perform(get("/formation/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_step1"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldReturnNoComposanteView() throws Exception {

        manager.setComposante(null);

        when(userService.getUserByEmail("manager@test.com")).thenReturn(manager);
        when(composanteService.getAllComposantes()).thenReturn(List.of());

        mockMvc.perform(get("/formation/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("no_composante"));
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldCreateFormation() throws Exception {

        Composante comp = new Composante();
        comp.setCodeComposante("C1");

        Formation created = new Formation();
        created.setCodeFormation("FORM1");

        when(userService.getUserByEmail(anyString())).thenReturn(manager);
        when(composanteService.getComposanteByCodeComposante("C1")).thenReturn(comp);
        when(formationService.createFormationIdentity(any(Formation.class)))
                .thenReturn(created);

        mockMvc.perform(post("/formation/create")
                        .param("codeFormation", "FORM1")
                        .param("codeComposante", "C1")
                        .param("typeCertification", "RS")
                        .param("codeCertification", "RS1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation/FORM1/edit-content"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldPublishFormation() throws Exception {

        doNothing().when(formationService).publishFormation("FORM1");

        mockMvc.perform(post("/formation/FORM1/publish"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));

        verify(formationService).publishFormation("FORM1");
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldDeleteFormation() throws Exception {

        doNothing().when(formationService).deleteFormation("FORM1");

        mockMvc.perform(post("/formation/FORM1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));

        verify(formationService).deleteFormation("FORM1");
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldViewFormation() throws Exception {

        when(formationService.getFormationByCodeWithActions("FORM1"))
                .thenReturn(formation);

        when(contactService.getContactsByComposante(any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/formation/view/FORM1"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_view"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldSubmitStep1_success() throws Exception {

        Composante comp = new Composante();
        comp.setCodeComposante("C1");

        Formation created = new Formation();
        created.setCodeFormation("FORM1");

        when(composanteService.getComposanteByCodeComposante("C1")).thenReturn(comp);
        when(formationService.createFormationIdentity(any(Formation.class)))
                .thenReturn(created);

        mockMvc.perform(post("/formation/create")
                        .param("codeFormation", "FORM1")
                        .param("codeComposante", "C1")
                        .param("typeCertification", "RS")
                        .param("codeCertification", "RS1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation/FORM1/edit-content"));
    }
    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldSubmitStep2_success() throws Exception {

        doNothing().when(formationService)
                .createFormationWithAllOtherFields(any(), eq("FORM1"));

        mockMvc.perform(post("/formation/FORM1/edit-content")
                        .param("codeFormation", "FORM1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));
    }
    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldShowStep2() throws Exception {

        when(formationService.getFormationByCode("FORM1"))
                .thenReturn(formation);

        when(contactService.getContactsByComposante(any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/formation/FORM1/edit-content"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_form"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldAssignContact() throws Exception {

        when(formationService.getFormationByCode("FORM1"))
                .thenReturn(formation);

        doNothing().when(contactService)
                .assignContactToFormation("C1", "FORM1");

        mockMvc.perform(post("/formation/FORM1/assign-contact")
                        .param("codeContact", "C1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation/view/FORM1"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldHandlePublishException() throws Exception {

        doThrow(new RuntimeException("error"))
                .when(formationService)
                .publishFormation("FORM1");

        mockMvc.perform(post("/formation/FORM1/publish"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldCreateDraftFormation() throws Exception {

        Composante comp = new Composante();
        comp.setCodeComposante("C1");

        formation.setComposante(comp);

        when(formationService.getFormationByCode("FORM1"))
                .thenReturn(formation);

        when(userService.getUserByEmail(anyString())).thenReturn(manager);

        mockMvc.perform(post("/formation/createDraft/FORM1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldNotCreateDraftFormationWhenManagerHasNoComposante() throws Exception {
        when(formationService.getFormationByCode("FORM1"))
            .thenReturn(formation);

        User managerWithoutComposante = new User();
        managerWithoutComposante.setRole(Role.MANAGER);
        managerWithoutComposante.setComposante(null);

        when(userService.getUserByEmail("manager@test.com"))
            .thenReturn(managerWithoutComposante);
        mockMvc.perform(post("/formation/createDraft/FORM1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/formation"))
            .andExpect(flash().attribute(
                "error",
                "Votre compte n’est associé à aucune composante. Vous ne pouvez pas créer de brouillon"
            ));
        verify(formationService).getFormationByCode("FORM1");
        verify(userService).getUserByEmail("manager@test.com");
        verify(formationService, never()).createDraftVersion(anyString());
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldShowEditPage() throws Exception {

        when(formationService.getFormationByCode("FORM1")).thenReturn(formation);

        mockMvc.perform(get("/formation/FORM1/edit-content"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_form"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldPublishFormation_exception() throws Exception {

        doThrow(new RuntimeException("fail"))
                .when(formationService).publishFormation("FORM1");

        mockMvc.perform(post("/formation/FORM1/publish"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldShowStep2Page() throws Exception {

        when(formationService.getFormationByCode("FORM1"))
                .thenReturn(formation);

        mockMvc.perform(get("/formation/FORM1/edit-content"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_form"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldAssignContact_missingParam() throws Exception {

        when(formationService.getFormationByCode("FORM1"))
                .thenReturn(formation);

        mockMvc.perform(post("/formation/FORM1/assign-contact"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldSubmitStep1_validationError() throws Exception {
        when(userService.getUserByEmail(anyString())).thenReturn(manager);
        when(composanteService.getAllComposantes())
                .thenReturn(List.of(manager.getComposante()));

        mockMvc.perform(post("/formation/create")
                        .param("codeFormation", "FORM1")
                        .param("codeComposante", "C1")
                        .param("typeCertification", "RS")
                        .param("codeCertification", "INVALID"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_step1"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldSubmitStep1_serviceThrows() throws Exception {
        Composante comp = new Composante();
        comp.setCodeComposante("C1");

        when(userService.getUserByEmail(anyString())).thenReturn(manager);
        when(composanteService.getComposanteByCodeComposante("C1")).thenReturn(comp);
        when(formationService.createFormationIdentity(any(Formation.class)))
                .thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/formation/create")
                        .param("codeFormation", "FORM1")
                        .param("codeComposante", "C1")
                        .param("typeCertification", "RS")
                        .param("codeCertification", "RS1234"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_step1"))
                .andExpect(model().attributeExists("error"));
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldSubmitStep2_serviceThrows() throws Exception {
        doThrow(new RuntimeException("fail"))
                .when(formationService)
                .createFormationWithAllOtherFields(any(), eq("FORM1"));

        mockMvc.perform(post("/formation/FORM1/edit-content")
                        .param("codeFormation", "FORM1"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldSubmitEdit_success() throws Exception {
        doNothing().when(formationService).alterFormation(eq("FORM1"), any());

        mockMvc.perform(post("/formation/FORM1/edit")
                        .param("codeFormation", "FORM1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation/view/FORM1"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldSubmitEdit_serviceThrows() throws Exception {
        doThrow(new RuntimeException("fail"))
                .when(formationService).alterFormation(eq("FORM1"), any());

        mockMvc.perform(post("/formation/FORM1/edit")
                        .param("codeFormation", "FORM1"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_form"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldPublish_constraintViolation() throws Exception {
        doThrow(new jakarta.validation.ConstraintViolationException("invalide", java.util.Set.of()))
                .when(formationService).publishFormation("FORM1");

        mockMvc.perform(post("/formation/FORM1/publish"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldGetActions() throws Exception {
        when(formationService.getFormationByCodeWithActions("FORM1"))
                .thenReturn(formation);

        mockMvc.perform(get("/formation/FORM1/actions"))
                .andExpect(status().isOk())
                .andExpect(view().name("formation_actions"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldGetActions_notFound() throws Exception {
        when(formationService.getFormationByCodeWithActions("FORM1"))
                .thenReturn(null);

        mockMvc.perform(get("/formation/FORM1/actions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldCreateDraft_noComposante() throws Exception {
        manager.setComposante(null);

        when(formationService.getFormationByCode("FORM1")).thenReturn(formation);
        when(userService.getUserByEmail(anyString())).thenReturn(manager);

        mockMvc.perform(post("/formation/createDraft/FORM1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldCreateDraft_wrongComposante() throws Exception {
        Composante autre = new Composante();
        autre.setCodeComposante("C2");
        formation.setComposante(autre);

        when(formationService.getFormationByCode("FORM1")).thenReturn(formation);
        when(userService.getUserByEmail(anyString())).thenReturn(manager);

        mockMvc.perform(post("/formation/createDraft/FORM1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));

        verify(formationService, never()).createDraftVersion(anyString());
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldAssignContact_formationNotFound() throws Exception {
        when(formationService.getFormationByCode("FORM1")).thenReturn(null);

        mockMvc.perform(post("/formation/FORM1/assign-contact")
                        .param("codeContact", "C1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error_page"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void shouldAssignContact_serviceThrows() throws Exception {
        when(formationService.getFormationByCode("FORM1")).thenReturn(formation);
        doThrow(new RuntimeException("interdit"))
                .when(contactService).assignContactToFormation("C1", "FORM1");

        mockMvc.perform(post("/formation/FORM1/assign-contact")
                        .param("codeContact", "C1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error_page"))
                .andExpect(model().attributeExists("message"));
    }


}