package pl.mkramek.dictionary.service;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.mkramek.dictionary.model.dto.LanguageDTO;
import pl.mkramek.dictionary.model.entity.Language;
import pl.mkramek.dictionary.repository.LanguageRepository;
import pl.mkramek.dictionary.support.mapping.LanguageMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LanguageService {
    private final LanguageRepository repository;
    private final LanguageMapper mapper;

    public LanguageService(LanguageRepository repository, LanguageMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Optional<LanguageDTO> getByName(String name) {
        return repository.findByName(name).map(mapper::toDTO);
    }

    public List<LanguageDTO> getAll() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public Page<LanguageDTO> getAllPaged(int page, int pageSize) {
        return repository.findAll(PageRequest.of(page, pageSize)).map(mapper::toDTO);
    }

    public Optional<LanguageDTO> createLanguage(LanguageDTO lang) throws DataAccessException {
        Optional<LanguageDTO> existingLang = getByName(lang.getName());
        if (existingLang.isPresent()) {
            return existingLang;
        }
        lang.setId(UUID.randomUUID());
        Language mappedLang = mapper.toEntity(lang);
        var saved = repository.save(mappedLang);
        return Optional.of(mapper.toDTO(saved));
    }
}
