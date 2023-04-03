package pl.mkramek.dictionary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.mkramek.dictionary.model.entity.Word;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordRepository extends JpaRepository<Word, UUID> {
    Optional<Word> findByContentAndLanguage_Name(String content, String langName);
    Page<Word> findAllByTranslationOfEmptyAndTranslationsEmpty(Pageable pageable);
    List<Word> findAllByLanguage_Name(String name);
}
