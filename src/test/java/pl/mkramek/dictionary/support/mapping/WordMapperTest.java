package pl.mkramek.dictionary.support.mapping;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.model.entity.Language;
import pl.mkramek.dictionary.model.entity.Word;
import pl.mkramek.dictionary.model.http.request.LanguageRequest;
import pl.mkramek.dictionary.model.http.request.WordRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[test suite] Word Mapper")
public class WordMapperTest {
    private final WordMapper mapper = new WordMapper();

    @DisplayName("[test] Map entity to DTO")
    @Test
    public void givenEntity_whenMappedToDTO_returnValidDTO() {
        Language language = new Language(UUID.randomUUID(), "English");
        Word word = new Word();
        word.setId(UUID.randomUUID());
        word.setContent("test");
        word.setLanguage(language);

        WordDTO dto = mapper.toDTO(word);

        assertEquals(word.getId(), dto.getId());
        assertEquals(word.getContent(), dto.getContent());
        assertEquals(word.getLanguage().getId(), dto.getLanguage().getId());
        assertEquals(word.getLanguage().getName(), dto.getLanguage().getName());
        assertTrue(word.getTranslations().isEmpty());
        assertTrue(word.getTranslationOf().isEmpty());
    }

    @DisplayName("[test] Map request to DTO")
    @Test
    public void givenRequest_whenMappedToDTO_returnValidDTO() {
        LanguageRequest languageRequest = new LanguageRequest("Spanish");
        WordRequest request = new WordRequest("test", languageRequest);

        WordDTO dto = mapper.toDTO(request);

        assertEquals(request.getContent(), dto.getContent());
        assertEquals(request.getLanguage().getName(), dto.getLanguage().getName());
    }

    @DisplayName("[test] Map DTO to entity")
    @Test
    public void givenDTO_whenMappedToEntity_returnValidEntity() {
        LanguageDTO languageDTO = new LanguageDTO(UUID.randomUUID(), "Hindi");
        WordDTO wordDTO = new WordDTO(UUID.randomUUID(), "test", languageDTO);

        Word entity = mapper.toEntity(wordDTO);

        assertEquals(wordDTO.getId(), entity.getId());
        assertEquals(wordDTO.getContent(), entity.getContent());
        assertEquals(wordDTO.getLanguage().getId(), entity.getLanguage().getId());
        assertEquals(wordDTO.getLanguage().getName(), entity.getLanguage().getName());
    }
}
