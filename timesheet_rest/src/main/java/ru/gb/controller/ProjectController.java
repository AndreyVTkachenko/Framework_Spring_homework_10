package ru.gb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.aspect.Recover;
import ru.gb.exception.API;
import ru.gb.model.Project;
import ru.gb.model.Timesheet;
import ru.gb.service.ProjectService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/projects")
@Tag(name = "Projects", description = "API для работы с проектами")
@Recover
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @Operation(summary = "Get Project", description = "Получить проект по его идентификатору")
    @API.SuccessResponse
    @API.NotFoundResponse
    @API.InternalServerErrorResponse
    @GetMapping("/{id}")
    public ResponseEntity<Project> find(@PathVariable @Parameter(description = "Идентификатор проекта") Long id) {
        Project project = service.findById(id).orElseThrow(() -> new NoSuchElementException("Проект не найден"));
        return ResponseEntity.ok(project);
    }

    @Operation(summary = "Get Project Timesheets", description = "Получить все табели для проекта по его идентификатору")
    @API.SuccessResponse
    @API.NotFoundResponse
    @API.InternalServerErrorResponse
    @GetMapping("/{id}/timesheets")
    public ResponseEntity<List<Timesheet>> findTimesheets(@PathVariable @Parameter(description = "Идентификатор проекта") Long id) {
        try {
            return ResponseEntity.ok(service.getTimesheets(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get All Projects", description = "Получить список всех проектов", responses = @ApiResponse(description = "Успешный ответ", responseCode = "200", content = @Content(schema = @Schema(implementation = Project.class))))
    @API.SuccessResponse
    @GetMapping
    public ResponseEntity<List<Project>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Create Project", description = "Создать новый проект", responses = @ApiResponse(description = "Успешный ответ", responseCode = "201", content = @Content(schema = @Schema(implementation = Project.class))))
    @PostMapping
    public ResponseEntity<Project> create(@RequestBody Project project) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(project));
    }

    @Operation(summary = "Delete Project", description = "Удалить проект по его идентификатору", responses = @ApiResponse(description = "Успешный ответ", responseCode = "204"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "Идентификатор проекта") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
