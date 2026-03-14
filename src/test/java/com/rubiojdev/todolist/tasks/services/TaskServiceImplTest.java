package com.rubiojdev.todolist.tasks.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.shared.exceptions.DuplicateResourceException;
import com.rubiojdev.todolist.shared.exceptions.EntityNotFoundException;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWithItemsResponseDto;
import com.rubiojdev.todolist.tasks.entities.Task;
import com.rubiojdev.todolist.tasks.mappers.TaskMapper;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;
    @Mock
    private TaskMapper mapper;
    @InjectMocks
    private TaskServiceImpl service;

    private User user;
    private Task task1;
    private Task task2;
    private int numPage;
    private int sizeOfPage;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("User1");
        user.setEmail("user1@gmail.com");
        user.setPassword("1234");

        task1 = new Task();
        task1.setId(1L);
        task1.setUser(user);
        task1.setName("Tarea1");
        task1.setCompleted(false);

        task2 = new Task();
        task2.setId(2L);
        task2.setUser(user);
        task2.setName("Tarea2");
        task2.setCompleted(false);

        numPage = 0;
        sizeOfPage = 5;
    }

    @Test
    void getAllTasks_existingTasks_returnsPageResponse() {
        //Arrange
        List<Task> tasks = List.of(task1, task2);
        Pageable pageable = PageRequest.of(numPage, sizeOfPage);
        Page<Task> pageTask = new PageImpl<>(tasks, pageable, tasks.size());

        when(repository.findAllByUserOrderByUpdatedAtDesc(user, pageable)).thenReturn(pageTask);
        when(mapper.toResponseDto(any(Task.class))).thenReturn(new TaskResponseDto());

        //Act
        PageResponse<TaskResponseDto> result = service.getAllTasks(user, numPage, sizeOfPage);

        //Assert
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(numPage, result.getPage());
        Assertions.assertEquals(sizeOfPage, result.getSize());
        Assertions.assertTrue(result.isFirst());
        Assertions.assertTrue(result.isLast());

        verify(repository).findAllByUserOrderByUpdatedAtDesc(user, pageable);
        verify(mapper, times(2)).toResponseDto(any(Task.class));
    }

    @Test
    void getAllTasks_emptyTasks_returnsPageEmpty() {
        //Arrange
        Pageable pageable = PageRequest.of(numPage, sizeOfPage);
        Page<Task> pageTask = Page.empty(pageable);

        when(repository.findAllByUserOrderByUpdatedAtDesc(user, pageable)).thenReturn(pageTask);

        //Act
        PageResponse<TaskResponseDto> result = service.getAllTasks(user, numPage, sizeOfPage);

        //Assert
        Assertions.assertTrue(result.getContent().isEmpty());

        verify(repository).findAllByUserOrderByUpdatedAtDesc(user, pageable);
        verify(mapper, never()).toResponseDto(any(Task.class));
    }

    @Test
    void getAllTasks_multiplePages_returnsCorrectPaginationMetadata() {
        //Arrange
        int page = 1;

        List<Task> tasks = List.of(task1, task2);
        Pageable pageable = PageRequest.of(page, sizeOfPage);
        Page<Task> pageTask = new PageImpl<>(tasks, pageable, 15);

        when(repository.findAllByUserOrderByUpdatedAtDesc(user, pageable)).thenReturn(pageTask);
        when(mapper.toResponseDto(any(Task.class))).thenReturn(new TaskResponseDto());

        //Act
        PageResponse<TaskResponseDto> result = service.getAllTasks(user, page, sizeOfPage);

        //Assert
        Assertions.assertFalse(result.isFirst());
        Assertions.assertFalse(result.isLast());

        verify(repository).findAllByUserOrderByUpdatedAtDesc(user, pageable);
        verify(mapper, times(2)).toResponseDto(any(Task.class));
    }

    @Test
    void findTaskById_existingTask_returnsTaskWithItems() {
        //Arrange
        Long userId = 1L;

        task1.setUpdatedAt(Instant.parse("2026-02-10T11:00:00Z"));
        List<TaskItemResponseDto> taskItemResponseDtoList = List.of(new TaskItemResponseDto());
        TaskWithItemsResponseDto responseDto = new TaskWithItemsResponseDto(
                task1.getId(), task1.getName(), task1.getUpdatedAt(), task1.isCompleted(), taskItemResponseDtoList);

        when(repository.findTaskWithItemsByIdAndUser(user, userId)).thenReturn(Optional.of(task1));

        when(mapper.toResponseDtoWhitItem(task1)).thenReturn(responseDto);

        //Act
        TaskWithItemsResponseDto result = service.findTaskById(user, userId);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTaskItemDtoList().size());
        Assertions.assertEquals(task1.getId(), result.getId());
        Assertions.assertEquals(task1.getName(), result.getName());
        Assertions.assertEquals(task1.getUpdatedAt(), result.getUpdatedAt());
        Assertions.assertFalse(result.isCompleted());

        verify(repository).findTaskWithItemsByIdAndUser(user, userId);
        verify(mapper).toResponseDtoWhitItem(task1);
    }

    @Test
    void findTaskById_taskNotFound_throwEntityNotFoundException() {
        //Arrange
        Long userId = 2L;
        String expectedMessage = "Tarea no encontrada o no pertenece al usuario";

        when(repository.findTaskWithItemsByIdAndUser(user, userId)).thenReturn(Optional.empty());

        //Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                service.findTaskById(user, userId));

        Assertions.assertEquals(expectedMessage, exception.getMessage());

        verify(repository).findTaskWithItemsByIdAndUser(user, userId);
        verify(mapper, never()).toResponseDtoWhitItem(any(Task.class));
    }

    @Test
    void findAllTaskByName_existingTasksByName_returnsPageResponse() {
        //Arrange
        String name = "Tarea1";

        List<Task> tasks = List.of(task1, task2);
        Pageable pageable = PageRequest.of(numPage, sizeOfPage);
        Page<Task> pageTask = new PageImpl<>(tasks, pageable, 5);

        when(repository.findAllByNameContainingIgnoreCaseAndUser(name, user, pageable))
                .thenReturn(pageTask);

        when(mapper.toResponseDto(any(Task.class))).thenReturn(new TaskResponseDto());

        //Act
        PageResponse<TaskResponseDto> result = service.findAllTaskByName(user, name, numPage, sizeOfPage);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(numPage, result.getPage());
        Assertions.assertEquals(sizeOfPage, result.getSize());
        Assertions.assertTrue(result.isFirst());
        Assertions.assertTrue(result.isLast());

        verify(repository).findAllByNameContainingIgnoreCaseAndUser(name, user, pageable);
        verify(mapper, times(2)).toResponseDto(any(Task.class));
    }

    @Test
    void findAllTaskByName_noMatches_returnsPageResponseEmpty() {
        //Arrange
        String name = "Tarea1";

        Pageable pageable = PageRequest.of(numPage, sizeOfPage);
        Page<Task> pageTask = Page.empty(pageable);

        when(repository.findAllByNameContainingIgnoreCaseAndUser(name, user, pageable)).thenReturn(pageTask);

        //Act
        PageResponse<TaskResponseDto> result = service.findAllTaskByName(user, name, numPage, sizeOfPage);

        //Assert
        Assertions.assertTrue(result.getContent().isEmpty());

        verify(repository).findAllByNameContainingIgnoreCaseAndUser(name, user, pageable);
        verify(mapper, never()).toResponseDto(any(Task.class));
    }

    @Test
    void createNewTask_nameNotFound_returnsTaskResponseDto() {
        //Arrange
        TaskCreateDto taskCreateDto = new TaskCreateDto("Tarea1");
        Task task = new Task();
        task.setName("Tarea1");
        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setName("Tarea1");

        when(repository.existsByNameIgnoreCaseAndUser(taskCreateDto.getName(), user))
                .thenReturn(false);

        when(mapper.toEntity(taskCreateDto)).thenReturn(task);
        when(repository.save(task)).thenReturn(task);
        when(mapper.toResponseDto(task)).thenReturn(responseDto);

        //Act
        TaskResponseDto result = service.createNewTask(user, taskCreateDto);

        //Assert
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(repository).save(captor.capture());

        Task savedTask = captor.getValue();
        Assertions.assertEquals(user, savedTask.getUser());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(taskCreateDto.getName(), result.getName());

        verify(repository).existsByNameIgnoreCaseAndUser(taskCreateDto.getName(), user);
        verify(mapper).toEntity(taskCreateDto);
        verify(mapper).toResponseDto(task);
    }

    @Test
    void createNewTask_taskAlreadyCreated_trhowDuplicateResourceException() {
        //Arrange
        TaskCreateDto taskCreateDto = new TaskCreateDto("Tarea1");

        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setName("Tarea1");

        String expectedMessage = "Ese nombre ya existe";

        when(repository.existsByNameIgnoreCaseAndUser(taskCreateDto.getName(), user))
                .thenReturn(true);

        //Act & //Assert
        DuplicateResourceException exception = Assertions.assertThrows(DuplicateResourceException.class, () ->
                service.createNewTask(user, taskCreateDto));

        Assertions.assertEquals(expectedMessage, exception.getMessage());

        verify(repository).existsByNameIgnoreCaseAndUser(taskCreateDto.getName(), user);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void updateTask_verifyingTask_returnsTaskResponseDto() {
        //Arrange
        Long taskId = 1L;
        Task task = new Task(user, "Tarea1");
        TaskUpdateDto updateDto = new TaskUpdateDto("Tarea1", false);
        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setName("Tarea1");
        responseDto.setCompleted(false);

        when(repository.existsByNameIgnoreCaseAndUserAndIdNot(updateDto.getName(), user, taskId))
                .thenReturn(false);

        when(repository.findTaskWithItemsByIdAndUser(user, taskId)).thenReturn(Optional.of(task));
        when(mapper.toResponseDto(task)).thenReturn(responseDto);

        //Act
        TaskResponseDto result = service.updateTask(user, taskId, updateDto);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(updateDto.getName(), result.getName());
        Assertions.assertEquals(updateDto.getCompleted(), result.isCompleted());

        verify(repository).existsByNameIgnoreCaseAndUserAndIdNot("Tarea1", user, taskId);
        verify(repository).findTaskWithItemsByIdAndUser(user, taskId);
        verify(mapper).updateEntity(task, updateDto);
        verify(mapper).toResponseDto(task);
    }

    @Test
    void updateTask_nameIsNull_shouldNotValidateDuplicate() {
        //Arrange
        Long taskId = 1L;
        TaskUpdateDto updateDto = new TaskUpdateDto(null, false);
        TaskResponseDto responseDto = new TaskResponseDto();

        when(repository.findTaskWithItemsByIdAndUser(user, taskId)).thenReturn(Optional.of(task1));
        when(mapper.toResponseDto(task1)).thenReturn(responseDto);

        //Act
        TaskResponseDto result = service.updateTask(user, taskId, updateDto);

        //Assert
        Assertions.assertNotNull(result);

        verify(repository, never()).existsByNameIgnoreCaseAndUserAndIdNot(any(), any(), any());
        verify(repository).findTaskWithItemsByIdAndUser(user, taskId);
        verify(mapper).updateEntity(task1, updateDto);
        verify(mapper).toResponseDto(task1);
    }

    @Test
    void updateTask_existingName_throwDuplicateResourceException() {
        //Arrange
        Long taskId = 1L;
        TaskUpdateDto updateDto = new TaskUpdateDto("Tarea1", false);
        String expectedMessage = "Ese nombre ya existe";

        when(repository.existsByNameIgnoreCaseAndUserAndIdNot(updateDto.getName(), user, taskId))
                .thenReturn(true);

        //Act & Assert
        DuplicateResourceException exception = Assertions.assertThrows(DuplicateResourceException.class, () ->
                service.updateTask(user, taskId, updateDto));

        Assertions.assertEquals(expectedMessage, exception.getMessage());

        verify(repository).existsByNameIgnoreCaseAndUserAndIdNot("Tarea1", user, taskId);
        verify(repository, never()).findTaskWithItemsByIdAndUser(any(), any());
        verify(mapper, never()).updateEntity(any(), any());
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void updateTask_taskNotFound_throwEntityNotFoundException() {
        //Arrange
        Long taskId = 1L;
        TaskUpdateDto updateDto = new TaskUpdateDto("Tarea1", false);
        String expectedMessage = "Tarea no encontrada o no pertenece al usuario";

        when(repository.existsByNameIgnoreCaseAndUserAndIdNot(updateDto.getName(), user, taskId))
                .thenReturn(false);

        when(repository.findTaskWithItemsByIdAndUser(user, taskId)).thenReturn(Optional.empty());

        //Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                service.updateTask(user, taskId, updateDto));

        Assertions.assertEquals(expectedMessage, exception.getMessage());

        verify(repository).existsByNameIgnoreCaseAndUserAndIdNot("Tarea1", user, taskId);
        verify(repository).findTaskWithItemsByIdAndUser(user, taskId);
        verify(mapper, never()).updateEntity(any(), any());
        verify(mapper, never()).toResponseDto(any());
    }

    @Test
    void deleteTask_existingTask_deletedTask() {
        //Arrange
        Long taskId = 1L;

        when(repository.findTaskWithItemsByIdAndUser(user, taskId)).thenReturn(Optional.of(task1));

        //Act
        service.deleteTask(user, taskId);

        //Assert
        verify(repository).findTaskWithItemsByIdAndUser(user, taskId);
        verify(repository).delete(task1);
    }

    @Test
    void deleteTask_taskNotFound_throwEntityNotFoundException() {
        //Arrange
        Long taskId = 1L;
        String expectedMessage = "Tarea no encontrada o no pertenece al usuario";

        when(repository.findTaskWithItemsByIdAndUser(user, taskId)).thenReturn(Optional.empty());

        //Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                service.deleteTask(user, taskId));

        Assertions.assertEquals(expectedMessage, exception.getMessage());

        verify(repository).findTaskWithItemsByIdAndUser(user, taskId);
        verify(repository, never()).delete(any(Task.class));
    }
}