package com.rubiojdev.todolist.taskitems.mappers;

import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskItemMapperImplTest {

    @InjectMocks
    private TaskItemMapperImpl mapper;

    @Test
    void toEntity_creatingTaskItem_returnsTaskItem() {
        //Arrange
        String nameExpected = "Pay Netflix";
        TaskItemCreateDto createDto = new TaskItemCreateDto(" Pay Netflix ");

        //Act
        TaskItem result = mapper.toEntity(createDto);

        //Assertion
        Assertions.assertNotNull(result);
        Assertions.assertEquals(nameExpected, result.getName());
    }

    @Test
    void toEntity_taskItemIsNull_throwIllegalArgumentException() {
        //Arrange
        String messageExpected = "TaskItemCreateDto no puede ser NULL";
        TaskItemCreateDto createDto = null;

        //Act & Assertion
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                mapper.toEntity(createDto));

        Assertions.assertEquals(messageExpected, exception.getMessage());
    }

    @Test
    void toResponseDto_creatingTaskItemResponseDto_returnsTaskItemResponseDto() {
        //Arrange
        TaskItem taskItem = new TaskItem();
        taskItem.setId(1L);
        taskItem.setName("Buy Coffee");
        taskItem.setCompleted(false);

        //Act
        TaskItemResponseDto result = mapper.toResponseDto(taskItem);

        //Assertion
        Assertions.assertNotNull(result);
        Assertions.assertEquals(taskItem.getId(), result.getId());
        Assertions.assertEquals(taskItem.getName(), result.getName());
        Assertions.assertEquals(taskItem.isCompleted(), result.isCompleted());
    }

    @Test
    void updateEntity_validFields_updateTaskItem() {
        //Arrange
        TaskItem taskItem = new TaskItem();
        TaskItemUpdateDto updateDto = new TaskItemUpdateDto("Go to Gym", false);

        //Act
        mapper.updateEntity(taskItem, updateDto);

        //Assertion
        Assertions.assertEquals(updateDto.getName(), taskItem.getName());
        Assertions.assertEquals(updateDto.getCompleted(), taskItem.isCompleted());
    }

    @Test
    void updateEntity_nameNull_shouldNotUpdateName() {
        //Arrange
        TaskItem taskItem = new TaskItem();
        taskItem.setName("OldName");
        TaskItemUpdateDto updateDto = new TaskItemUpdateDto(null, false);

        //Act
        mapper.updateEntity(taskItem, updateDto);

        //Assertion
        Assertions.assertEquals("OldName", taskItem.getName());
        Assertions.assertEquals(updateDto.getCompleted(), taskItem.isCompleted());
    }

    @Test
    void updateEntity_nameIsEmpty_shouldNotUpdateName() {
        //Arrange
        TaskItem taskItem = new TaskItem();
        taskItem.setName("OldName");
        TaskItemUpdateDto updateDto = new TaskItemUpdateDto(" ", false);

        //Act
        mapper.updateEntity(taskItem, updateDto);

        //Assertion
        Assertions.assertEquals("OldName", taskItem.getName());
        Assertions.assertEquals(updateDto.getCompleted(), taskItem.isCompleted());
    }

    @Test
    void updateEntity_completedIsNull_shouldNotUpdateCompleted() {
        //Arrange
        TaskItem taskItem = new TaskItem();
        taskItem.setCompleted(false);
        TaskItemUpdateDto updateDto = new TaskItemUpdateDto("Buy Coffee", null);

        //Act
        mapper.updateEntity(taskItem, updateDto);

        //Assertion
        Assertions.assertEquals(updateDto.getName(), taskItem.getName());
        Assertions.assertFalse(taskItem.isCompleted());
    }
}