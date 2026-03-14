package com.rubiojdev.todolist.tasks.mappers;

import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWithItemsResponseDto;
import com.rubiojdev.todolist.tasks.entities.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class TaskMapperImplTest {

    @InjectMocks
    private TaskMapperImpl mapper;

    @Test
    void toEntity_creatingTask_returnsTask() {
        //Arrange
        String nameExpected = "Tarea1";
        TaskCreateDto createDto = new TaskCreateDto(" Tarea1 ");

        //Act
        Task result = mapper.toEntity(createDto);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(nameExpected, result.getName());
    }

    @Test
    void toEntity_dtoIsNull_throwIllegalArgumentException() {
        //Arrange
        String expectedMessage = "TaskCreateDto no puede ser NULL";

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                mapper.toEntity(null));

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void updateEntity_validFields_updatesTask() {
        //Arrange
        Task task = new Task();
        task.setName("OldName");
        task.setCompleted(false);

        TaskUpdateDto dto = new TaskUpdateDto(" NewName ", true);

        //Act
        mapper.updateEntity(task, dto);

        //Assert
        Assertions.assertEquals("NewName", task.getName());
        Assertions.assertTrue(task.isCompleted());
    }

    @Test
    void updateEntity_nameNull_shouldNotUpdateName() {
        //Arrange
        Task task = new Task();
        task.setName("OldName");

        TaskUpdateDto dto = new TaskUpdateDto(null, null);

        //Act
        mapper.updateEntity(task, dto);

        //Assert
        Assertions.assertEquals("OldName", task.getName());
    }

    @Test
    void updateEntity_nameBlank_shouldNotUpdateName() {
        //Arrange
        Task task = new Task();
        task.setName("OldName");

        TaskUpdateDto dto = new TaskUpdateDto("   ", null);

        //Act
        mapper.updateEntity(task, dto);

        //Assert
        Assertions.assertEquals("OldName", task.getName());
    }

    @Test
    void updateEntity_completedNull_shouldNotUpdateCompleted() {
        //Arrange
        Task task = new Task();
        task.setCompleted(false);

        TaskUpdateDto dto = new TaskUpdateDto(null, null);

        //Act
        mapper.updateEntity(task, dto);

        //Assert
        Assertions.assertFalse(task.isCompleted());
    }

    @Test
    void toResponseDto_creatingResponseDto_returnsResponseDto() {
        //Arrange
        Task task = new Task();
        task.setId(1L);
        task.setName("Task1");
        task.setCompleted(false);
        task.setUpdatedAt(Instant.now());

        //Act
        TaskResponseDto result = mapper.toResponseDto(task);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isCompleted());
        Assertions.assertEquals(task.getId(), result.getId());
        Assertions.assertEquals(task.getName(), result.getName());
        Assertions.assertEquals(task.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void toResponseDtoWhitItem_creatingResponseDtoWhitItem_returnsResponseDtoWhitItem() {
        //Arrange
        TaskItem taskItem = new TaskItem("Buy");
        List<TaskItem> taskItems = List.of(taskItem);

        Task task = new Task();
        task.setId(1L);
        task.setName("Task1");
        task.setCompleted(false);
        task.setUpdatedAt(Instant.now());
        task.setTaskItems(taskItems);

        //Act
        TaskWithItemsResponseDto result = mapper.toResponseDtoWhitItem(task);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(task.getId(), result.getId());
        Assertions.assertEquals(task.getName(), result.getName());
        Assertions.assertEquals(task.getUpdatedAt(), result.getUpdatedAt());
        Assertions.assertEquals(1, result.getTaskItemDtoList().size());
    }

    @Test
    void toResponseDtoWhitItem_taskItemsNull_shouldReturnEmptyList() {
        //Arrange
        Task task = new Task();
        task.setTaskItems(null);

        //Act
        TaskWithItemsResponseDto result = mapper.toResponseDtoWhitItem(task);

        //Assert
        Assertions.assertNotNull(result.getTaskItemDtoList());
        Assertions.assertTrue(result.getTaskItemDtoList().isEmpty());
    }
}