package ru.gb.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.gb.model.Timesheet;
import ru.gb.service.TimesheetService;

@ContextConfiguration(classes = {TimesheetController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class TimesheetControllerDiffblueTest {
    @Autowired
    private TimesheetController timesheetController;

    @MockBean
    private TimesheetService timesheetService;

    /**
     * Method under test: {@link TimesheetController#findAll()}
     */
    @Test
    void testFindAll() throws Exception {
        // Arrange
        when(timesheetService.findAll()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/timesheets");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(timesheetController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    /**
     * Method under test: {@link TimesheetController#create(Timesheet)}
     */
    @Test
    void testCreate() throws Exception {
        // Arrange
        Timesheet timesheet = new Timesheet();
        timesheet.setCreatedAt(LocalDate.of(1970, 1, 1));
        timesheet.setEmployeeId(1L);
        timesheet.setId(1L);
        timesheet.setMinutes(1);
        timesheet.setProjectId(1L);
        when(timesheetService.create(Mockito.<Timesheet>any())).thenReturn(timesheet);

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setCreatedAt(null);
        timesheet2.setEmployeeId(1L);
        timesheet2.setId(1L);
        timesheet2.setMinutes(1);
        timesheet2.setProjectId(1L);
        String content = (new ObjectMapper()).writeValueAsString(timesheet2);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/timesheets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(timesheetController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"id\":1,\"projectId\":1,\"employeeId\":1,\"minutes\":1,\"createdAt\":[1970,1,1]}"));
    }

    /**
     * Method under test: {@link TimesheetController#delete(Long)}
     */
    @Test
    void testDelete() throws Exception {
        // Arrange
        doNothing().when(timesheetService).delete(Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/timesheets/{id}", 1L);

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(timesheetController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Method under test: {@link TimesheetController#delete(Long)}
     */
    @Test
    void testDelete2() throws Exception {
        // Arrange
        doNothing().when(timesheetService).delete(Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/timesheets/{id}", 1L);
        requestBuilder.contentType("https://example.org/example");

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(timesheetController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Method under test: {@link TimesheetController#update(Long, Timesheet)}
     */
    @Test
    void testUpdate() throws Exception {
        // Arrange
        Timesheet timesheet = new Timesheet();
        timesheet.setCreatedAt(LocalDate.of(1970, 1, 1));
        timesheet.setEmployeeId(1L);
        timesheet.setId(1L);
        timesheet.setMinutes(1);
        timesheet.setProjectId(1L);
        when(timesheetService.update(Mockito.<Long>any(), Mockito.<Timesheet>any())).thenReturn(timesheet);

        Timesheet timesheet2 = new Timesheet();
        timesheet2.setCreatedAt(null);
        timesheet2.setEmployeeId(1L);
        timesheet2.setId(1L);
        timesheet2.setMinutes(1);
        timesheet2.setProjectId(1L);
        String content = (new ObjectMapper()).writeValueAsString(timesheet2);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/timesheets/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(timesheetController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"id\":1,\"projectId\":1,\"employeeId\":1,\"minutes\":1,\"createdAt\":[1970,1,1]}"));
    }

    /**
     * Method under test: {@link TimesheetController#find(Long)}
     */
    @Test
    void testFind() throws Exception {
        // Arrange
        Timesheet timesheet = new Timesheet();
        timesheet.setCreatedAt(LocalDate.of(1970, 1, 1));
        timesheet.setEmployeeId(1L);
        timesheet.setId(1L);
        timesheet.setMinutes(1);
        timesheet.setProjectId(1L);
        Optional<Timesheet> ofResult = Optional.of(timesheet);
        when(timesheetService.findById(Mockito.<Long>any())).thenReturn(ofResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/timesheets/{id}", 1L);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(timesheetController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"id\":1,\"projectId\":1,\"employeeId\":1,\"minutes\":1,\"createdAt\":[1970,1,1]}"));
    }

    /**
     * Method under test: {@link TimesheetController#find(Long)}
     */
    @Test
    void testFind2() throws Exception {
        // Arrange
        Optional<Timesheet> emptyResult = Optional.empty();
        when(timesheetService.findById(Mockito.<Long>any())).thenReturn(emptyResult);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/timesheets/{id}", 1L);

        // Act
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(timesheetController)
                .build()
                .perform(requestBuilder);

        // Assert
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
