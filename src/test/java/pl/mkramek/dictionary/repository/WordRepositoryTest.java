package pl.mkramek.dictionary.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.mkramek.dictionary.model.entity.Language;
import pl.mkramek.dictionary.model.entity.Word;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[test suite] Word Repository")
@DataJpaTest
public class WordRepositoryTest {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @DisplayName("[test] Find word by content and language")
    @Test
    void givenContentAndLanguageName_returnMatchingWord() {
        Optional<Language> languageOpt = languageRepository.findByName("english");
        assertTrue(languageOpt.isPresent());
        Word word = wordRepository.save(new Word(UUID.randomUUID(), "test", languageOpt.get()));

        Optional<Word> result = wordRepository.findByContentAndLanguage_Name("test", "english");

        assert result.isPresent();
        assertEquals(result.get(), word);
    }

    @DisplayName("[test] Find all non-translated words")
    @Test
    void testFindAllByTranslationOfEmptyAndTranslationsEmpty() {
        Word word = new Word();
        word.setContent("test");
        wordRepository.save(word);

        Page<Word> result = wordRepository.findAllByTranslationOfEmptyAndTranslationsEmpty(PageRequest.of(0, 10));

        assert result.hasContent();
        assertThat(result.getContent().get(0).getContent(), is("test"));
    }

    @DisplayName("[test] Find words by language")
    @Test
    void testFindAllByLanguage_Name() {
        Optional<Language> lang = languageRepository.findByName("english");
        assertTrue(lang.isPresent());
        Word word = wordRepository.save(new Word(UUID.randomUUID(), "test", lang.get()));

        List<Word> result = wordRepository.findAllByLanguage_Name("english");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(word, result.get(0));
    }
}