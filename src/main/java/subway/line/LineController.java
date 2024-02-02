package subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/lines")
    public ResponseEntity<LineResponse> createLine(@RequestBody LineCreateRequest request) {
        LineResponse lineResponse = lineService.create(request);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

    @GetMapping("/lines")
    public ResponseEntity<List<LineResponse>> getLines() {
        List<LineResponse> responses = lineService.getLines();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/lines/{lineId}")
    public ResponseEntity<LineResponse> getLine(@PathVariable Long lineId) {
        LineResponse response = lineService.getLine(lineId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/lines/{lineId}")
    public ResponseEntity<Void> updateLine(@PathVariable Long lineId, @RequestBody LineUpdateRequest request) {
        lineService.updateLine(lineId, request);
        return ResponseEntity.ok().build();
    }
}



