package pl.mkramek.dictionary.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.model.entity.Word;
import pl.mkramek.dictionary.repository.WordRepository;
import pl.mkramek.dictionary.support.mapping.WordMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class WordService {
    private final WordRepository repository;
    private final LanguageService langService;
    private final WordMapper mapper;

    public WordService(WordRepository repository, LanguageService langService, WordMapper mapper) {
        this.repository = repository;
        this.langService = langService;
        this.mapper = mapper;
    }

    public Optional<WordDTO> getByContent(String content, String languageName) {
        Optional<Word> found = repository.findByContentAndLanguage_Name(content, languageName);
        return found.map(mapper::toDTO);
    }

    @Transactional
    public Page<WordDTO> getAllPaged(int page, int perPage) {
        Page<Word> found = repository.findAll(PageRequest.of(page, perPage));
        return found.map(mapper::toDTO);
    }

    public Page<WordDTO> getUntranslatedPaged(int page, int perPage) {
        Page<Word> found = repository.findAllByTranslationOfEmptyAndTranslationsEmpty(PageRequest.of(page, perPage));
        return found.map(mapper::toDTO);
    }

    public List<WordDTO> getAll() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public List<WordDTO> getAllByLanguage (String languageName) {
        return repository.findAllByLanguage_Name(languageName).stream().map(mapper::toDTO).toList();
    }

    public Optional<WordDTO> getById(UUID id) {
        Optional<Word> found = repository.findById(id);
        return found.map(mapper::toDTO);
    }

    public Optional<WordDTO> createWord(String content, String language) throws DataAccessException {
        Optional<LanguageDTO> lang = langService.getByName(language);
        return lang.map(lng -> createWord(content, lng)).orElseThrow(() -> new IllegalArgumentException("Unsupported language"));
    }

    public Optional<WordDTO> createWord(String content, LanguageDTO language) throws DataAccessException {
        Optional<LanguageDTO> lang = langService.getByName(language.getName());
        WordDTO dto = new WordDTO();
        dto.setContent(content);
        if (lang.isPresent()) {
            dto.setLanguage(lang.get());
            return createWord(dto);
        }
        dto.setLanguage(language);
        return createWord(dto);
    }

    public Optional<WordDTO> createWord(WordDTO word) throws DataAccessException {
        Optional<WordDTO> existingWord = getByContent(word.getContent(), word.getLanguage().getName());
        if (existingWord.isPresent()) {
            return existingWord;
        }
        word.setId(UUID.randomUUID());
        Optional<LanguageDTO> lang = langService.getByName(word.getLanguage().getName());
        lang.ifPresent(word::setLanguage);
        Word mappedWord = mapper.toEntity(word);
        var saved = repository.save(mappedWord);
        return Optional.of(mapper.toDTO(saved));
    }

    public Optional<WordDTO> updateWord(UUID wordId, WordDTO word) throws DataAccessException {
        word.setId(UUID.randomUUID());
        var foundWord = repository.findById(wordId);
        if (foundWord.isPresent()) {
            var mapped = mapper.toEntity(word);
            var target = foundWord.get();
            target.setContent(mapped.getContent());
            target.setLanguage(mapped.getLanguage());
            var saved = repository.save(target);
            return Optional.of(mapper.toDTO(saved));
        } else {
            return Optional.empty();
        }
    }

    public WordDTO saveOrUpdate(WordDTO dto) throws DataAccessException {
        if (dto.getId() != null) {
            Optional<WordDTO> foundOptional = getById(dto.getId());
            if (foundOptional.isPresent()) {
                WordDTO found = foundOptional.get();
                found.setContent(dto.getContent());
                found.setLanguage(dto.getLanguage());
                found.getTranslationOf().addAll(dto.getTranslationOf());
                found.getTranslations().addAll(dto.getTranslations());
                var foundEntity = mapper.toEntity(found);
                var result = repository.save(foundEntity);
                return mapper.toDTO(result);
            }
        } else {
            dto.setId(UUID.randomUUID());
        }
        var entity = mapper.toEntity(dto);
        var result = repository.save(entity);
        return mapper.toDTO(result);
    }
}
