package co.edu.uniandes.dse.ligaajedrez.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GameDetailDTO extends GameDTO {
    private List<PlayerDTO> players = new ArrayList<>();
    private List<MoveDTO> moves = new ArrayList<>();
    private List<CommentDTO> comments = new ArrayList<>();
}
