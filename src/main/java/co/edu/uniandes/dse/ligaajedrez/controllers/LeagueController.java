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

import co.edu.uniandes.dse.ligaajedrez.dto.LeagueDTO;
import co.edu.uniandes.dse.ligaajedrez.dto.LeagueDetailDTO;
import co.edu.uniandes.dse.ligaajedrez.entities.LeagueEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.services.LeagueService;

@RestController
@RequestMapping("/leagues")
public class LeagueController {

    @Autowired
    private LeagueService leagueService;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<LeagueDetailDTO> findAll() {
        List<LeagueEntity> leagues = leagueService.getLeagues();
        return modelMapper.map(leagues, new TypeToken<List<LeagueDetailDTO>>(){}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public LeagueDetailDTO findOne(@PathVariable("id") Long id) throws IllegalOperationException, EntityNotFoundException {
        LeagueEntity leagueEntity = leagueService.getLeague(id);
        return modelMapper.map(leagueEntity, LeagueDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public LeagueDTO create(@RequestBody LeagueDTO leagueDTO) throws IllegalOperationException {
        LeagueEntity leagueEntity = leagueService.createLeague(modelMapper.map(leagueDTO, LeagueEntity.class));
        return modelMapper.map(leagueEntity, LeagueDTO.class);
    }

    @PostMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public LeagueDTO update(@PathVariable("id") Long id, @RequestBody LeagueDTO leagueDTO) throws IllegalOperationException, EntityNotFoundException {
        LeagueEntity leagueEntity = leagueService.updateLeague(id, modelMapper.map(leagueDTO, LeagueEntity.class));
        return modelMapper.map(leagueEntity, LeagueDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws IllegalOperationException, EntityNotFoundException {
        leagueService.deleteLeague(id);
    }
}
