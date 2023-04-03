package pl.mkramek.dictionary.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.service.WordService;
import pl.mkramek.dictionary.support.mapping.WordMapper;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("[test suite] Word Controller")
@WebMvcTest(WordController.class)
public class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordService service;

    @MockBean
    private WordMapper mapper;

    @DisplayName("[test] Get all words")
    @Test
    public void getAllWords_shouldReturnAllWords() throws Exception {
        List<WordDTO> wordList = Arrays.asList(
                new WordDTO(UUID.randomUUID(), "yes", new LanguageDTO(UUID.randomUUID(), "English")),
                new WordDTO(UUID.randomUUID(), "no", new LanguageDTO(UUID.randomUUID(), "English"))
        );

        Page<WordDTO> page = new PageImpl<>(wordList);

        given(service.getAllPaged(0, 10)).willReturn(page);

        mockMvc.perform(get("/api/v1/word"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(notNullValue())))
                .andExpect(jsonPath("$.content[0].content", is(notNullValue())))
                .andExpect(jsonPath("$.content[0].language", is(notNullValue())))
                .andExpect(jsonPath("$.content[1].id", is(notNullValue())))
                .andExpect(jsonPath("$.content[1].content", is(notNullValue())))
                .andExpect(jsonPath("$.content[1].language", is(notNullValue())));
    }

    @DisplayName("[test] Throw HTTP 404 on no words")
    @Test
    public void getAllWords_shouldThrow404IfNoWords() throws Exception {
        Page<WordDTO> page = new PageImpl<>(Collections.emptyList());

        given(service.getAllPaged(0, 10)).willReturn(page);

        mockMvc.perform(get("/api/v1/word"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[test] Get single word")
    @Test
    public void getWordById_shouldReturnWordIfIdExists() throws Exception {
        UUID id = UUID.randomUUID();
        WordDTO word = new WordDTO(id, "test", new LanguageDTO(UUID.randomUUID(), "English"));

        given(service.getById(id)).willReturn(Optional.of(word));

        mockMvc.perform(get("/api/v1/word/%s".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.content", is(notNullValue())))
                .andExpect(jsonPath("$.language", is(notNullValue())));
    }

    @DisplayName("[test] Throw HTTP 404 when no single word matches")
    @Test
    public void getWordById_shouldThrow404IfIdDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        given(service.getById(id)).willReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/word/%s".formatted(id)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[test] Throw HTTP 400 when invalid ID")
    @Test
    public void getWordById_shouldThrow400IfInvalidId() throws Exception {
        mockMvc.perform(get("/api/v1/word/invalid-id"))
                .andExpect(status().isBadRequest());
    }
}