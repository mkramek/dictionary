package pl.mkramek.dictionary.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class WordDTO implements Serializable {
    private UUID id;

    @NonNull
    private String content;

    @NonNull
    private LanguageDTO language;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<WordDTO> translations = new LinkedList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<WordDTO> translationOf = new LinkedList<>();

    public WordDTO(UUID id, @NonNull String content, @NonNull LanguageDTO language) {
        this.id = id;
        this.content = content;
        this.language = language;
    }
}
