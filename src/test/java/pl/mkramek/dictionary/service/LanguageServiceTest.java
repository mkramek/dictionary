package pl.mkramek.dictionary.service;

import org.junit.jupiter.api.*;
import org.mockito.*;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.entity.Language;
import pl.mkramek.dictionary.repository.LanguageRepository;
import pl.mkramek.dictionary.support.mapping.LanguageMapper;

import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[test suite] Language Service")
class LanguageServiceTest {
    private LanguageService languageService;
    private LanguageRepository languageRepository;
    private LanguageMapper languageMapper;

    @BeforeEach
    void setUp() {
        languageRepository = Mockito.mock(LanguageRepository.class);
        languageMapper = Mockito.mock(LanguageMapper.class);
        languageService = new LanguageService(languageRepository, languageMapper);
    }

    @DisplayName("[test] Get language by name")
    @Test
    void getByName_WhenLanguageExists_ReturnsLanguageDTO() {
        String languageName = "English";
        Language language = new Language(UUID.randomUUID(), languageName);
        LanguageDTO expectedDTO = new LanguageDTO(language.getId(), language.getName());

        when(languageRepository.findByName(languageName)).thenReturn(Optional.of(language));
        when(languageMapper.toDTO(language)).thenReturn(expectedDTO);

        Optional<LanguageDTO> result = languageService.getByName(languageName);

        assertTrue(result.isPresent());
        assertEquals(expectedDTO, result.get());

        verify(languageRepository).findByName(languageName);
        verify(languageMapper).toDTO(language);
    }

    @DisplayName("[test] Get empty Optional when language doesn't exist")
    @Test
    void getByName_WhenLanguageDoesNotExist_ReturnsEmpty() {
        String languageName = "English";

        when(languageRepository.findByName(languageName)).thenReturn(Optional.empty());

        Optional<LanguageDTO> result = languageService.getByName(languageName);

        assertTrue(result.isEmpty());

        verify(languageRepository).findByName(languageName);
        verifyNoInteractions(languageMapper);
    }

    @DisplayName("[test] Create language if doesn't exist")
    @Test
    void createLanguage_WhenLanguageDoesNotExist_ReturnsCreatedLanguageDTO() {
        String languageName = "English";
        LanguageDTO inputDTO = new LanguageDTO(null, languageName);
        Language expectedLanguage = new Language(UUID.randomUUID(), languageName);
        LanguageDTO expectedDTO = new LanguageDTO(expectedLanguage.getId(), expectedLanguage.getName());

        when(languageRepository.findByName(languageName)).thenReturn(Optional.empty());
        when(languageMapper.toEntity(inputDTO)).thenReturn(expectedLanguage);
        when(languageRepository.save(expectedLanguage)).thenReturn(expectedLanguage);
        when(languageMapper.toDTO(expectedLanguage)).thenReturn(expectedDTO);

        Optional<LanguageDTO> result = languageService.createLanguage(inputDTO);

        assertTrue(result.isPresent());
        assertEquals(expectedDTO, result.get());

        verify(languageRepository).findByName(languageName);
        verify(languageMapper).toEntity(inputDTO);
        verify(languageRepository).save(expectedLanguage);
        verify(languageMapper).toDTO(expectedLanguage);
    }

    @DisplayName("[test] Create language, but return existing one if matches")
    @Test
    void createLanguage_WhenLanguageExists_ReturnsExistingLanguageDTO() {
        String languageName = "English";
        LanguageDTO inputDTO = new LanguageDTO(null, languageName);
        Language existingLanguage = new Language(UUID.randomUUID(), languageName);
        LanguageDTO expectedDTO = new LanguageDTO(existingLanguage.getId(), existingLanguage.getName());

        when(languageRepository.findByName(languageName)).thenReturn(Optional.of(existingLanguage));
        when(languageMapper.toDTO(existingLanguage)).thenReturn(expectedDTO);

        Optional<LanguageDTO> result = languageService.createLanguage(inputDTO);

        assertTrue(result.isPresent());
        assertEquals(expectedDTO, result.get());

        verify(languageRepository).findByName(languageName);
        verify(languageMapper).toDTO(existingLanguage);
        verifyNoMoreInteractions(languageRepository, languageMapper);
    }
}