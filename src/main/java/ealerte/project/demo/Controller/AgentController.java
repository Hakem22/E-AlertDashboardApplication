package ealerte.project.demo.Controller;

import ealerte.project.demo.Model.Agent;
import ealerte.project.demo.Model.Alert;
import ealerte.project.demo.Repository.AgentRepository;
import ealerte.project.demo.Repository.AlertRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AgentController {
    private AgentRepository agentRepository;
    private AlertRepository alertRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public AgentController(AgentRepository agentRepository, AlertRepository alertRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.agentRepository = agentRepository;
        this.alertRepository = alertRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/agents")
    public List<Agent> getAgents() {
        return (List<Agent>) agentRepository.findAll();
    }

    @PostMapping("/agent")
    public void addAgent(@RequestBody Agent agent){
        agent.setPassword(bCryptPasswordEncoder.encode(agent.getPassword()));
        agentRepository.save(agent);
    }

    @GetMapping("/agent/{id}")
    public Agent retrieveAgent(@PathVariable long id) {
        Optional<Agent> agent = agentRepository.findById(id);
       /* if (!admin.isPresent())
            throw new AdminNotFoundException("id-" + id);*/
        return agent.get();
    }
    @DeleteMapping("/agent/{id}")
    public void deleteAgent(@PathVariable long id) {
        agentRepository.deleteById(id);
    }

    @PutMapping("/agent/{id}")
    public ResponseEntity<Object> updateAgent(@RequestBody Agent agent, @PathVariable long id) {

        Optional<Agent> agentOptional = agentRepository.findById(id);

        if (!agentOptional.isPresent())  return ResponseEntity.notFound().build();

        agent.setId(id);
        agent.setPassword(bCryptPasswordEncoder.encode(agent.getPassword()));
        agentRepository.save(agent);
        return ResponseEntity.noContent().build();
    }

    /* Manipulating Alerts */

    @GetMapping("/agent/{id}/alerts")
    public List<Alert> getAllAlertsByAgentId(@PathVariable (value = "id") Long id) {
        return alertRepository.findAllByActeurId(id);
    }

    @PostMapping("/agent/{id}/alerts")
    public Alert createAlert(@PathVariable (value = "id") Long id, @Valid @RequestBody Alert alert) {
        return agentRepository.findById(id).map(agent -> {
            alert.setActeur(agent);
            return alertRepository.save(alert);
        }).orElseThrow(() -> new ResourceNotFoundException("agentId " + id + " not found"));
    }



    @DeleteMapping("/agent/{id}/alert/{alertId}")
    public ResponseEntity<?> deleteAlert(@PathVariable (value = "id") Long id,
                                         @PathVariable (value = "MediaId") Long alertId) {
        return alertRepository.findByIdAndActeurId(alertId,id).map(alert -> {
            alertRepository.delete(alert);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("alert not found with id " + alertId + " and postId " + id));
    }


}
