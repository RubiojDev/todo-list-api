package com.rubiojdev.todolist.taskitems.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.shared.exceptions.EntityNotFoundException;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import com.rubiojdev.todolist.taskitems.mappers.TaskItemMapper;
import com.rubiojdev.todolist.taskitems.repositories.TaskItemRepository;
import com.rubiojdev.todolist.tasks.entities.Task;
import com.rubiojdev.todolist.tasks.repositories.TaskRepository;
import com.rubiojdev.todolist.users.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con {@link TaskItem}.
 * <p>
 * Esta clase forma parte de la capa de servicio dentro de una arquitectura en capas,
 * y se encarga de coordinar las operaciones entre los controladores y la capa
 * de persistencia ({@link TaskItemRepository}).
 * <p>
 * Además, valida las reglas de negocio y garantiza que cada operación
 * se realice únicamente sobre los recursos (items/subtareas) pertenecientes a tareas
 * del usuario autenticado.
 * <p>
 * Responsabilidades principales:
 * <ul>
 *     <li>Crear nuevos items (subtareas) asociados a una tarea del usuario</li>
 *     <li>Recuperar items de una tarea específica con paginación</li>
 *     <li>Actualizar items existentes</li>
 *     <li>Eliminar items asociados a tareas del usuario</li>
 *     <li>Validar que los items pertenecen a tareas del usuario autenticado</li>
 * </ul>
 *
 * @see TaskItemService
 * @see TaskItemRepository
 * @see TaskItemMapper
 */
@Service
public class TaskItemServiceImpl implements TaskItemService{

    private final TaskItemMapper mapper;
    private final TaskRepository taskRepository;
    private final TaskItemRepository repository;

    @Autowired
    public TaskItemServiceImpl(TaskItemMapper mapper,
                               TaskRepository taskRepository,
                               TaskItemRepository repository) {

        this.mapper = mapper;
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    /**
     * Obtiene todos los items (subtareas) asociados a una tarea específica con paginación.
     * <p>
     * Recupera los items de la tarea especificada, validando que la tarea pertenezca
     * al usuario autenticado. Los resultados se ordenan por identificador en orden ascendente.
     * <p>
     * Esta operación es de solo lectura para optimizar el rendimiento de la transacción.
     *
     * @param user   usuario autenticado propietario de la tarea
     * @param taskId identificador único de la tarea cuyos items se desean obtener
     * @param page   número de página (0-indexed)
     * @param size   cantidad de items por página
     * @return {@link PageResponse} que contiene la página de items convertidos a DTO
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskItemResponseDto> getItemsByTask(User user, Long taskId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TaskItem> taskItems = repository.findByTaskIdAndTaskUserOrderByIdAsc(taskId, user, pageable);
        Page<TaskItemResponseDto> responseDtoList = taskItems.map(mapper::toResponseDto);

        return PageResponse.toPage(responseDtoList);
    }

    /**
     * Crea un nuevo item (subtarea) asociado a una tarea del usuario autenticado.
     * <p>
     * Realiza las siguientes validaciones y operaciones:
     * <ul>
     *     <li>Valida que la tarea existe y pertenece al usuario autenticado</li>
     *     <li>Mapea el DTO {@link TaskItemCreateDto} a la entidad {@link TaskItem}</li>
     *     <li>Asocia el item a la tarea especificada</li>
     *     <li>Persiste la entidad en la base de datos</li>
     *     <li>Convierte la entidad almacenada a {@link TaskItemResponseDto}</li>
     * </ul>
     *
     * @param user                usuario autenticado propietario de la tarea
     * @param taskId              identificador único de la tarea a la cual se asociará el item
     * @param taskItemCreateDto   DTO que contiene los datos necesarios para crear el item
     * @return {@link TaskItemResponseDto} que representa el item creado
     * @throws EntityNotFoundException si la tarea no existe o no pertenece al usuario autenticado
     */
    @Override
    @Transactional
    public TaskItemResponseDto createNewTaskItem(User user, Long taskId, TaskItemCreateDto taskItemCreateDto) {

        Task task = taskRepository.findByIdAndUser(taskId, user)
                .orElseThrow(() ->
                        new EntityNotFoundException("La Tarea no existe o no pertenece al usuario")
                );

        TaskItem taskItem = mapper.toEntity(taskItemCreateDto);
        taskItem.setTask(task);

        TaskItem saved = repository.save(taskItem);
        task.touch();
        return mapper.toResponseDto(saved);
    }

    /**
     * Actualiza un item (subtarea) existente asociado a una tarea del usuario autenticado.
     * <p>
     * Realiza las siguientes validaciones y operaciones:
     * <ul>
     *     <li>Verifica que el item existe, pertenece a la tarea especificada
     *         y la tarea pertenece al usuario autenticado</li>
     *     <li>Aplica los cambios del DTO {@link TaskItemUpdateDto} a la entidad {@link TaskItem}</li>
     *     <li>Persiste los cambios en la base de datos</li>
     *     <li>Convierte la entidad actualizada a {@link TaskItemResponseDto}</li>
     * </ul>
     *
     * @param user                usuario autenticado propietario de la tarea
     * @param taskId              identificador único de la tarea propietaria del item
     * @param id                  identificador único del item a actualizar
     * @param taskItemUpdateDto   DTO que contiene los datos a actualizar
     * @return {@link TaskItemResponseDto} que representa el item actualizado
     * @throws EntityNotFoundException si el item no existe, no pertenece a la tarea especificada
     *         o la tarea no pertenece al usuario autenticado
     */
    @Override
    @Transactional
    public TaskItemResponseDto updateTaskItem(User user, Long taskId, Long id,
                                              TaskItemUpdateDto taskItemUpdateDto) {

        TaskItem taskItem =repository.findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user)
                .orElseThrow(() -> new EntityNotFoundException("Subtarea no encontrada o no pertenece al usuario"));

        mapper.updateEntity(taskItem, taskItemUpdateDto);
        taskItem.getTask().touch();

        return mapper.toResponseDto(taskItem);
    }

    /**
     * Elimina un item (subtarea) asociado a una tarea del usuario autenticado.
     * <p>
     * Verifica que el item existe, pertenece a la tarea especificada y la tarea
     * pertenece al usuario autenticado antes de proceder a su eliminación.
     * <p>
     *
     * @param user   usuario autenticado propietario de la tarea
     * @param taskId identificador único de la tarea propietaria del item
     * @param id     identificador único del item a eliminar
     * @throws EntityNotFoundException si el item no existe, no pertenece a la tarea especificada
     *         o la tarea no pertenece al usuario autenticado
     */
    @Override
    @Transactional
    public void deleteTaskItem(User user, Long taskId, Long id) {

        TaskItem taskItem = repository.findTaskItemByIdAndTaskIdAndTaskUser(id, taskId, user)
                .orElseThrow(() -> new EntityNotFoundException("Subtarea no encontrada o no pertenece al usuario"));

        taskItem.getTask().touch();
        repository.delete(taskItem);
    }
}
