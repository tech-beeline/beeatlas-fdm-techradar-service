package ru.beeline.techradar.maper;

import org.springframework.stereotype.Component;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.domain.TechVersion;
import ru.beeline.techradar.dto.RingDTO;
import ru.beeline.techradar.dto.TechVersionDTO;

import java.time.format.DateTimeFormatter;

@Component
public class TechVersionMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public TechVersionDTO toTechVersionDTO(TechVersion techVersion, Ring ring) {
        if (techVersion == null || ring == null) {
            return null;
        }

        RingDTO ringDTO = new RingDTO(ring.getId(), ring.getName(), ring.getOrder());

        return new TechVersionDTO(
                techVersion.getId(),
                techVersion.getVersionStart(),
                techVersion.getVersionEnd(),
                techVersion.getCreatedDate() != null ? techVersion.getCreatedDate().format(DATE_TIME_FORMATTER) : null,
                techVersion.getDeletedDate() != null ? techVersion.getDeletedDate().format(DATE_TIME_FORMATTER) : null,
                techVersion.getLastModifiedDate() != null ? techVersion.getLastModifiedDate().format(DATE_TIME_FORMATTER) : null,
                ringDTO
        );
    }

}