package pl.mkramek.dictionary.support.mapping;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.dto.WordDTO;
import pl.mkramek.dictionary.model.entity.Language;
import pl.mkramek.dictionary.model.entity.Word;
import pl.mkramek.dictionary.model.http.request.WordRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
public class WordMapper {
    @Transactional
    public WordDTO toDTO(Word word) {
        WordDTO dto = prepareDTO(word);
        List<WordDTO> translationsDTO = new LinkedList<>();
        List<WordDTO> translatablesDTO = new LinkedList<>();
        for (Word translation : word.getTranslations()) {
            translationsDTO.add(prepareDTO(translation));
        }
        for (Word translatable : word.getTranslationOf()) {
            translatablesDTO.add(prepareDTO(translatable));
        }
        dto.setTranslations(translationsDTO);
        dto.setTranslationOf(translatablesDTO);
        return dto;
    }

    public WordDTO toDTO(WordRequest request) {
        return prepareDTO(request);
    }

    @Transactional
    public Word toEntity(WordDTO wordDTO) {
        Word entity = prepareEntity(wordDTO);
        List<Word> translations = new LinkedList<>();
        List<Word> translatables = new LinkedList<>();
        for (WordDTO translation : wordDTO.getTranslations()) {
            translations.add(prepareEntity(translation));
        }
        for (WordDTO translatable : wordDTO.getTranslationOf()) {
            translatables.add(prepareEntity(translatable));
        }
        entity.setTranslations(translations);
        entity.setTranslationOf(translatables);
        return entity;
    }

    private WordDTO prepareDTO(Word word) {
        WordDTO dto = new WordDTO();
        dto.setId(word.getId());
        dto.setContent(word.getContent());
        LanguageDTO langDTO = new LanguageDTO(word.getLanguage().getId(), word.getLanguage().getName());
        dto.setLanguage(langDTO);
        return dto;
    }

    private WordDTO prepareDTO(WordRequest request) {
        WordDTO dto = new WordDTO();
        dto.setId(UUID.randomUUID());
        dto.setContent(request.getContent());
        LanguageDTO langDTO = new LanguageDTO(UUID.randomUUID(), request.getLanguage().getName());
        dto.setLanguage(langDTO);
        return dto;
    }

    private Word prepareEntity(WordDTO dto) {
        Word entity = new Word();
        entity.setId(dto.getId());
        entity.setContent(dto.getContent());
        Language lang = new Language();
        lang.setId(dto.getLanguage().getId());
        lang.setName(dto.getLanguage().getName());
        entity.setLanguage(lang);
        return entity;
    }
}
