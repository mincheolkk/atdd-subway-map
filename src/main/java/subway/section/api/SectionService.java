package subway.section.api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.global.exception.AlreadyRegisteredException;
import subway.global.exception.SectionMismatchException;
import subway.global.exception.StationNotMatchException;
import subway.global.exception.InsufficientStationException;
import subway.line.domain.Line;
import subway.line.repository.LineRepository;
import subway.section.SectionRepository;
import subway.section.api.response.SectionResponse;
import subway.section.domain.Section;
import subway.section.presentation.request.SectionCreateRequest;

import javax.persistence.EntityNotFoundException;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;

    public SectionService(SectionRepository sectionRepository, LineRepository lineRepository) {
        this.sectionRepository = sectionRepository;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public SectionResponse create(Long lineId, SectionCreateRequest request) {
        Line line = getLine(lineId);

        validateSequence(request, line);
        validateUniqueness(request, line);

        Section section = SectionCreateRequest.toEntity(
                request.getDownStationId(),
                request.getUpStationId(),
                request.getDistance()
        );
        section.addLine(line);
        Section savedSection = sectionRepository.save(section);

        line.getSections().addSection(savedSection);
        line.changeDownStationId(request.getDownStationId());
        line.changeDistance(request.getDistance());

        return SectionResponse.of(savedSection);
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        Line line = getLine(lineId);

        validateDownStationId(stationId, line);
        validateLastStation(line);

        Section section = line.getSections().getLastSection();
        line.changeDownStationId(section.getDownStationId());
        line.changeDistance(-section.getDistance());

        line.getSections().deleteLastSection();
    }

    private Line getLine(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Line Id '%d'를 찾을 수 없습니다.", lineId))
        );
    }

    private void validateSequence(SectionCreateRequest request, Line line) {
        if (line.getDownStationId() != request.getUpStationId()) {
            throw new SectionMismatchException();
        }
    }

    private void validateUniqueness(SectionCreateRequest request, Line line) {
        if (line.getSections().getStationIds().contains(request.getDownStationId())) {
            throw new AlreadyRegisteredException();
        }
    }

    private void validateLastStation(Line line) {
        if (line.getSections().getStationIds().size() < 1) {
            throw new InsufficientStationException();
        }
    }

    private void validateDownStationId(Long stationId, Line line) {
        if (line.getDownStationId() != stationId) {
            throw new StationNotMatchException();
        }
    }

}