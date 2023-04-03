package pl.mkramek.dictionary.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.model.http.request.WordRequest;
import pl.mkramek.dictionary.service.WordService;
import pl.mkramek.dictionary.support.mapping.WordMapper;

import java.util.Optional;
import java.util.UUID;

@RequestMapping("/api/v1")
@RestController
@Slf4j
public class WordController {
    private final WordService service;
    private final WordMapper mapper;
    private static final int DEFAULT_PAGE = 1;

    @Value("${application.default_page_size}")
    private int defaultPageSize;

    public WordController(WordService service, WordMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/word")
    public ResponseEntity<Page<WordDTO>> getAllWords(
            @Nullable
            @RequestParam(name = "per_page", defaultValue = "10")
            @Pattern(regexp = "^[0-9]*$")
            String perPage,
            @Nullable
            @RequestParam(name = "page", defaultValue = "0")
            @Pattern(regexp = "^[0-9]*$")
            String page
    ) {
        int pageNo = page == null ? DEFAULT_PAGE : Integer.parseInt(page);
        int pageSize = perPage == null ? defaultPageSize : Integer.parseInt(perPage);

        var words = service.getAllPaged(pageNo, pageSize);
        if (words.hasContent()) {
            return ResponseEntity.ok(words);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No words found");
        }
    }

    @GetMapping("/word/{id}")
    public ResponseEntity<WordDTO> getWordById(@PathVariable("id") String id) {
        try {
            var wordId = UUID.fromString(id);
            var word = service.getById(wordId);
            return word
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found"));
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID for word", iae);
        }
    }

    @PostMapping("/word")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<WordDTO> addWord(@Valid @RequestBody WordRequest newWord) {
        var existingWord = service.getByContent(newWord.getContent(), newWord.getLanguage().getName());
        if (existingWord.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Word already exists");
        } else {
            var mapped = mapper.toDTO(newWord);
            Optional<WordDTO> translation = service.createWord(mapped);
            return translation
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not create the word"));
        }
    }

    @PutMapping("/word/{id}")
    public ResponseEntity<WordDTO> updateWord(@PathVariable String id, @Valid @RequestBody WordRequest newWord) {
        try {
            var wordId = UUID.fromString(id);
            var mapped = mapper.toDTO(newWord);
            var word = service.updateWord(wordId, mapped);
            return word
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not update the word"));
        } catch (IllegalArgumentException iae) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID for word", iae);
        }
    }
}
