package pl.mkramek.dictionary.support.mapping;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.entity.Language;
import pl.mkramek.dictionary.model.http.request.LanguageRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("[test suite] Language Mapper")
public class LanguageMapperTest {
    private final LanguageMapper mapper = new LanguageMapper();

    @DisplayName("[test] Map entity to DTO")
    @Test
    void givenEntity_whenMappedToDTO_returnValidDTO() {
        Language language = new Language(UUID.randomUUID(), "English");
        LanguageDTO dto = mapper.toDTO(language);
        assertNotNull(dto);
        assertEquals(language.getId(), dto.getId());
        assertEquals(language.getName(), dto.getName());
    }

    @DisplayName("[test] Map request to DTO")
    @Test
    void givenRequest_whenMappedToDTO_returnValidDTO() {
        LanguageRequest request = new LanguageRequest("French");
        LanguageDTO dto = mapper.toDTO(request);
        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(request.getName(), dto.getName());
    }

    @DisplayName("[test] Map DTO to entity")
    @Test
    void givenDTO_whenMappedToEntity_returnValidEntity() {
        LanguageDTO dto = new LanguageDTO(UUID.randomUUID(), "German");
        Language entity = mapper.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
    }
}
