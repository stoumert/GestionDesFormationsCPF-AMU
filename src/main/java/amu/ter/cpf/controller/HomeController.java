package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.Composante;
import amu.ter.cpf.service.ComposanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ComposanteService composanteService;
    @GetMapping("/")
    public ModelAndView getComposantes() {
        List<Composante> composantes = composanteService.getAllComposantes();
        return new ModelAndView("index",
                "composantes", composantes);
    }

}
