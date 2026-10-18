package amu.ter.cpf.controller;

import amu.ter.cpf.service.ExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
public class ExportControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ExportService exportService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    @WithMockUser
    void shouldExportXmlFileSuccessfully() throws Exception {
        byte[] xmlContent = "<lheo></lheo>".getBytes();

        when(exportService.export()).thenReturn(xmlContent);

        mockMvc.perform(get("/export"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"catalogue.xml\""
                ))
                .andExpect(content().contentTypeCompatibleWith(
                        "application/xml"
                ))
                .andExpect(content().bytes(xmlContent));

        verify(exportService).export();
    }



}