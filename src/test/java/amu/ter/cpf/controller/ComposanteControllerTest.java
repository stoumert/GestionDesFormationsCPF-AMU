package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.Composante;
import amu.ter.cpf.model.entity.Role;
import amu.ter.cpf.model.entity.User;
import amu.ter.cpf.service.ComposanteService;
import amu.ter.cpf.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
public class ComposanteControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ComposanteService composanteService;
    @MockitoBean
    private UserService userService;
    @Mock
    private static final User manager = new User();
    private static final Composante composante = new Composante();

    @BeforeAll
    static void setUpBeforeClass() {
        manager.setId(1);
        manager.setRole(Role.MANAGER);
        composante.setCodeComposante("SCI");
        composante.setName("Science");
    }

    private MockMvc mockMvc(){
        return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @WithMockUser(authorities = "ROLE_ADMIN")
    @Test
    void shouldReturnComposanteFormPage() throws Exception {
        mockMvc().perform(get("/composante/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("composante_form"));
    }
    @WithMockUser(authorities = "ROLE_ADMIN")
    @Test
    void shouldReturnAssignComposantePage() throws Exception {
        when(composanteService.getComposanteByCodeWithUsers("SCI")).thenReturn(composante);
        mockMvc().perform(get("/composante/SCI/assign"))
                .andExpect(status().isOk())
                .andExpect(view().name("assign_composante"));
    }

    @WithMockUser(authorities = "ROLE_ADMIN")
    @Test
    void shouldPostComposante() throws Exception {
        mockMvc().perform(post("/composante/create")
                    .param("codeComposante", "SCI")
                    .param("name", "Science")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(composanteService).create(any());
    }
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldAssignComposanteToManager() throws Exception {
        when(composanteService.getComposanteByCodeComposante("SCI")).thenReturn(composante);
        when(userService.getUserById(manager.getId())).thenReturn(manager);
        mockMvc().perform(post("/composante/SCI/assign/add").param("managerId","1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/composante/SCI/assign"));
        verify(composanteService).assignComposanteToManager(composante, manager);
    }
    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void shouldDeleteComposanteToManager() throws Exception {
        when(composanteService.getComposanteByCodeComposante("SCI")).thenReturn(composante);
        when(userService.getUserById(manager.getId())).thenReturn(manager);
        mockMvc().perform(post("/composante/SCI/assign/remove").param("managerId","1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/composante/SCI/assign"));
        verify(composanteService).removeManager(composante, manager);
    }

}
