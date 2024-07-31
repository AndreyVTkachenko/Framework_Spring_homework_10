package ru.gb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import ru.gb.model.Employee;
import ru.gb.model.Project;
import ru.gb.model.Timesheet;
import ru.gb.repository.EmployeeRepository;
import ru.gb.repository.ProjectRepository;
import ru.gb.repository.TimesheetRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimesheetControllerTest {

    @Autowired
    TimesheetRepository timesheetRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    EmployeeRepository employeeRepository;

    @LocalServerPort
    private int port;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        timesheetRepository.deleteAll();
        restClient = RestClient.create("http://localhost:" + port);
    }


    @Test
    void find() {
        Timesheet expected = new Timesheet();
        expected.setProjectId(999L);
        expected = timesheetRepository.save(expected);

        RestClient restClient = RestClient.create("http://localhost:" + port);

        ResponseEntity<Timesheet> actual = restClient.get()
                .uri("/timesheets/" + expected.getId())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Timesheet>() {
                });

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        Timesheet responseBody = actual.getBody();
        assertNotNull(responseBody);
        assertEquals(expected.getId(), responseBody.getId());
        assertEquals(expected.getProjectId(), responseBody.getProjectId());
        assertEquals(expected.getEmployeeId(), responseBody.getEmployeeId());
        assertEquals(expected.getMinutes(), responseBody.getMinutes());
        assertEquals(expected.getCreatedAt(), responseBody.getCreatedAt());

    }

    @Test
    void findAll() {
        Timesheet expected1 = new Timesheet();
        expected1.setProjectId(101L);
        expected1 = timesheetRepository.save(expected1);

        Timesheet expected2 = new Timesheet();
        expected2.setProjectId(102L);
        expected2 = timesheetRepository.save(expected2);

        Timesheet expected3 = new Timesheet();
        expected3.setProjectId(103L);
        expected3 = timesheetRepository.save(expected3);

        Timesheet expected4 = new Timesheet();
        expected4.setProjectId(104L);
        expected4 = timesheetRepository.save(expected4);

        Timesheet expected5 = new Timesheet();
        expected5.setProjectId(105L);
        expected5 = timesheetRepository.save(expected5);

        ResponseEntity<List<Timesheet>> actual = restClient.get()
                .uri("/timesheets")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Timesheet>>() {});

        assertEquals(HttpStatus.OK, actual.getStatusCode());

        List<Timesheet> responseBody = actual.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());

        List<Long> ids = responseBody.stream().map(Timesheet::getId).collect(Collectors.toList());
        assertTrue(ids.contains(expected1.getId()));
        assertTrue(ids.contains(expected2.getId()));
        assertTrue(ids.contains(expected3.getId()));
        assertTrue(ids.contains(expected4.getId()));
        assertTrue(ids.contains(expected5.getId()));
    }

    @Test
    void create() {
        Project project = new Project();
        project.setName("Test Project");
        project = projectRepository.save(project);

        Employee employee = new Employee();
        employee.setName("Test Employee");
        employee = employeeRepository.save(employee);

        Timesheet toCreate = new Timesheet();
        toCreate.setProjectId(project.getId());
        toCreate.setEmployeeId(employee.getId());

        ResponseEntity<Timesheet> response = restClient.post()
                .uri("/timesheets")
                .body(toCreate)
                .retrieve()
                .toEntity(Timesheet.class);

        Timesheet responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getId());
        assertEquals(responseBody.getProjectId(), toCreate.getProjectId());
        assertEquals(toCreate.getEmployeeId(), responseBody.getEmployeeId());

        assertTrue(timesheetRepository.existsById(responseBody.getId()));
    }

    @Test
    void delete() {
        Timesheet toDelete = new Timesheet();
        toDelete.setProjectId(500L);
        toDelete = timesheetRepository.save(toDelete);

        ResponseEntity<Void> response = restClient.delete()
                .uri("/timesheets/" + toDelete.getId())
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertFalse(timesheetRepository.existsById(toDelete.getId()));

        Timesheet finalToDelete = toDelete;
        assertThrows(NoSuchElementException.class, () -> {
            timesheetRepository.findById(finalToDelete.getId()).orElseThrow();
        });
    }

    @Test
    void update() {

        Timesheet expected1 = new Timesheet();
        expected1.setProjectId(666L);
        expected1.setEmployeeId(666L);
        expected1.setMinutes(666);
        expected1.setCreatedAt(LocalDate.now());
        expected1 = timesheetRepository.save(expected1);

        Timesheet expected2 = new Timesheet();
        expected2.setProjectId(999L);
        expected2.setEmployeeId(999L);
        expected2.setMinutes(999);
        expected2.setCreatedAt(LocalDate.now());

        ResponseEntity<Timesheet> response = restClient.put()
                .uri("/timesheets/" + expected1.getId())
                .body(expected2)
                .retrieve()
                .toEntity(Timesheet.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Timesheet responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(expected1.getId(), responseBody.getId());
        assertEquals(expected2.getProjectId(), responseBody.getProjectId());
        assertEquals(expected2.getEmployeeId(), responseBody.getEmployeeId());
        assertEquals(expected2.getMinutes(), responseBody.getMinutes());
        assertEquals(expected2.getCreatedAt(), responseBody.getCreatedAt());

        Timesheet updatedTimesheet = timesheetRepository.findById(expected1.getId())
                .orElseThrow(() -> new AssertionError("Timesheet not found in database"));
        assertEquals(expected2.getProjectId(), updatedTimesheet.getProjectId());
        assertEquals(expected2.getEmployeeId(), updatedTimesheet.getEmployeeId());
        assertEquals(expected2.getMinutes(), updatedTimesheet.getMinutes());
        assertEquals(expected2.getCreatedAt(), updatedTimesheet.getCreatedAt());
    }
}