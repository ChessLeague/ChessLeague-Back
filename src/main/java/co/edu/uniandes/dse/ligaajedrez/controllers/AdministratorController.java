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

import co.edu.uniandes.dse.ligaajedrez.dto.AdministratorDTO;
import co.edu.uniandes.dse.ligaajedrez.dto.AdministratorDetailDTO;
import co.edu.uniandes.dse.ligaajedrez.entities.AdministratorEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.ligaajedrez.services.AdministratorService;

@RestController
@RequestMapping("/administrators")
public class AdministratorController {

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private ModelMapper modelMapper = new ModelMapper();

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<AdministratorDetailDTO> findAll() {
        List<AdministratorEntity> administrators = administratorService.getAdministrators();
        return modelMapper.map(administrators, new TypeToken<List<AdministratorDetailDTO>>(){}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public AdministratorDTO findOne(@PathVariable("id") Long id) throws IllegalOperationException, EntityNotFoundException {
        AdministratorEntity administratorEntity = administratorService.getAdministrator(id);
        return modelMapper.map(administratorEntity, AdministratorDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public AdministratorDTO create(@RequestBody AdministratorDTO administratorDTO) throws IllegalOperationException {
        AdministratorEntity administratorEntity = administratorService.createAdministrator(modelMapper.map(administratorDTO, AdministratorEntity.class));
        return modelMapper.map(administratorEntity, AdministratorDTO.class);
    }

    @PostMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public AdministratorDTO update(@PathVariable("id") Long id, @RequestBody AdministratorDTO administratorDTO) throws IllegalOperationException, EntityNotFoundException {
        AdministratorEntity administratorEntity = administratorService.updateAdministrator(id, modelMapper.map(administratorDTO, AdministratorEntity.class));
        return modelMapper.map(administratorEntity, AdministratorDTO.class);
    }

    @DeleteMapping(value = "/id")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws IllegalOperationException, EntityNotFoundException {
        administratorService.deleteAdministrator(id);
    }
    
}

