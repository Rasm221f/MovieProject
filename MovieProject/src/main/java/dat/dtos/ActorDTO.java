package dat.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dat.entities.Actor;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActorDTO {

    private Integer id;
    private String name;
    private String character;

    // Constructor to convert Actor entity to ActorDTO
    public ActorDTO(Actor actor) {
        this.id = actor.getId();
        this.name = actor.getName();
        this.character = actor.getCharacter();
    }
}
