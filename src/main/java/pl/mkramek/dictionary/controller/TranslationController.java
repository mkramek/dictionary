package pl.mkramek.dictionary.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.model.http.request.TranslationRequest;
import pl.mkramek.dictionary.model.http.response.PhraseTranslationResponse;
import pl.mkramek.dictionary.model.http.response.StandardResponse;
import pl.mkramek.dictionary.model.http.response.WordTranslationResponse;
import pl.mkramek.dictionary.service.TranslationService;
import pl.mkramek.dictionary.support.exception.InvalidTranslationException;
import pl.mkramek.dictionary.support.mapping.WordMapper;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
public class TranslationController {
    private final TranslationService service;
    private final WordMapper mapper;
    private static final int DEFAULT_PAGE = 0;

    @Value("${application.default_page_size}")
    private int defaultPageSize;

    public TranslationController(TranslationService service, WordMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/translation/word")
    public ResponseEntity<WordTranslationResponse> translateWord(@RequestParam("word") String word, @RequestParam("source_lang") String sourceLanguage, @RequestParam("target_lang") String targetLanguage) {
        try {
            return ResponseEntity.ok(service.translateWord(word, sourceLanguage, targetLanguage));
        } catch (InvalidTranslationException nte) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No translations found");
        } catch (DataAccessException dae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while accessing the data", dae);
        }
    }

    @GetMapping("/translation/phrase")
    public ResponseEntity<PhraseTranslationResponse> translatePhrase(@RequestParam("phrase") String phrase, @RequestParam("source_lang") String sourceLanguage, @RequestParam("target_lang") String targetLanguage) {
        try {
            return ResponseEntity.ok(service.translatePhrase(phrase, sourceLanguage, targetLanguage));
        } catch (DataAccessException dae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while accessing the data", dae);
        }
    }

    @GetMapping("/translation/empty")
    public ResponseEntity<Page<WordDTO>> getWordsWithoutTranslation(
            @Nullable
            @RequestParam(name = "per_page", defaultValue = "10")
            @Pattern(regexp = "^[0-9]*$")
            String perPage,
            @Nullable
            @RequestParam(name = "page", defaultValue = "0")
            @Pattern(regexp = "^[0-9]*$")
            String page
    ) {
        try {
            int pageNo = page == null ? DEFAULT_PAGE : Integer.parseInt(page);
            int pageSize = perPage == null ? defaultPageSize : Integer.parseInt(perPage);

            var words = service.getUntranslatedPaged(pageNo, pageSize);
            return ResponseEntity.ok(words);
        } catch (DataAccessException dae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while accessing the data", dae);
        }
    }

    @PostMapping("/translation")
    public ResponseEntity<StandardResponse> createOrGetTranslation(@Valid @RequestBody TranslationRequest newTranslation) {
        try {
            var translationWord = newTranslation.getTranslation();
            var translatableWord = newTranslation.getTranslatable();
            var translationDTO = mapper.toDTO(translationWord);
            var translatableDTO = mapper.toDTO(translatableWord);
            var translationResponse = service.createTranslationFromDTOs(translatableDTO, translationDTO);
            return ResponseEntity.created(new URI("/api/v1/translation")).body(translationResponse);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while creating translation", e);
        }
    }
}
