package ru.gb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.aspect.Recover;
import ru.gb.exception.API;
import ru.gb.model.Employee;
import ru.gb.model.Timesheet;
import ru.gb.service.EmployeeService;
import ru.gb.service.TimesheetService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Recover
public class EmployeeController {

    private final EmployeeService employeeService;
    private final TimesheetService timesheetService;

    @Operation(summary = "Get Employee", description = "Получить сотрудника по его идентификатору")
    @API.SuccessResponse
    @API.NotFoundResponse
    @API.InternalServerErrorResponse
    @GetMapping("/{id}")
    public ResponseEntity<Employee> get(@PathVariable @Parameter(description = "Идентификатор сотрудника") Long id) {
        return employeeService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get All Employees", description = "Получить список всех сотрудников", responses = @ApiResponse(description = "Успешный ответ", responseCode = "200", content = @Content(schema = @Schema(implementation = Employee.class))))
    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @Operation(
            summary = "Create Employee",
            description = "Создать нового сотрудника",
            responses = {
                    @ApiResponse(description = "Успешный ответ", responseCode = "201", content = @Content(schema = @Schema(implementation = Employee.class)))
            }
    )
    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(employee));
    }

    @Operation(summary = "Delete Employee", description = "Удалить сотрудника по его идентификатору", responses = @ApiResponse(description = "Успешный ответ", responseCode = "204"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Parameter(description = "Идентификатор сотрудника") Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get Employee Timesheets", description = "Получить все табели для сотрудника по его идентификатору")
    @API.SuccessResponse
    @API.NotFoundResponse
    @API.InternalServerErrorResponse
    @GetMapping("/{id}/timesheets")
    public ResponseEntity<List<Timesheet>> getTimesheets(@PathVariable @Parameter(description = "Идентификатор сотрудника") Long id) {
        if (employeeService.getById(id).isEmpty()) {
            throw new NoSuchElementException("Сотрудник с id = " + id + " не существует");
        }
        return ResponseEntity.ok(timesheetService.getByEmployeeId(id));
    }
}
