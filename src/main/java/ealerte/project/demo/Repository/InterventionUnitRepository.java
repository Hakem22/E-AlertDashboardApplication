package ealerte.project.demo.Repository;

import ealerte.project.demo.Model.InterventionUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface   InterventionUnitRepository extends JpaRepository<InterventionUnit, Long> {
}
