package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.entity.Formation;
import amu.ter.cpf.service.ActionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
public class ActionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ActionService actionService;


    @MockitoBean
    private Validator validator;

    private MockMvc mockMvc;
    private Action action;
    private Formation formation;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        formation = new Formation();
        formation.setCodeFormation("FORM001");

        action = new Action();
        action.setCodeAction("ACT001");
        action.setFormation(formation);


        when(validator.validate(any(), any())).thenReturn(Collections.emptySet());
        when(validator.validateProperty(any(), any(), any())).thenReturn(Collections.emptySet());
        when(validator.validateValue(any(), any(), any(), any())).thenReturn(Collections.emptySet());
        when(validator.forExecutables()).thenReturn(mock(jakarta.validation.executable.ExecutableValidator.class));
    }



    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void showStep1ShouldCreateActionAndReturnForm() throws Exception {
        when(actionService.createAndAssignFormation("FORM001")).thenReturn(action);

        mockMvc.perform(get("/action/create")
                        .param("codeFormation", "FORM001"))
                .andExpect(status().isOk())
                .andExpect(view().name("action_step1"))
                .andExpect(model().attributeExists("action"))
                .andExpect(model().attribute("isEdit", false))
                .andExpect(model().attributeExists("modalitesEnseignement"))
                .andExpect(model().attributeExists("langues"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void showStep1ShouldRedirectWhenFormationNotFound() throws Exception {
        when(actionService.createAndAssignFormation("UNKNOWN"))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/action/create")
                        .param("codeFormation", "UNKNOWN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void reloadStep1ShouldReturnFormWithAction() throws Exception {
        when(actionService.getByCode("ACT001")).thenReturn(action);

        mockMvc.perform(get("/action/step1")
                        .param("codeAction", "ACT001"))
                .andExpect(status().isOk())
                .andExpect(view().name("action_step1"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep1WithSaveTypeShouldRedirectToActions() throws Exception {
        mockMvc.perform(post("/action/step1")
                        .with(csrf())
                        .param("submitType", "save")
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation/FORM001/actions"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep1WithNextTypeShouldRedirectToStep2() throws Exception {
        mockMvc.perform(post("/action/step1")
                        .with(csrf())
                        .param("submitType", "next")
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/action/step2?codeAction=ACT001"));
    }



    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void showStep2ShouldReturnForm() throws Exception {
        when(actionService.getByCode("ACT001")).thenReturn(action);

        mockMvc.perform(get("/action/step2")
                        .param("codeAction", "ACT001"))
                .andExpect(status().isOk())
                .andExpect(view().name("action_step2"))
                .andExpect(model().attributeExists("action"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep2WithSaveTypeShouldRedirectToActions() throws Exception {
        mockMvc.perform(post("/action/step2")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001")
                        .param("submitType", "save"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation/FORM001/actions"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep2WithNextTypeShouldRedirectToStep3() throws Exception {
        mockMvc.perform(post("/action/step2")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001")
                        .param("submitType", "next"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/action/step3?codeAction=ACT001"));
    }



    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void showStep3ShouldReturnForm() throws Exception {
        when(actionService.getByCode("ACT001")).thenReturn(action);

        mockMvc.perform(get("/action/step3")
                        .param("codeAction", "ACT001"))
                .andExpect(status().isOk())
                .andExpect(view().name("action_step3"))
                .andExpect(model().attributeExists("action"))
                .andExpect(model().attributeExists("typesHoraires"))
                .andExpect(model().attributeExists("perimetresRecrutement"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep3WithSaveTypeShouldRedirectToActions() throws Exception {
        mockMvc.perform(post("/action/step3")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001")
                        .param("submitType", "save"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation/FORM001/actions"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep3WithNextTypeShouldRedirectToStep4() throws Exception {
        mockMvc.perform(post("/action/step3")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001")
                        .param("submitType", "next"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/action/step4?codeAction=ACT001"));
    }



    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void showStep4ShouldReturnForm() throws Exception {
        when(actionService.getByCode("ACT001")).thenReturn(action);

        mockMvc.perform(get("/action/step4")
                        .param("codeAction", "ACT001"))
                .andExpect(status().isOk())
                .andExpect(view().name("action_step4"))
                .andExpect(model().attributeExists("action"))
                .andExpect(model().attributeExists("tauxTva"))
                .andExpect(model().attributeExists("modelesEconomiques"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep4WithSaveTypeShouldRedirectToActions() throws Exception {
        mockMvc.perform(post("/action/step4")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001")
                        .param("submitType", "save"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation/FORM001/actions"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep4WithNextTypeShouldRedirectToStep5() throws Exception {
        mockMvc.perform(post("/action/step4")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001")
                        .param("submitType", "next"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/action/step5?codeAction=ACT001"));
    }



    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void showStep5ShouldReturnForm() throws Exception {
        when(actionService.getByCode("ACT001")).thenReturn(action);

        mockMvc.perform(get("/action/step5")
                        .param("codeAction", "ACT001"))
                .andExpect(status().isOk())
                .andExpect(view().name("action_step5"))
                .andExpect(model().attributeExists("action"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep5WithSaveTypeShouldCallSaveLogisticsAndRedirect() throws Exception {
        mockMvc.perform(post("/action/step5")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001")
                        .param("submitType", "save"))
                .andExpect(status().is3xxRedirection());

        verify(actionService).saveLogistics(eq("ACT001"), any(Action.class));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep5WithValidateTypeShouldCallPublishAndRedirect() throws Exception {
        mockMvc.perform(post("/action/step5")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001")
                        .param("submitType", "validate"))
                .andExpect(status().is3xxRedirection());

        verify(actionService).publish(eq("ACT001"), any(Action.class));
    }
/*
    @SuppressWarnings("unchecked")
    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveStep5WithValidateTypeShouldReturnFormOnConstraintViolation() throws Exception {
        ConstraintViolation<Action> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath())
                .thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getMessage()).thenReturn("champ requis");

        doThrow(new ConstraintViolationException(Set.of(violation)))
                .when(actionService).publish(eq("ACT001"), any(Action.class));

        mockMvc.perform(post("/action/step5")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("formation.codeFormation", "FORM001")
                        .param("submitType", "validate"))
                .andExpect(status().isOk())
                .andExpect(view().name("action_step5"))
                .andExpect(model().attributeExists("publicationError"));
    }
*/


    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void deleteActionShouldCallServiceAndRedirect() throws Exception {
        when(actionService.getByCode("ACT001")).thenReturn(action);

        mockMvc.perform(post("/action/ACT001/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation/FORM001/actions"));

        verify(actionService).delete("ACT001");
    }



    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void showStep1ShouldBeForbiddenForUser() throws Exception {
        mockMvc.perform(get("/action/create")
                        .param("codeFormation", "FORM001"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void saveStep1ShouldBeForbiddenForUser() throws Exception {
        mockMvc.perform(post("/action/step1")
                        .with(csrf())
                        .param("codeAction", "ACT001")
                        .param("submitType", "save"))
                .andExpect(status().isForbidden());
    }
}