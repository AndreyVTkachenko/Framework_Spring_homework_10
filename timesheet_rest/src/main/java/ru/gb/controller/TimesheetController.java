package ru.gb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.aspect.Recover;
import ru.gb.exception.API;
import ru.gb.model.Timesheet;
import ru.gb.service.TimesheetService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/timesheets")
@Recover
public class TimesheetController {

    private final TimesheetService service;

    public TimesheetController(TimesheetService service) {
        this.service = service;
    }

    @Operation(summary = "Get Timesheet", description = "Получить табель по его идентификатору")
    @API.SuccessResponse
    @API.NotFoundResponse
    @API.InternalServerErrorResponse
    @GetMapping("/{id}")
    public ResponseEntity<Timesheet> find(@PathVariable @Parameter(description = "Идентификатор табеля") Long id) {
        Optional<Timesheet> timesheet = service.findById(id);
        return timesheet.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get All Timesheets", description = "Получить список всех табелей", responses = @ApiResponse(description = "Успешный ответ", responseCode = "200", content = @Content(schema = @Schema(implementation = Timesheet.class))))
    @GetMapping
    public ResponseEntity<List<Timesheet>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Create Timesheet", description = "Создать новый табель", responses = @ApiResponse(description = "Успешный ответ", responseCode = "201", content = @Content(schema = @Schema(implementation = Timesheet.class))))
    @PostMapping
    public ResponseEntity<Timesheet> create(@RequestBody Timesheet timesheet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(timesheet));
    }

    @Operation(summary = "Delete Timesheet", description = "Удалить табель по его идентификатору", responses = @ApiResponse(description = "Успешный ответ", responseCode = "204"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "Идентификатор табеля") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update Timesheet", description = "Обновить табель", responses = {
            @ApiResponse(description = "Успешный ответ", responseCode = "200", content = @Content(schema = @Schema(implementation = Timesheet.class))),
            @ApiResponse(description = "Табель не найден", responseCode = "404")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Timesheet> update(@PathVariable Long id, @RequestBody Timesheet timesheet) {
        Timesheet updatedTimesheet = service.update(id, timesheet);
        return ResponseEntity.ok(updatedTimesheet);
    }

}
