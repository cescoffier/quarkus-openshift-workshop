package io.quarkus.workshop.villain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VillainRepository extends JpaRepository<Villain, Long> {

    //<springSpecificMethod>
    default Villain findRandom() {
        var count = count();
        var index = (int) (Math.random() * count);
        return findAll().get(index);
    }
    //</springSpecificMethod>

}
