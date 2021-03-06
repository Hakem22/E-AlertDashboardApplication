package ealerte.project.demo.Controller;

import ealerte.project.demo.Model.AlertS;
import ealerte.project.demo.Model.Sensor;
import ealerte.project.demo.Model.SensorState;
import ealerte.project.demo.Model.SensorType;
import ealerte.project.demo.Repository.AlertSRepository;
import ealerte.project.demo.Repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class SensorController {
    private SensorRepository sensorRepository;
    private AlertSRepository alertSRepository;

    public SensorController(SensorRepository sensorRepository, AlertSRepository alertSRepository) {
        this.sensorRepository = sensorRepository;
        this.alertSRepository= alertSRepository;
    }

    @GetMapping("/sensors")
    public List<Sensor> getSensors() {
        System.out.println(sensorRepository.findAll());
        return (List<Sensor>) sensorRepository.findAll();
    }
    @PostMapping("/sensor")
    public void addSensor(@RequestBody Sensor sensor){
        sensorRepository.save(sensor);
    }

    @GetMapping("/sensor/{id}")
    public Sensor retrieveSensor(@PathVariable long id) {
        Optional<Sensor> sensor = sensorRepository.findById(id);
       /* if (!admin.isPresent())
            throw new AdminNotFoundException("id-" + id);*/
        return sensor.get();
    }
    @DeleteMapping("/sensor/{id}")
    public void deleteSensor(@PathVariable long id) {
        sensorRepository.deleteById(id);
    }

    @PutMapping("/sensor/{id}")
    public ResponseEntity<Object> updateAdmin(@RequestBody Sensor sensor, @PathVariable long id) {
        Optional<Sensor> sensorOptional = sensorRepository.findById(id);
        if (!sensorOptional.isPresent())  return ResponseEntity.notFound().build();
        sensor.setId(id);
        sensorRepository.save(sensor);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sensor/{id}/alerts")
    public List<AlertS> getAllAlertsBySensorId(@PathVariable (value = "id") Long id) {
        return alertSRepository.findBySensorId(id);
    }

    @GetMapping("/sensor/type/{type}")
    public List<Sensor> getSensorsByType(@PathVariable String type) {
        if(type.equals("water")) return sensorRepository.findSensorsByType(SensorType.WATER_LEVEL);
        else return sensorRepository.findSensorsByType(SensorType.FOREST_FIRE);

    }

    @GetMapping("/sensor/status/{state}")
    public List<Sensor> getSensorsByState(@PathVariable String state) {
       SensorState sensorState;


        if(state.equals("active")) sensorState=SensorState.ACTIVE;
        else if(state.equals("suspend")) sensorState=SensorState.SUSPEND;
        else sensorState=SensorState.DISABLED;

        System.out.println(sensorState);
       List<Sensor> sensors= new ArrayList<Sensor>() ;
        List<Sensor> sensorsFound=sensorRepository.findAll();
        for (Sensor s: sensorsFound){
            if(s.getState().equals(sensorState)) sensors.add(s);
        }
        System.out.println(sensors);
        return sensors;
    }

    @PostMapping("/sensor/{id}/alerts")
    public AlertS createAlerts(@PathVariable (value = "id") Long id, @Valid @RequestBody AlertS alertS) {
        return sensorRepository.findById(id).map(sensor -> {
            alertS.setSensor(sensor);
            return alertSRepository.save(alertS);
        }).orElseThrow(() -> new ResourceNotFoundException("sensorId " + id + " not found"));
    }



    @DeleteMapping("/sensor/{id}/alerts/{alertId}")
    public ResponseEntity<?> deleteAlert(@PathVariable (value = "id") Long id,
                                           @PathVariable (value = "alertId") Long alertId) {
        return alertSRepository.findByIdAndSensorId(alertId,id).map(alertS -> {
            alertSRepository.delete(alertS);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("alert not found with id " + alertId + " and postId " + id));
    }

}
