package com.rubiojdev.todolist.taskitems.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.shared.exceptions.EntityNotFoundException;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import com.rubiojdev.todolist.taskitems.mappers.TaskItemMapperImpl;
import com.rubiojdev.todolist.taskitems.repositories.TaskItemRepository;
import com.rubiojdev.todolist.tasks.entities.Task;
import com.rubiojdev.todolist.tasks.repositories.TaskRepository;
import com.rubiojdev.todolist.users.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskItemServiceImplTest {

    @Mock
    private TaskItemMapperImpl mapper;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskItemRepository repository;
    @InjectMocks
    private TaskItemServiceImpl service;

    private int numPage;
    private int sizeOfPage;
    private User user;
    private Long taskId;
    private TaskItem taskItem1;
    private TaskItem taskItem2;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setId(1L);
        user.setUsername("User1");
        user.setEmail("user1@gmail.com");
        user.setPassword("1234");

        taskItem1 = new TaskItem();
        taskItem1.setId(1L);
        taskItem1.setName("Buy Milk");

        taskItem2 = new TaskItem();
        taskItem1.setId(2L);
        taskItem1.setName("Buy Coffee");

        taskId = 1L;

        numPage = 0;
        sizeOfPage = 5;
    }

    @Test
    void getItemsByTask_existingTaskItem_returnsPageResponse() {
        //Arrange
        List<TaskItem> taskItems = List.of(taskItem1, taskItem2);

        Pageable pageable = PageRequest.of(numPage, sizeOfPage);
        Page<TaskItem> itemPage = new PageImpl<>(taskItems, pageable, 10);

        when(repository.findByTaskIdAndTaskUserOrderByIdAsc(taskId, user, pageable)).thenReturn(itemPage);
        when(mapper.toResponseDto(any(TaskItem.class))).thenReturn(new TaskItemResponseDto());

        //Act
        PageResponse<TaskItemResponseDto> result = service.getItemsByTask(user, taskId, numPage, sizeOfPage);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertTrue(result.isFirst());
        Assertions.assertFalse(result.isLast());
        verify(repository).findByTaskIdAndTaskUserOrderByIdAsc(taskId, user, pageable);
        verify(mapper, times(2)).toResponseDto(any(TaskItem.class));
    }

    @Test
    void getItemsByTask_emptyTaskItems_returnsPageEmpty() {
        //Arrange
        Pageable pageable = PageRequest.of(numPage, sizeOfPage);
        Page<TaskItem> itemPage = Page.empty(pageable);

        when(repository.findByTaskIdAndTaskUserOrderByIdAsc(taskId, user, pageable)).thenReturn(itemPage);

        //Act
        PageResponse<TaskItemResponseDto> result = service.getItemsByTask(user, taskId, numPage, sizeOfPage);

        //Assert
        Assertions.assertTrue(result.getContent().isEmpty());
        verify(repository).findByTaskIdAndTaskUserOrderByIdAsc(taskId, user, pageable);
        verify(mapper, never()).toResponseDto(any(TaskItem.class));
    }

    @Test
    void getItemsByTask_multiplePages_returnsCorrectPaginationMetadata() {
        //Arrange
        int numPage = 1;

        List<TaskItem> taskItems = List.of(taskItem1, taskItem2);
        Pageable pageable = PageRequest.of(numPage, sizeOfPage);
        Page<TaskItem> itemPage = new PageImpl<>(taskItems, pageable, 15);

        when(repository.findByTaskIdAndTaskUserOrderByIdAsc(taskId, user, pageable)).thenReturn(itemPage);
        when(mapper.toResponseDto(any(TaskItem.class))).thenReturn(new TaskItemResponseDto());

        //Act
        PageResponse<TaskItemResponseDto> result = service.getItemsByTask(user, taskId, numPage, sizeOfPage);

        //Assert
        Assertions.assertFalse(result.isFirst());
        Assertions.assertFalse(result.isLast());

        verify(repository).findByTaskIdAndTaskUserOrderByIdAsc(taskId, user, pageable);
        verify(mapper, times(2)).toResponseDto(any(TaskItem.class));
    }

    @Test
    void createNewTaskItem_existingTask_returnsTaskItemResposeDto() {
        //Arrange
        TaskItemCreateDto createDto = new TaskItemCreateDto("Buy Milk");
        TaskItemResponseDto responseDto = new TaskItemResponseDto();
        Task task = new Task(user, "Compras");

        when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.of(task));
        when(mapper.toEntity(createDto)).thenReturn(taskItem1);
        when(repository.save(taskItem1)).thenReturn(taskItem1);
        when(mapper.toResponseDto(taskItem1)).thenReturn(responseDto);

        //Act
        TaskItemResponseDto result = service.createNewTaskItem(user, taskId, createDto);

        //Assert
        ArgumentCaptor<TaskItem> captor = ArgumentCaptor.forClass(TaskItem.class);
        verify(repository).save(captor.capture());

        TaskItem savedTaskItem = captor.getValue();
        Assertions.assertEquals(task,savedTaskItem.getTask());

        Assertions.assertNotNull(result);

        verify(taskRepository).findByIdAndUser(taskId, user);
        verify(mapper).toEntity(createDto);
        verify(mapper).toResponseDto(taskItem1);
    }

    @Test
    void createNewTaskItem_taskNotFound_throwEntityNotFoundException() {
        //Arrange
        TaskItemCreateDto createDto = new TaskItemCreateDto("Buy Milk");
        String expectedMessage = "La Tarea no existe o no pertenece al usuario";

        when(taskRepository.findByIdAndUser(taskId, user)).thenReturn(Optional.empty());

        //Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                service.createNewTaskItem(user, taskId, createDto));

        Assertions.assertEquals(expectedMessage, exception.getMessage());

        verify(taskRepository).findByIdAndUser(taskId, user);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void updateTaskItem_verifyingTaskItem_returnsTaskItemResponseDto() {
        //Arrange
        Long id = 1L;
        TaskItemUpdateDto updateDto = new TaskItemUpdateDto("Buy a book", false);
        TaskItemResponseDto responseDto = new TaskItemResponseDto(id, "Buy a book", false);

        when(repository.findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user)).thenReturn(Optional.of(taskItem1));
        when(mapper.toResponseDto(taskItem1)).thenReturn(responseDto);

        //Act
        TaskItemResponseDto result = service.updateTaskItem(user, taskId, id, updateDto);

        //Assert
        Assertions.assertEquals(responseDto.getName(), result.getName());
        Assertions.assertEquals(responseDto.isCompleted(), result.isCompleted());

        verify(repository).findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user);
        verify(mapper).updateEntity(taskItem1, updateDto);
        verify(mapper).toResponseDto(taskItem1);
    }

    @Test
    void updateTaskItem_taskItemNotFound_throwEntityNotFoundException() {
        //Arrange
        Long id = 1L;
        TaskItemUpdateDto updateDto = new TaskItemUpdateDto("Buy a book", false);

        when(repository.findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user)).thenReturn(Optional.empty());

        //Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                service.updateTaskItem(user, taskId, id, updateDto));

        verify(repository).findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user);
        verify(mapper, never()).updateEntity(any(), any());
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void deleteTaskItem_existingTaskItem_deleteTaskItem() {
        //Arrange
        Long id = 1L;

        when(repository.findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user)).thenReturn(Optional.of(taskItem1));

        //Act
        service.deleteTaskItem(user, taskId, id);

        //Assert
        verify(repository).findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user);
        verify(repository).delete(taskItem1);
    }

    @Test
    void deleteTaskItem_taskItemNotFound_throwEntityNotFoundException() {
        //Arrange
        Long id = 1L;
        String expectedMessage = "Subtarea no encontrada o no pertenece al usuario";

        when(repository.findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user)).thenReturn(Optional.empty());

        //Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                service.deleteTaskItem(user, taskId, id));

        Assertions.assertEquals(expectedMessage, exception.getMessage());

        verify(repository).findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user);
        verify(repository, never()).delete(any());
    }
}