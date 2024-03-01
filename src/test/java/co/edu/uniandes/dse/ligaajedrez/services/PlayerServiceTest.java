package co.edu.uniandes.dse.ligaajedrez.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.ligaajedrez.entities.PlayerEntity;
import co.edu.uniandes.dse.ligaajedrez.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.ligaajedrez.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(PlayerService.class)
public class PlayerServiceTest {
    @Autowired
    private PlayerService playerService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<PlayerEntity> playerList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PlayerEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            PlayerEntity playerEntity = factory.manufacturePojo(PlayerEntity.class);
            playerEntity.setName("Test Name");
            playerEntity.setPhotoURL("Test Photo URL");
            playerEntity.setBirthDate(new Date());
            playerEntity.setBirthPlace("Test Birth Place");
            playerEntity.setEloRating(0);
            playerEntity.setUsername("Username" + i);
            playerEntity.setPassword("Test Password");
            entityManager.persist(playerEntity);
            playerList.add(playerEntity);
        }
    }

    @Test
    void testCreatePlayer() throws IllegalOperationException {
        PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
        newPlayer.setId(123L);
        newPlayer.setName("Test Name");
        newPlayer.setPhotoURL("Test Photo URL");
        newPlayer.setBirthDate(new Date());
        newPlayer.setBirthPlace("Test Birth Place");
        newPlayer.setEloRating(0);
        newPlayer.setUsername("Test Username");
        newPlayer.setPassword("Test Password");
        PlayerEntity playerEntity = playerService.createPlayer(newPlayer);
        assertNotNull(playerEntity);
        PlayerEntity entity = entityManager.find(PlayerEntity.class, playerEntity.getId());
        assertEquals(playerEntity.getId(), entity.getId());
        assertEquals(playerEntity.getName(), entity.getName());
        assertEquals(playerEntity.getPhotoURL(), entity.getPhotoURL());
        assertEquals(playerEntity.getBirthDate(), entity.getBirthDate());
        assertEquals(playerEntity.getBirthPlace(), entity.getBirthPlace());
        assertEquals(playerEntity.getEloRating(), entity.getEloRating());
        assertEquals(playerEntity.getUsername(), entity.getUsername());
        assertEquals(playerEntity.getPassword(), entity.getPassword());
    }

    @Test
    void testCreateInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            newPlayer.setId(null);
            newPlayer.setName("Test Name");
            newPlayer.setPhotoURL("Test Photo URL");
            newPlayer.setUsername("Test Username");
            newPlayer.setPassword("Test Password");
            playerService.createPlayer(newPlayer);
        });
    }

    @Test
    void testCreateInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            newPlayer.setId(0L);
            newPlayer.setName("Test Name");
            newPlayer.setPhotoURL("Test Photo URL");
            newPlayer.setUsername("Test Username");
            newPlayer.setPassword("Test Password");
            playerService.createPlayer(newPlayer);
        });
    }

    @Test
    void testCreateInvalidPlayer3() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            newPlayer.setId(123L);
            newPlayer.setName(null);
            newPlayer.setPhotoURL("Test Photo URL");
            newPlayer.setUsername("Test Username");
            newPlayer.setPassword("Test Password");
            playerService.createPlayer(newPlayer);
        });
    }

    @Test
    void testCreateInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            newPlayer.setId(123L);
            newPlayer.setName("Test Name");
            newPlayer.setPhotoURL(null);
            newPlayer.setUsername("Test Username");
            newPlayer.setPassword("Test Password");
            playerService.createPlayer(newPlayer);
        });
    }

    @Test
    void testCreateInvalidPlayer5() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            newPlayer.setId(123L);
            newPlayer.setName("Test Name");
            newPlayer.setPhotoURL("Test Photo URL");
            newPlayer.setUsername(null);
            newPlayer.setPassword("Test Password");
            playerService.createPlayer(newPlayer);
        });
    }

    @Test
    void testCreateInvalidPlayer6() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            newPlayer.setId(123L);
            newPlayer.setName("Test Name");
            newPlayer.setPhotoURL("Test Photo URL");
            newPlayer.setUsername(playerList.get(0).getUsername());
            newPlayer.setPassword("Test Password");
            playerService.createPlayer(newPlayer);
        });
    }

    @Test
    void testCreateInvalidPlayer7() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            newPlayer.setId(123L);
            newPlayer.setName("Test Name");
            newPlayer.setPhotoURL("Test Photo URL");
            newPlayer.setUsername("Test Username");
            newPlayer.setPassword(null);
            playerService.createPlayer(newPlayer);
        });
    }

    @Test
    void testCreateInvalidPlayer8() {
        assertThrows(IllegalOperationException.class, () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            PlayerEntity newPlayer = factory.manufacturePojoWithFullData(PlayerEntity.class);
            newPlayer.setId(123L);
            newPlayer.setId(123L);
            newPlayer.setName("Test Name");
            newPlayer.setPhotoURL("Test Photo URL");
            newPlayer.setBirthDate(calendar.getTime());
            newPlayer.setUsername("Test Username");
            newPlayer.setPassword("Test Password");
            playerService.createPlayer(newPlayer);
        });
    }

    @Test
    void testGetPlayers() {
        List<PlayerEntity> players = playerService.getPlayers();
        assertEquals(players.size(), playerList.size());
        for (PlayerEntity player : players) {
            boolean found = false;
            for (PlayerEntity storedPlayer : playerList) {
                if (player.getId().equals(storedPlayer.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void testGetPlayer() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity storedPlayer = playerList.get(0);
        PlayerEntity player = playerService.getPlayer(storedPlayer.getId());
        assertNotNull(player);
        assertEquals(storedPlayer.getId(), player.getId());
        assertEquals(storedPlayer.getName(), player.getName());
        assertEquals(storedPlayer.getPhotoURL(), player.getPhotoURL());
        assertEquals(storedPlayer.getBirthDate(), player.getBirthDate());
        assertEquals(storedPlayer.getBirthPlace(), player.getBirthPlace());
        assertEquals(storedPlayer.getEloRating(), player.getEloRating());
        assertEquals(storedPlayer.getUsername(), player.getUsername());
        assertEquals(storedPlayer.getPassword(), player.getPassword());
    }

    @Test
    void testGetInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerService.getPlayer(null);
        });
    }

    @Test
    void testGetInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            playerService.getPlayer(0L);
        });
    }

    @Test
    void testGetInvalidPlayer3() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerService.getPlayer(321L);
        });
    }

    @Test
    void testUpdatePlayer() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity storedPlayer = playerList.get(0);
        PlayerEntity player = factory.manufacturePojoWithFullData(PlayerEntity.class);
        player.setId(storedPlayer.getId());
        player.setName("Test Name");
        player.setPhotoURL("Test Photo URL");
        player.setBirthDate(new Date());
        player.setBirthPlace("Test Birth Place");
        player.setEloRating(0);
        player.setUsername("Test Username");
        player.setPassword("Test Password");
        PlayerEntity updatedPlayer = playerService.updatePlayer(storedPlayer.getId(), player);
        assertEquals(player.getId(), updatedPlayer.getId());
        assertEquals(player.getName(), updatedPlayer.getName());
        assertEquals(player.getPhotoURL(), updatedPlayer.getPhotoURL());
        assertEquals(player.getBirthDate(), updatedPlayer.getBirthDate());
        assertEquals(player.getBirthPlace(), updatedPlayer.getBirthPlace());
        assertEquals(player.getEloRating(), updatedPlayer.getEloRating());
        assertEquals(player.getUsername(), updatedPlayer.getUsername());
        assertEquals(player.getPassword(), updatedPlayer.getPassword());
    }

    @Test
    void testUpdateInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity storedPlayer = playerList.get(0);
            playerService.updatePlayer(null, storedPlayer);
        });
    }

    @Test
    void testUpdateInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity storedPlayer = playerList.get(0);
            playerService.updatePlayer(0L, storedPlayer);
        });
    }

    @Test
    void testUpdateInvalidPlayer3() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerService.updatePlayer(321L, playerList.get(0));
        });
    }

    @Test
    void testUpdateInvalidPlayer4() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity storedPlayer = playerList.get(0);
            storedPlayer.setName(null);
            storedPlayer.setPhotoURL("Test Photo URL");
            storedPlayer.setUsername("Test Username");
            storedPlayer.setPassword("Test Password");
            playerService.updatePlayer(storedPlayer.getId(), storedPlayer);
        });
    }

    @Test
    void testUpdateInvalidPlayer5() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity storedPlayer = playerList.get(0);
            storedPlayer.setName("Test Name");
            storedPlayer.setPhotoURL(null);
            storedPlayer.setUsername("Test Username");
            storedPlayer.setPassword("Test Password");
            playerService.updatePlayer(storedPlayer.getId(), storedPlayer);
        });
    }

    @Test
    void testUpdateInvalidPlayer6() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity storedPlayer = playerList.get(0);
            storedPlayer.setName("Test Name");
            storedPlayer.setPhotoURL("Test Photo URL");
            storedPlayer.setUsername(null);
            storedPlayer.setPassword("Test Password");
            playerService.updatePlayer(storedPlayer.getId(), storedPlayer);
        });
    }

    @Test
    void testUpdateInvalidPlayer7() {
        assertThrows(IllegalOperationException.class, () -> {
            PlayerEntity storedPlayer = playerList.get(0);
            storedPlayer.setName("Test Name");
            storedPlayer.setPhotoURL("Test Photo URL");
            storedPlayer.setUsername("Test Username");
            storedPlayer.setPassword(null);
            playerService.updatePlayer(storedPlayer.getId(), storedPlayer);
        });
    }

    @Test
    void testUpdateInvalidPlayer8() {
        assertThrows(IllegalOperationException.class, () -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            PlayerEntity storedPlayer = playerList.get(0);
            storedPlayer.setName("Test Name");
            storedPlayer.setPhotoURL("Test Photo URL");
            storedPlayer.setBirthDate(calendar.getTime());
            storedPlayer.setUsername("Test Username");
            storedPlayer.setPassword("Test Password");
            playerService.updatePlayer(storedPlayer.getId(), storedPlayer);
        });
    }

    @Test
    void testDeletePlayer() throws IllegalOperationException, EntityNotFoundException {
        PlayerEntity storedPlayer = playerList.get(0);
        playerService.deletePlayer(storedPlayer.getId());
        PlayerEntity deletedPlayer = entityManager.find(PlayerEntity.class, storedPlayer.getId());
        assertNull(deletedPlayer);
    }

    @Test
    void testDeleteInvalidPlayer1() {
        assertThrows(IllegalOperationException.class, () -> {
            playerService.deletePlayer(null);
        });
    }

    @Test
    void testDeleteInvalidPlayer2() {
        assertThrows(IllegalOperationException.class, () -> {
            playerService.deletePlayer(0L);
        });
    }

    @Test
    void testDeleteInvalidPlayer3() {
        assertThrows(EntityNotFoundException.class, () -> {
            playerService.deletePlayer(321L);
        });
    }
}
