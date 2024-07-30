package ru.gb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
