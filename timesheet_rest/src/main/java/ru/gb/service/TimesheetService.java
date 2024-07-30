package ru.gb.service;

import org.slf4j.event.Level;
import org.springframework.stereotype.Service;
import ru.gb.aspect.Timer;
import ru.gb.model.Timesheet;
import ru.gb.repository.EmployeeRepository;
import ru.gb.repository.ProjectRepository;
import ru.gb.repository.TimesheetRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@Timer(level = Level.TRACE)
public class TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public TimesheetService(TimesheetRepository repository, ProjectRepository projectRepository, EmployeeRepository employeeRepository) {
        this.timesheetRepository = repository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }

    public Optional<Timesheet> findById(Long id) {
        return timesheetRepository.findById(id);
    }

    public List<Timesheet> findAll() {
        return timesheetRepository.findAll();
    }

    public Timesheet create(Timesheet timesheet) {
        if (Objects.isNull(timesheet.getProjectId()) || Objects.isNull(timesheet.getEmployeeId())) {
            throw new IllegalArgumentException("projectId and employeeId must not be null");
        }

        if (projectRepository.findById(timesheet.getProjectId()).isEmpty()) {
            throw new NoSuchElementException("Project with id " + timesheet.getProjectId() + " does not exist");
        }

        if (employeeRepository.findById(timesheet.getEmployeeId()).isEmpty()) {
            throw new NoSuchElementException("Employee with id " + timesheet.getEmployeeId() + " does not exist");
        }

        timesheet.setCreatedAt(LocalDate.now());
        return timesheetRepository.save(timesheet);
    }

    public void delete(Long id) {
        timesheetRepository.deleteById(id);
    }

    public List<Timesheet> getByEmployeeId(Long employeeId) {
        if (employeeRepository.findById(employeeId).isEmpty()) {
            throw new NoSuchElementException("Employee with id = " + employeeId + " does not exists");
        }
        return timesheetRepository.findByEmployeeId(employeeId);
    }

    public Timesheet update(Long id, Timesheet timesheet) {
        Timesheet existingTimesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Timesheet with id " + id + " does not exist"));

        existingTimesheet.setProjectId(timesheet.getProjectId());
        existingTimesheet.setEmployeeId(timesheet.getEmployeeId());
        existingTimesheet.setMinutes(timesheet.getMinutes());
        existingTimesheet.setCreatedAt(timesheet.getCreatedAt());

        return timesheetRepository.save(existingTimesheet);
    }
}
