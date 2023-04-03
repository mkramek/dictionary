package pl.mkramek.dictionary.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.model.http.response.PhraseTranslationResponse;
import pl.mkramek.dictionary.model.http.response.StandardResponse;
import pl.mkramek.dictionary.model.http.response.WordTranslationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@DisplayName("[test suite] Translation Service")
@ExtendWith(MockitoExtension.class)
public class TranslationServiceTest {
    @Mock
    private WordService wordService;

    @Mock
    private LanguageService languageService;

    @InjectMocks
    private TranslationService translationService;

    @DisplayName("[test] Create translation from DTO")
    @Test
    void testCreateTranslationFromDTOs() {
        WordDTO translatable = new WordDTO(UUID.randomUUID(), "test", new LanguageDTO(UUID.randomUUID(), "English"));
        WordDTO translation = new WordDTO(UUID.randomUUID(), "prueba", new LanguageDTO(UUID.randomUUID(), "Spanish"));
        when(wordService.createWord(translatable)).thenReturn(Optional.of(translatable));
        when(wordService.createWord(translation)).thenReturn(Optional.of(translation));
        when(wordService.saveOrUpdate(translation)).thenReturn(translation);
        when(wordService.saveOrUpdate(translatable)).thenReturn(translatable);

        StandardResponse response = translationService.createTranslationFromDTOs(translatable, translation);

        assertNotNull(response);
        assertEquals(1, translatable.getTranslations().size());
        assertEquals(1, translation.getTranslationOf().size());
    }

    @DisplayName("[test] Partially translate a phrase")
    @Test
    void givenPhrase_returnPartialTranslation() {
        String phrase = "this is a test";
        String sourceLanguage = "English";
        String targetLanguage = "Spanish";
        LanguageDTO sourceLangDTO = new LanguageDTO(UUID.randomUUID(), sourceLanguage);
        LanguageDTO targetLangDTO = new LanguageDTO(UUID.randomUUID(), targetLanguage);
        WordDTO wordDTO = new WordDTO("test", sourceLangDTO);
        List<WordDTO> translationList = new ArrayList<>();
        WordDTO translation = new WordDTO("prueba", targetLangDTO);
        translationList.add(translation);
        wordDTO.setTranslations(translationList);
        when(languageService.getByName(sourceLanguage)).thenReturn(Optional.of(sourceLangDTO));
        when(languageService.getByName(targetLanguage)).thenReturn(Optional.of(targetLangDTO));
        WordDTO thisDTO = new WordDTO("this", sourceLangDTO);
        WordDTO isDTO = new WordDTO("is", sourceLangDTO);
        WordDTO aDTO = new WordDTO("a", sourceLangDTO);
        when(wordService.getByContent("this", sourceLanguage)).thenReturn(Optional.of(thisDTO));
        when(wordService.getByContent("is", sourceLanguage)).thenReturn(Optional.of(isDTO));
        when(wordService.getByContent("a", sourceLanguage)).thenReturn(Optional.of(aDTO));
        when(wordService.getByContent("test", sourceLanguage)).thenReturn(Optional.of(wordDTO));

        PhraseTranslationResponse response = translationService.translatePhrase(phrase, sourceLanguage, targetLanguage);

        assertNotNull(response);
        assertEquals(3, response.getUnknownWordsCount());
        assertEquals("this is a prueba", response.getTranslatedPhrase());
    }

    @DisplayName("[test] Translate a word")
    @Test
    void givenWord_returnTranslation() {
        String sourceLanguage = "English";
        String targetLanguage = "Spanish";
        LanguageDTO sourceLangDTO = new LanguageDTO(UUID.randomUUID(), sourceLanguage);
        LanguageDTO targetLangDTO = new LanguageDTO(UUID.randomUUID(), targetLanguage);
        WordDTO wordDTO = new WordDTO("test", sourceLangDTO);
        WordDTO translation = new WordDTO("prueba", targetLangDTO);
        List<WordDTO> translations = new ArrayList<>();
        translations.add(translation);
        wordDTO.setTranslations(translations);
        when(languageService.getByName(sourceLanguage)).thenReturn(Optional.of(sourceLangDTO));
        when(languageService.getByName(targetLanguage)).thenReturn(Optional.of(targetLangDTO));
        when(wordService.getByContent("test", sourceLanguage)).thenReturn(Optional.of(wordDTO));

        WordTranslationResponse response = translationService.translateWord("test", sourceLanguage, targetLanguage);

        assertNotNull(response);
        assertEquals("test", response.getWordToTranslate());
        assertEquals("prueba", response.getTranslation());
    }
}
