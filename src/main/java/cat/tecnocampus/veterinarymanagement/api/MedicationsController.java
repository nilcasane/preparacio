package cat.tecnocampus.veterinarymanagement.api;

import cat.tecnocampus.veterinarymanagement.application.MedicationsService;
import cat.tecnocampus.veterinarymanagement.application.exceptions.MedicationBatchDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.exceptions.MedicationDoesNotExistException;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.LowStockAlertCommand;
import cat.tecnocampus.veterinarymanagement.application.inputDTO.MedicationBatchCommand;
import cat.tecnocampus.veterinarymanagement.application.mappers.LowStockAlertMapper;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.LowStockAlertInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationBatchInformation;
import cat.tecnocampus.veterinarymanagement.application.outputDTO.MedicationInformation;
import cat.tecnocampus.veterinarymanagement.domain.LowStockAlert;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medications")
public class MedicationsController {
    private final MedicationsService medicationsService;

    @Autowired
    public MedicationsController(MedicationsService medicationsService) {
        this.medicationsService = medicationsService;
    }

    @GetMapping("/{medication_id}")
    public MedicationInformation getMedication(@PathVariable Long medication_id) {
        return medicationsService.getMedicationById(medication_id)
                .orElseThrow(() -> new MedicationDoesNotExistException("Medication with id " + medication_id + " does not exist"));
    }

    /**
     * Crea un nuevo lote para un medicamento
     * @param medication_id ID del medicamento
     * @param input Datos del lote
     * @param uriBuilder Constructor de URI
     * @return Información del lote creado
     */
    @PostMapping("/{medication_id}/medicationBatches")
    public ResponseEntity<MedicationBatchInformation> createMedicationBatch(
            @PathVariable Long medication_id,
            @RequestBody @Valid MedicationBatchCommand input,
            UriComponentsBuilder uriBuilder) {
        Long id = medicationsService.createMedicationBatch(medication_id, input);
        var location = uriBuilder.path("/medicationsBatches/{id}")
                .buildAndExpand(id).toUri();
        MedicationBatchInformation info = medicationsService.getMedicationBatchById(id).orElseThrow();
        return ResponseEntity.created(location).body(info);
    }

    /**
     * Lista todos los lotes de un medicamento
     * @param medication_id ID del medicamento
     * @return Lista de lotes
     */
    @GetMapping("/{medication_id}/medicationBatches")
    public List<MedicationBatchInformation> getMedicationBatches(@PathVariable Long medication_id) {
        return medicationsService.getMedicationBatches(medication_id);
    }
    /**
     * Obtiene un lote de medicamento por su ID
     * @param batch_id ID del lote
     * @return Información del lote
     */
    @GetMapping("/{batch_id}")
    public MedicationBatchInformation getMedicationBatch(@PathVariable Long batch_id) {
        return medicationsService
                .getMedicationBatchById(batch_id)
                .orElseThrow(() -> new MedicationBatchDoesNotExistException("Medication batch with id " + batch_id + " does not exist"));
    }

    /**
     * Elimina un lote de medicamento por su ID
     * @param batch_id ID del lote
     * @return Respuesta vacía
     */
    @DeleteMapping("/{batch_id}")
    public ResponseEntity<Void> deleteMedicationBatch(@PathVariable Long batch_id) {
        medicationsService.deleteMedicationBatch(batch_id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza un lote de medicamento existente
     * @param batch_id ID del lote
     * @param input Datos actualizados del lote
     * @return Información del lote actualizado
     */
    @PutMapping("/{batch_id}")
    public ResponseEntity<MedicationBatchInformation> updateMedicationBatch(
            @PathVariable Long batch_id,
            @RequestBody @Valid MedicationBatchCommand input) {
        MedicationBatchInformation info = medicationsService.updateMedicationBatch(batch_id, input);
        return ResponseEntity.ok(info);
    }
    @GetMapping("/{medicationId}")
    public LowStockAlertInformation getLowStockAlert(@PathVariable Long medicationId) {
        LowStockAlert alert = medicationsService.getLowStockAlertByMedicationId(medicationId);
        return LowStockAlertMapper.toMedicationBatchInformation(alert);
    }

    @GetMapping
    public List<LowStockAlertInformation> getAllLowStockAlerts() {
        return medicationsService.getAllLowStockAlerts().stream()
                .map(LowStockAlertMapper::toMedicationBatchInformation)
                .collect(Collectors.toList());
    }

    @PostMapping("/{medicationId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LowStockAlertInformation createLowStockAlert(@RequestBody LowStockAlertCommand command,
                                                        @PathVariable Long medicationId) {
        LowStockAlert alert = LowStockAlertMapper.inputLowToDomain(command, null);
        LowStockAlert created = medicationsService.createLowStockAlert(medicationId, alert);
        return LowStockAlertMapper.toMedicationBatchInformation(created);
    }

    @DeleteMapping("/{medication_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLowStockAlert(@PathVariable Long medication_id) {
        medicationsService.deleteLowStockAlert(medication_id);
    }
}