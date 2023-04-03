package pl.mkramek.dictionary.model.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class WordRequest implements Serializable {
    @NonNull
    @NotBlank(message = "Content of the word is required")
    @Pattern(regexp = "^[^0-9\\d\\s]+$", message = "Word can only contain letters")
    private String content;
    @NonNull
    private LanguageRequest language;
}
