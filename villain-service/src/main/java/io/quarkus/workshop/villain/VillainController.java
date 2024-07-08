package io.quarkus.workshop.villain;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RunOnVirtualThread
@RestController
@RequestMapping(value = "/api/villains")
public class VillainController {


    private final VillainRepository repository;

    public VillainController(VillainRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/random")
    public ResponseEntity<Villain> getRandomVillain() {
        var villain = repository.findRandom();
        if (villain != null) {
            Log.debugf("Found random villain: %s", villain);
            return ResponseEntity.ok(villain);
        } else {
            Log.debug("No random villain found");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Villain> getAllVillains() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Villain> getVillain(Long id) {
        var villain = repository.findById(id).orElse(null);
        if (villain != null) {
            return ResponseEntity.ok(villain);
        } else {
            Log.debugf("No Villain found with id %d", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<URI> createVillain(@Valid Villain villain, @Context UriInfo uriInfo) {
        var persisted = repository.save(villain);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(persisted.getId()));
        Log.debugf("New Villain created with URI %s", builder.build().toString());
        return ResponseEntity.created(builder.build()).build();
    }

    @PutMapping
    @Transactional
    public Villain updateVillain(@Valid Villain villain) {
        Villain retrieved = repository.findById(villain.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Villain not found"));
        retrieved.setName(villain.getName());
        retrieved.setOtherName(villain.getOtherName());
        retrieved.setLevel(villain.getLevel());
        retrieved.setPicture(villain.getPicture());
        retrieved.setPowers(villain.setPowers());
        Log.debugf("Villain updated with new valued %s", retrieved);
        return retrieved;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteVillain(Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello Villain Service";
    }
}

