package pl.mkramek.dictionary.model.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LanguageDTO implements Serializable {

    private UUID id;

    @NonNull
    @NotBlank(message = "Language name is required")
    @JsonValue
    private String name;
}
