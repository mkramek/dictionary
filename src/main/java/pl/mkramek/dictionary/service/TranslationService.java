package pl.mkramek.dictionary.service;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.model.http.response.PhraseTranslationResponse;
import pl.mkramek.dictionary.model.http.response.StandardResponse;
import pl.mkramek.dictionary.model.http.response.WordTranslationResponse;
import pl.mkramek.dictionary.support.exception.InvalidTranslationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TranslationService {
    private final WordService wordService;
    private final LanguageService languageService;

    public TranslationService(WordService wordService, LanguageService languageService) {
        this.wordService = wordService;
        this.languageService = languageService;
    }

    @Transactional
    public StandardResponse createTranslationFromDTOs(WordDTO translatable, WordDTO translation) throws DataAccessException {
        Optional<WordDTO> persistedTranslatable = wordService.createWord(translatable);
        Optional<WordDTO> persistedTranslation = wordService.createWord(translation);
        if (persistedTranslatable.isPresent() && persistedTranslation.isPresent()) {
            persistedTranslation.get().getTranslationOf().add(persistedTranslatable.get());
            persistedTranslatable.get().getTranslations().add(persistedTranslation.get());
            wordService.saveOrUpdate(persistedTranslation.get());
            wordService.saveOrUpdate(persistedTranslatable.get());
        } else {
            throw new InvalidTranslationException("Unexpected error while saving the translation");
        }
        return new StandardResponse("Successfully added the translation");
    }

    private Optional<String> getTranslationForWord(String word, String sourceLanguage, String targetLanguage) throws IllegalArgumentException, DataAccessException {
        if (word.matches("\\W")) {
            throw new IllegalArgumentException("Invalid word; dictionary doesn't support non-word characters");
        }
        Optional<LanguageDTO> srcLang = languageService.getByName(sourceLanguage);
        Optional<LanguageDTO> tgtLang = languageService.getByName(targetLanguage);
        if (srcLang.isEmpty() || tgtLang.isEmpty()) {
            throw new IllegalArgumentException("Unsupported language");
        }
        String result = wordService.getByContent(word, srcLang.get().getName()).map(foundDTO -> {
            List<WordDTO> matches = foundDTO.getTranslations();
            matches.addAll(foundDTO.getTranslationOf());
            return matches.stream()
                    .filter(elem -> elem.getLanguage().getName().equals(tgtLang.get().getName()))
                    .findFirst()
                    .map(WordDTO::getContent)
                    .orElse(null);
        }).orElse(null);
        return Optional.ofNullable(result);
    }

    public PhraseTranslationResponse translatePhrase(String phrase, String sourceLanguage, String targetLanguage) throws IllegalArgumentException, DataAccessException {
        List<String> words = Arrays.stream(phrase.split(" ")).toList();
        PhraseTranslationResponse response = new PhraseTranslationResponse();
        response.setPhraseToTranslate(phrase);
        AtomicInteger unknownWordCount = new AtomicInteger(0);
        List<String> translated = words.stream().map(word -> {
            Optional<String> translation = getTranslationForWord(word, sourceLanguage, targetLanguage);
            return translation.orElseGet(() -> {
                unknownWordCount.incrementAndGet();
                wordService.createWord(word, sourceLanguage);
                return word;
            });
        }).toList();
        response.setTranslatedPhrase(String.join(" ", translated));
        response.setUnknownWordsCount(unknownWordCount.get());
        return response;
    }

    public WordTranslationResponse translateWord(String word, String sourceLanguage, String targetLanguage) throws InvalidTranslationException, DataAccessException {
        WordTranslationResponse response = new WordTranslationResponse();
        response.setWordToTranslate(word);
        Optional<String> translation = getTranslationForWord(word, sourceLanguage, targetLanguage);
        String translated = translation.orElseThrow(() -> new InvalidTranslationException("No translation for specified word"));
        response.setTranslation(translated);
        return response;
    }

    public Page<WordDTO> getUntranslatedPaged(int page, int perPage) {
        return wordService.getUntranslatedPaged(page, perPage);
    }
}
