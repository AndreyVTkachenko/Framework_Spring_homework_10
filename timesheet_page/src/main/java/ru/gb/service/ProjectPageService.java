package ru.gb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.gb.client.ProjectResponse;
import ru.gb.dto.ProjectPageDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectPageService {

    private final RestClient restClient;

    public ProjectPageService() {
        this.restClient = RestClient.create("http://localhost:8080");
    }

    public List<ProjectPageDto> findAll() {
        List<ProjectResponse> projects = restClient.get()
                .uri("/projects")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProjectResponse>>() {
                });

        List<ProjectPageDto> result = new ArrayList<>();
        for (ProjectResponse project : projects) {
            ProjectPageDto projectPageDto = new ProjectPageDto();
            projectPageDto.setId(String.valueOf(project.getId()));
            projectPageDto.setName(project.getName());
            result.add(projectPageDto);
        }
        return result;
    }

    public Optional<ProjectPageDto> findById(Long id) {
        try {
            ProjectResponse project = restClient.get()
                    .uri("/projects/" + id)
                    .retrieve()
                    .body(ProjectResponse.class);

            ProjectPageDto projectPageDto = new ProjectPageDto();
            projectPageDto.setId(String.valueOf(project.getId()));
            projectPageDto.setName(project.getName());
            return Optional.of(projectPageDto);

        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }
}
