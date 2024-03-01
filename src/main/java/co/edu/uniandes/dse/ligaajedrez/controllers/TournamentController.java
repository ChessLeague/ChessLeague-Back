package co.edu.uniandes.dse.ligaajedrez.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniandes.dse.ligaajedrez.dto.TournamentDTO;
import co.edu.uniandes.dse.ligaajedrez.dto.TournamentDetailDTO;
import co.edu.uniandes.dse.ligaajedrez.entities.TournamentEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.services.TournamentService;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<TournamentDetailDTO> findAll() {
        List<TournamentEntity> tournaments = tournamentService.getTournaments();
        return modelMapper.map(tournaments, new TypeToken<List<TournamentDetailDTO>>(){}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public TournamentDetailDTO findOne(@PathVariable("id") Long id) throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity tournamentEntity = tournamentService.getTournament(id);
        return modelMapper.map(tournamentEntity, TournamentDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TournamentDTO create(@RequestBody TournamentDTO tournamentDTO) throws IllegalOperationException {
        TournamentEntity tournamentEntity = tournamentService.createTournament(modelMapper.map(tournamentDTO, TournamentEntity.class));
        return modelMapper.map(tournamentEntity, TournamentDTO.class);
    }

    @PostMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public TournamentDTO update(@PathVariable("id") Long id, @RequestBody TournamentDTO tournamentDTO) throws IllegalOperationException, EntityNotFoundException {
        TournamentEntity tournamentEntity = tournamentService.updateTournament(id, modelMapper.map(tournamentDTO, TournamentEntity.class));
        return modelMapper.map(tournamentEntity, TournamentDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws IllegalOperationException, EntityNotFoundException {
        tournamentService.deleteTournament(id);
    }
}
