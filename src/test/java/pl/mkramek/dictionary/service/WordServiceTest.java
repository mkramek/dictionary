package pl.mkramek.dictionary.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.dto.WordDTO;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[test suite] Word Service")
@SpringBootTest
@Transactional
public class WordServiceTest {

    @Autowired
    private WordService wordService;

    @Autowired
    private LanguageService languageService;

    @DisplayName("[test] Create new word")
    @Test
    public void whenCreateWord_returnCreatedWord() {
        LanguageDTO languageDTO = new LanguageDTO(UUID.randomUUID(), "English");

        WordDTO wordDTO = new WordDTO("hello", languageDTO);
        Optional<WordDTO> createdWordDTO = wordService.createWord(wordDTO);

        assertTrue(createdWordDTO.isPresent());
        assertEquals(wordDTO.getContent(), createdWordDTO.get().getContent());
        assertEquals(wordDTO.getLanguage().getName(), createdWordDTO.get().getLanguage().getName());
    }

    @DisplayName("[test] Word search")
    @Test
    public void givenWord_whenSearch_returnFoundResult() {
        Optional<LanguageDTO> lang = languageService.createLanguage(new LanguageDTO("Spanish"));
        assert lang.isPresent();
        WordDTO wordDTO = new WordDTO(UUID.randomUUID(), "hola", lang.get());
        Optional<WordDTO> createdWordDTO = wordService.createWord(wordDTO);
        assert createdWordDTO.isPresent();

        Optional<WordDTO> foundWordDTO = wordService.getByContent("hola", "Spanish");

        assertTrue(foundWordDTO.isPresent());
        assertEquals(createdWordDTO.get().getId(), foundWordDTO.get().getId());
    }

    @DisplayName("[test] Get all results paginated")
    @Test
    public void givenData_whenRequestedForPaginatedData_returnPaginatedResults() {
        Optional<LanguageDTO> languageDTO = languageService.createLanguage(new LanguageDTO("French"));
        assert languageDTO.isPresent();

        WordDTO wordDTO1 = new WordDTO("bonjour", languageDTO.get());
        wordService.createWord(wordDTO1);

        WordDTO wordDTO2 = new WordDTO("au revoir", languageDTO.get());
        wordService.createWord(wordDTO2);

        WordDTO wordDTO3 = new WordDTO("bien", languageDTO.get());
        wordService.createWord(wordDTO3);

        Page<WordDTO> wordsPage1 = wordService.getAllPaged(0, 2);
        Page<WordDTO> wordsPage2 = wordService.getAllPaged(1, 2);

        assertEquals(2, wordsPage1.getNumberOfElements());
        assertEquals(2, wordsPage1.getTotalPages());
        assertEquals(0, wordsPage1.getNumber());
        assertEquals("bonjour", wordsPage1.getContent().get(0).getContent());
        assertEquals(1, wordsPage2.getNumberOfElements());
        assertEquals(2, wordsPage2.getTotalPages());
        assertEquals(1, wordsPage2.getNumber());
        assertEquals("bien", wordsPage2.getContent().get(0).getContent());
    }

    @DisplayName("[test] Update word")
    @Test
    public void givenOldWord_whenUpdate_returnUpdatedWord() {
        Optional<LanguageDTO> languageDTO = languageService.createLanguage(new LanguageDTO("German"));
        assert languageDTO.isPresent();

        WordDTO wordDTO = new WordDTO("hallo", languageDTO.get());
        Optional<WordDTO> createdWordDTO = wordService.createWord(wordDTO);
        assert createdWordDTO.isPresent();

        WordDTO updatedWordDTO = new WordDTO("guten tag", languageDTO.get());
        Optional<WordDTO> updatedWordOptional = wordService.updateWord(createdWordDTO.get().getId(), updatedWordDTO);

        assertTrue(updatedWordOptional.isPresent());
        assertEquals(updatedWordDTO.getContent(), updatedWordOptional.get().getContent());
    }
}