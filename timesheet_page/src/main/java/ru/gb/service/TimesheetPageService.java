package ru.gb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.gb.client.ProjectResponse;
import ru.gb.client.TimesheetResponse;
import ru.gb.dto.TimesheetPageDto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimesheetPageService {

  private final RestClient restClient;

  public TimesheetPageService() {
    this.restClient = RestClient.create("http://localhost:8080");
  }

  public List<TimesheetPageDto> findAll() {
    List<TimesheetResponse> timesheets = restClient.get()
            .uri("/timesheets")
            .retrieve()
            .body(new ParameterizedTypeReference<List<TimesheetResponse>>() {
            });

    List<TimesheetPageDto> result = new ArrayList<>();
    for (TimesheetResponse timesheet : timesheets) {
      TimesheetPageDto timesheetPageDto = new TimesheetPageDto();
      timesheetPageDto.setId(String.valueOf(timesheet.getId()));
      timesheetPageDto.setMinutes(String.valueOf(timesheet.getMinutes()));
      timesheetPageDto.setCreatedAt(timesheet.getCreatedAt().format(DateTimeFormatter.ISO_DATE));

      ProjectResponse project = restClient.get()
              .uri("/projects/" + timesheet.getProjectId())
              .retrieve()
              .body(ProjectResponse.class);
      timesheetPageDto.setProjectName(project.getName());

      result.add(timesheetPageDto);
    }
    return result;
  }

  public Optional<TimesheetPageDto> findById(Long id) {
    try {
      TimesheetResponse timesheet = restClient.get()
              .uri("/timesheets/" + id)
              .retrieve()
              .body(TimesheetResponse.class);

      TimesheetPageDto timesheetPageDto = new TimesheetPageDto();
      timesheetPageDto.setId(String.valueOf(timesheet.getId()));
      timesheetPageDto.setMinutes(String.valueOf(timesheet.getMinutes()));
      timesheetPageDto.setCreatedAt(timesheet.getCreatedAt().format(DateTimeFormatter.ISO_DATE));

      ProjectResponse project = restClient.get()
              .uri("/projects/" + timesheet.getProjectId())
              .retrieve()
              .body(ProjectResponse.class);
      timesheetPageDto.setProjectName(project.getName());
      return Optional.of(timesheetPageDto);

    } catch (HttpClientErrorException.NotFound e) {
      return Optional.empty();
    }
  }
}
