package subway.section.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import subway.section.api.SectionService;
import subway.section.api.response.SectionResponse;
import subway.section.presentation.request.SectionCreateRequest;

import java.net.URI;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId, @RequestBody SectionCreateRequest request) {
        SectionResponse sectionResponse = sectionService.create(lineId, request);
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + sectionResponse.getSectionId())).body(sectionResponse);
    }
}