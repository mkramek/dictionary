package pl.mkramek.dictionary.model.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "languages", schema = "dictionary")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Language {
    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column
    @JsonValue
    @NonNull
    private String name;
}
