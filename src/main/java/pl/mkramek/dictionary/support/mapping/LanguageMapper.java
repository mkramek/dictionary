package pl.mkramek.dictionary.support.mapping;

import org.springframework.stereotype.Component;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.entity.Language;
import pl.mkramek.dictionary.model.http.request.LanguageRequest;

import java.util.UUID;

@Component
public class LanguageMapper {
    public LanguageDTO toDTO(Language language) {
        return prepareDTO(language);
    }

    public LanguageDTO toDTO(LanguageRequest request) {
        return prepareDTO(request);
    }

    public Language toEntity(LanguageDTO langDTO) {
        return prepareEntity(langDTO);
    }

    private LanguageDTO prepareDTO(Language lang) {
        return new LanguageDTO(lang.getId(), lang.getName());
    }

    private LanguageDTO prepareDTO(LanguageRequest request) {
        return new LanguageDTO(UUID.randomUUID(), request.getName());
    }

    private Language prepareEntity(LanguageDTO dto) {
        Language entity = new Language();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }
}
