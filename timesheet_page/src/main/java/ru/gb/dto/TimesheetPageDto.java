package ru.gb.dto;

import lombok.Data;

/**
 * Класс (DTO Data Transfer Object), который описывает параметры для шаблонов HTML-страниц.
 * Т.е. он нужен для передачи параметров внутрь thymeleaf в тех контроллерах, которые сразу отдают HTML-страницы.
 */
@Data
public class TimesheetPageDto {

  private String projectName;
  private String id;
  private String minutes;
  private String createdAt;
  private String projectId;
}
