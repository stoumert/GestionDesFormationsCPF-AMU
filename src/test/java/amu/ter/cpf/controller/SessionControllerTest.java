package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.entity.Formation;
import amu.ter.cpf.model.entity.Session;
import amu.ter.cpf.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
public class SessionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private SessionService sessionService;

    private MockMvc mockMvc;
    private Action action;
    private Session session;
    private Formation formation;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        formation = new Formation();
        formation.setCodeFormation("FORM001");

        action = new Action();
        action.setCodeAction("ACT001");
        action.setFormation(formation);

        session = new Session();
        session.setCodeSession("SES001");
        session.setAction(action);
        session.setEtatRecrutement(1);
        session.setGarantie(1);
    }



    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void listSessionsShouldReturnSessionListPage() throws Exception {
        when(sessionService.getAction("ACT001")).thenReturn(action);
        when(sessionService.getSessionsByAction("ACT001"))
                .thenReturn(List.of(session));

        mockMvc.perform(get("/session/action/ACT001"))
                .andExpect(status().isOk())
                .andExpect(view().name("session_list"))
                .andExpect(model().attributeExists("action"))
                .andExpect(model().attributeExists("sessions"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void listSessionsShouldRedirectWhenActionNotFound() throws Exception {
        when(sessionService.getAction("UNKNOWN"))
                .thenThrow(new IllegalArgumentException("Action introuvable"));

        mockMvc.perform(get("/session/action/UNKNOWN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));
    }


    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void showCreateFormShouldReturnSessionForm() throws Exception {
        when(sessionService.prepareNewSession("ACT001"))
                .thenReturn(session);

        mockMvc.perform(get("/session/create")
                        .param("codeAction", "ACT001"))
                .andExpect(status().isOk())
                .andExpect(view().name("session_form"))
                .andExpect(model().attributeExists("session"))
                .andExpect(model().attributeExists("action"))
                .andExpect(model().attribute("isEdit", false))
                .andExpect(model().attributeExists("recruitmentStatuses"))
                .andExpect(model().attributeExists("guarantees"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void showCreateFormShouldRedirectWhenActionNotFound() throws Exception {
        when(sessionService.prepareNewSession("UNKNOWN"))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/session/create")
                        .param("codeAction", "UNKNOWN"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/formation"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void createSessionShouldRedirectOnSuccess() throws Exception {
        when(sessionService.getAction("ACT001")).thenReturn(action);

        mockMvc.perform(post("/session/create")
                        .param("codeAction", "ACT001")
                        .param("codeSession", "SES001")
                        .param("etatRecrutement", "1")
                        .param("garantie", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/session/action/ACT001"));

        verify(sessionService).createSession(eq("ACT001"), any(Session.class));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void createSessionShouldReturnFormOnValidationErrors() throws Exception {
        when(sessionService.getAction("ACT001")).thenReturn(action);

        mockMvc.perform(post("/session/create")
                        .param("codeAction", "ACT001")
                        .param("codeSession", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("session_form"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void showEditFormShouldReturnSessionForm() throws Exception {
        when(sessionService.getByCode("SES001")).thenReturn(session);
        when(sessionService.getAction("ACT001")).thenReturn(action);

        mockMvc.perform(get("/session/SES001/edit")
                        .param("codeAction", "ACT001"))
                .andExpect(status().isOk())
                .andExpect(view().name("session_form"))
                .andExpect(model().attributeExists("session"))
                .andExpect(model().attributeExists("action"))
                .andExpect(model().attribute("isEdit", true))
                .andExpect(model().attributeExists("recruitmentStatuses"))
                .andExpect(model().attributeExists("guarantees"));
    }


    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveEditShouldRedirectOnSuccess() throws Exception {
        when(sessionService.getAction("ACT001")).thenReturn(action);

        mockMvc.perform(post("/session/SES001/edit")
                        .param("codeAction", "ACT001")
                        .param("codeSession", "SES001")
                        .param("etatRecrutement", "1")
                        .param("garantie", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/session/action/ACT001"));

        verify(sessionService).saveSession(eq("SES001"), any(Session.class));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void saveEditShouldReturnFormOnValidationErrors() throws Exception {
        when(sessionService.getAction("ACT001")).thenReturn(action);

        mockMvc.perform(post("/session/SES001/edit")
                        .param("codeAction", "ACT001")
                        .param("codeSession", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("session_form"));
    }



    @Test
    @WithMockUser(authorities = "ROLE_MANAGER")
    void deleteShouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/session/SES001/delete"))
                .andExpect(status().is3xxRedirection());

        verify(sessionService).deleteSession("SES001");
    }


}