package amu.ter.cpf.controller;
import amu.ter.cpf.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping(value = "/export")
    public ResponseEntity<byte[]> exportTest() throws Exception {

        byte[] xml = exportService.export();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"catalogue.xml\"")
                .contentType(MediaType.parseMediaType("application/xml; charset=ISO-8859-1"))
                .body(xml);
    }


}