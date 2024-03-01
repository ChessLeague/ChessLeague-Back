package co.edu.uniandes.dse.ligaajedrez.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniandes.dse.ligaajedrez.dto.GameDTO;
import co.edu.uniandes.dse.ligaajedrez.dto.GameDetailDTO;
import co.edu.uniandes.dse.ligaajedrez.entities.GameEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.services.GameService;

@RestController
@RequestMapping("/games")
public class GameController {
    
    @Autowired
    private GameService gameService;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<GameDetailDTO> findAll(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "4") int size) {
        List<GameEntity> games = gameService.getGames(PageRequest.of(page, size));
        return modelMapper.map(games, new TypeToken<List<GameDetailDTO>>(){}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public GameDetailDTO findOne(@PathVariable("id") Long id) throws IllegalOperationException, EntityNotFoundException {
        GameEntity gameEntity = gameService.getGame(id);
        return modelMapper.map(gameEntity, GameDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public GameDTO create(@RequestBody GameDTO gameDTO) throws IllegalOperationException {
        GameEntity gameEntity = gameService.createGame(modelMapper.map(gameDTO, GameEntity.class));
        return modelMapper.map(gameEntity, GameDTO.class);
    }

    @PostMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public GameDTO update(@PathVariable("id") Long id, @RequestBody GameDTO gameDTO) throws IllegalOperationException, EntityNotFoundException {
        GameEntity gameEntity = gameService.updateGame(id, modelMapper.map(gameDTO, GameEntity.class));
        return modelMapper.map(gameEntity, GameDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws IllegalOperationException, EntityNotFoundException {
        gameService.deleteGame(id);
    }

    @GetMapping("/count")
    public long getGameCount() {
        return gameService.getTotalGameCount();
    }

}
