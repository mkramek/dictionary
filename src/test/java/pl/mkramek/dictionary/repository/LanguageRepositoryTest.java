package pl.mkramek.dictionary.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.mkramek.dictionary.model.entity.Language;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[test suite] Language Repository")
@DataJpaTest
class LanguageRepositoryTest {

    @Autowired
    private LanguageRepository repository;

    @DisplayName("[test] Find language by name")
    @Test
    void testFindByName() {
        Optional<Language> result = repository.findByName("english");

        assertTrue(result.isPresent());
        assertEquals("english", result.get().getName());
    }

    @DisplayName("[test] Find language by unknown name")
    @Test
    void testFindByNameUnknown() {
        Optional<Language> result = repository.findByName("Unknown");
        assertTrue(result.isEmpty());
    }
}