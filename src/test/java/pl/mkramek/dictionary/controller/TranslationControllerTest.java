package pl.mkramek.dictionary.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.model.http.request.LanguageRequest;
import pl.mkramek.dictionary.model.http.request.WordRequest;
import pl.mkramek.dictionary.model.http.response.PhraseTranslationResponse;
import pl.mkramek.dictionary.model.http.response.StandardResponse;
import pl.mkramek.dictionary.model.http.response.WordTranslationResponse;
import pl.mkramek.dictionary.repository.WordRepository;
import pl.mkramek.dictionary.service.LanguageService;
import pl.mkramek.dictionary.service.TranslationService;
import pl.mkramek.dictionary.service.WordService;
import pl.mkramek.dictionary.support.mapping.WordMapper;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[test suite] Translation Controller")
@WebMvcTest(TranslationController.class)
public class TranslationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private WordService wordService;

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private LanguageService languageService;

    @MockBean
    private TranslationService service;

    @MockBean
    private WordMapper mapper;

    @BeforeEach
    void setUp() {
        wordService = new WordService(wordRepository, languageService, mapper);
    }

    @DisplayName("[test] Translate word")
    @Test
    public void givenWord_whenRequestedForTranslation_returnTranslation() throws Exception {
        String word = "hello";
        String sourceLang = "en";
        String targetLang = "es";

        WordTranslationResponse translationResponse = new WordTranslationResponse();
        translationResponse.setWordToTranslate(word);
        translationResponse.setTranslation("hola");
        when(service.translateWord(word, sourceLang, targetLang)).thenReturn(translationResponse);

        mockMvc.perform(get("/api/v1/translation/word")
                        .param("word", word)
                        .param("source_lang", sourceLang)
                        .param("target_lang", targetLang))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.translation", is("hola")));
    }

    @DisplayName("[test] Translate phrase")
    @Test
    public void givenPhrase_whenRequestedForTranslation_returnTranslation() throws Exception {
        String phrase = "hello world";
        String sourceLang = "en";
        String targetLang = "es";

        PhraseTranslationResponse translationResponse = new PhraseTranslationResponse();
        translationResponse.setPhraseToTranslate(phrase);
        translationResponse.setTranslatedPhrase("hola mundo");
        when(service.translatePhrase(phrase, sourceLang, targetLang)).thenReturn(translationResponse);

        mockMvc.perform(get("/api/v1/translation/phrase")
                        .param("phrase", phrase)
                        .param("source_lang", sourceLang)
                        .param("target_lang", targetLang))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.translatedPhrase", is("hola mundo")));
    }

    @DisplayName("[test] Find or create translation")
    @Test
    public void givenWords_findOrCreateTranslation() throws Exception {

        WordRequest translatable = new WordRequest("hello", new LanguageRequest("English"));
        WordRequest translation = new WordRequest("hola", new LanguageRequest("Spanish"));
        WordDTO translationDTO = new WordDTO(UUID.randomUUID(), "hola", new LanguageDTO(UUID.randomUUID(), "Spanish"));
        WordDTO translatableDTO = new WordDTO(UUID.randomUUID(), "hello", new LanguageDTO(UUID.randomUUID(), "English"));

        when(mapper.toDTO(translation)).thenReturn(translationDTO);
        when(mapper.toDTO(translatable)).thenReturn(translatableDTO);

        StandardResponse translationResponse = new StandardResponse("Translation created successfully");
        when(service.createTranslationFromDTOs(translatableDTO, translationDTO)).thenReturn(translationResponse);

        mockMvc.perform(post("/api/v1/translation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"translatable\": {\"language\": \"English\", \"content\": \"hello\"}, \"translation\": {\"language\": \"Spanish\", \"content\": \"hola\"}}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/translation"))
                .andExpect(jsonPath("$.message", is("Translation created successfully")));
    }
}