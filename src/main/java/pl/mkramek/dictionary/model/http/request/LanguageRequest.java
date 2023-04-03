package pl.mkramek.dictionary.model.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class LanguageRequest implements Serializable {
    @NotBlank(message = "Language name is required")
    @Pattern(regexp = "^[^0-9\\d\\s]+$", message = "Language name has to be a word")
    private String name;
}
