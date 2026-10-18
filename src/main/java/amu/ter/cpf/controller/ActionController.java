package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.entity.Formation;
import amu.ter.cpf.model.referential.*;
import amu.ter.cpf.service.ActionService;
import amu.ter.cpf.validation.*;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/action")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    private void addStep1Referential(Model model) {
        model.addAttribute("modalitesEnseignement", TeachingMethod.values());
        model.addAttribute("modalitesEntreesSorties", EntryAndExitProcedures.values());
        model.addAttribute("langues", Language.values());
        model.addAttribute("modaliteAdmission", AdmissionRequirements.values());
        model.addAttribute("rythmeFormation", TrainingPace.values());
    }

    private void addStep3Referential(Model model) {
        model.addAttribute("typesHoraires", ScheduleType.values());
        model.addAttribute("groupesFormationEmploi", TrainingEmploymentGroup.values());
        model.addAttribute("perimetresRecrutement", RecruitmentScope.values());
    }
    private void addStep4Referential(Model model) {
        model.addAttribute("tauxTva", Tva.values());
        model.addAttribute("modelesEconomiques", EconomicModel.values());

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/create")
    public String showStep1(@RequestParam String codeFormation, Model model) {
        Action action;
        try {
             action = actionService.createAndAssignFormation(codeFormation);
        }catch (IllegalArgumentException e){
            return "redirect:/formation";
        }
        model.addAttribute("action", action);
        model.addAttribute("isEdit", false);
        addStep1Referential(model);
        return "action_step1";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/step1")
    public String reloadStep1(@RequestParam String codeAction, Model model) {
        Action action = actionService.getByCode(codeAction);
        model.addAttribute("action", action);
        model.addAttribute("isEdit", true);
        addStep1Referential(model);
        return "action_step1";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/step1")
    public String saveStep1(@Validated(Step1Validation.class) @ModelAttribute Action action,
                            BindingResult result,
                            @RequestParam String submitType,
                            Model model) {
        if (result.hasErrors()) {
            addStep1Referential(model);
            return "action_step1";
        }
        actionService.saveActionIdentity(action);
        if (submitType.equals("save")) {
            return "redirect:/formation/" + action.getFormation().getCodeFormation() + "/actions";
        }
        return "redirect:/action/step2?codeAction=" + action.getCodeAction();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/step2")
    public String showStep2(@RequestParam String codeAction, Model model) {
        model.addAttribute("action", actionService.getByCode(codeAction));
        return "action_step2";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/step2")
    public String saveStep2(@Validated(Step2Validation.class) @ModelAttribute Action action,
                            BindingResult result,
                            @RequestParam String submitType
    ) {
        if (result.hasErrors()) return "action_step2";
        actionService.saveEducationalContent(action.getCodeAction() ,action);
        if (submitType.equals("save")) {
            return "redirect:/formation/" + action.getFormation().getCodeFormation() + "/actions";
        }
        return "redirect:/action/step3?codeAction=" + action.getCodeAction();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/step3")
    public String showStep3(@RequestParam String codeAction, Model model) {
        model.addAttribute("action", actionService.getByCode(codeAction));
        addStep3Referential(model);
        return "action_step3";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/step3")
    public String saveStep3(@Validated(Step3Validation.class) @ModelAttribute Action action,
                            BindingResult result,
                            @RequestParam String submitType,
                            Model model) {
        if (result.hasErrors()) {
            addStep3Referential(model);
            return "action_step3";
        }
        actionService.saveTimesAndDuration(action.getCodeAction() ,action);
        if (submitType.equals("save")) {
            return "redirect:/formation/" + action.getFormation().getCodeFormation() + "/actions";
        }
        return "redirect:/action/step4?codeAction=" + action.getCodeAction();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/step4")
    public String showStep4(@RequestParam String codeAction, Model model) {
        model.addAttribute("action", actionService.getByCode(codeAction));
        addStep4Referential(model);
        return "action_step4";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/step4")
    public String saveStep4(@Validated(Step4Validation.class)@ModelAttribute Action action,
                            BindingResult result,
                            @RequestParam String submitType,
                            Model model) {
        if (result.hasErrors()) {
            addStep4Referential(model);
            return "action_step4";
        }
        actionService.saveCosts(action.getCodeAction() ,action);
        if (submitType.equals("save")) {
            return "redirect:/formation/" + action.getFormation().getCodeFormation() + "/actions";
        }
        return "redirect:/action/step5?codeAction=" + action.getCodeAction();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/step5")
    public String showStep5(@RequestParam String codeAction, Model model) {
        model.addAttribute("action", actionService.getByCode(codeAction));
        return "action_step5";
    }

    @PostMapping("/step5")
    public String saveStep5(@Validated(Step5Validation.class) @ModelAttribute("action") Action action,
                            BindingResult result,
                            @RequestParam String submitType,
                            Model model) {
        if (result.hasErrors()) return "action_step5";

        if ("validate".equals(submitType)) {
            try {
                actionService.publish(action.getCodeAction(), action);
            } catch (ConstraintViolationException e) {
                e.getConstraintViolations().forEach(v ->
                        result.rejectValue(
                                v.getPropertyPath().toString(),
                                "publication.invalid",
                                v.getMessage()
                        )
                );
                model.addAttribute("publicationError",
                        "L'action ne peut pas être validée car des informations obligatoires sont manquantes.");
                return "action_step5";
            }
        } else {
            actionService.saveLogistics(action.getCodeAction(), action);
        }
        return "redirect:/formation/" + action.getFormation().getCodeFormation() + "/actions";
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("{codeAction}/delete")
    public String deleteAction(@PathVariable String codeAction) {
        Action action = actionService.getByCode(codeAction);
        String codeFormation = action.getFormation().getCodeFormation();
        actionService.delete(codeAction);
        return "redirect:/formation/" + codeFormation + "/actions";
    }
}
