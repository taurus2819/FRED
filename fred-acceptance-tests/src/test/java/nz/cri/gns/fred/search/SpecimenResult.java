package nz.cri.gns.fred.search;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.steps.UIInteractionSteps;

import java.util.List;
import java.util.stream.Collectors;

public class SpecimenResult extends UIInteractionSteps {
    public List<String> specimens() {
        return findAll(SpecimenResultList.RESULT_SPECIMENS)
                .stream()
                .map(WebElementFacade::getTextContent)
                .collect(Collectors.toList());
    }
}
