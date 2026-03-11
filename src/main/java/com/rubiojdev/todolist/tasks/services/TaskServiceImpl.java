package com.rubiojdev.todolist.tasks.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.shared.exceptions.DuplicateResourceException;
import com.rubiojdev.todolist.shared.exceptions.EntityNotFoundException;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWithItemsResponseDto;
import com.rubiojdev.todolist.tasks.entities.Task;
import com.rubiojdev.todolist.tasks.mappers.TaskMapper;
import com.rubiojdev.todolist.tasks.repositories.TaskRepository;
import com.rubiojdev.todolist.users.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con {@link Task}.
 * <p>
 * Esta clase forma parte de la capa de servicio dentro de una arquitectura en capas,
 * y se encarga de coordinar las operaciones entre los controladores y la capa
 * de persistencia ({@link TaskRepository}).
 * <p>
 * Además, valida las reglas de negocio y garantiza que cada operación
 * se realice únicamente sobre los recursos (tareas) pertenecientes al usuario autenticado.
 * <p>
 * Responsabilidades principales:
 * <ul>
 *     <li>Crear nuevas tareas con validación de unicidad de nombre</li>
 *     <li>Recuperar tareas del usuario con paginación y búsqueda</li>
 *     <li>Buscar tareas específicas por nombre</li>
 *     <li>Actualizar tareas existentes</li>
 *     <li>Eliminar tareas del usuario</li>
 *     <li>Gestionar la relación entre tareas e items (subtareas)</li>
 * </ul>
 *
 * @see TaskService
 * @see TaskRepository
 * @see TaskMapper
 */
@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepository repository;
    private final TaskMapper mapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskMapper taskMapper) {

        this.mapper = taskMapper;
        this.repository = taskRepository;
    }

    /**
     * Obtiene todas las tareas del usuario autenticado con paginación.
     * <p>
     * Recupera las tareas asociadas al usuario, ordenadas por fecha de actualización
     * en orden descendente (las más recientes primero), con soporte para paginación.
     * <p>
     * Esta operación es de solo lectura para optimizar el rendimiento de la transacción.
     *
     * @param user usuario autenticado del cual se obtendrán las tareas
     * @param page número de página (0-indexed)
     * @param size cantidad de tareas por página
     * @return {@link PageResponse} que contiene la página de tareas convertidas a DTO
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponseDto> getAllTasks(User user, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Task> tasks = repository.findAllByUserOrderByUpdatedAtDesc(user, pageable);
        Page<TaskResponseDto> taskResponseDtos = tasks.map(mapper::toResponseDto);

        return PageResponse.toPage(taskResponseDtos);
    }

    /**
     * Obtiene una tarea específica del usuario junto con todos sus items (subtareas).
     * <p>
     * Recupera la tarea por su identificador, validando que pertenezca al usuario autenticado.
     * Incluye la información completa de la tarea y todos los items (subtareas) asociados.
     * <p>
     * Esta operación es de solo lectura para optimizar el rendimiento de la transacción.
     *
     * @param user usuario autenticado propietario de la tarea
     * @param id   identificador único de la tarea que se desea recuperar
     * @return {@link TaskWithItemsResponseDto} con la información completa de la tarea e items
     * @throws EntityNotFoundException si la tarea no existe o no pertenece al usuario autenticado
     */
    @Override
    @Transactional(readOnly = true)
    public TaskWithItemsResponseDto findTaskById(User user, Long id) {

        Task task = repository.findTaskWithItemsByIdAndUser(user, id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada o no pertenece al usuario"));

        return mapper.toResponseDtoWhitItem(task);
    }

    /**
     * Busca todas las tareas del usuario que contengan el nombre especificado.
     * <p>
     * Realiza una búsqueda case-insensitive (sin distinción de mayúsculas/minúsculas)
     * sobre el nombre de las tareas del usuario autenticado, con soporte para paginación.
     * <p>
     * Esta operación es de solo lectura para optimizar el rendimiento de la transacción.
     *
     * @param user usuario autenticado cuyas tareas serán filtradas
     * @param name fragmento del nombre de la tarea a buscar
     * @param page número de página (0-indexed)
     * @param size cantidad de tareas por página
     * @return {@link PageResponse} que contiene la página de tareas que coinciden con el criterio
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponseDto> findAllTaskByName(User user, String name, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = repository.findAllByNameContainingIgnoreCaseAndUser(name, user, pageable);
        Page<TaskResponseDto> taskResponseDtos = tasks.map(mapper::toResponseDto);

        return PageResponse.toPage(taskResponseDtos);
    }

    /**
     * Crea una nueva tarea asociada al usuario autenticado.
     * <p>
     * Realiza las siguientes validaciones y operaciones:
     * <ul>
     *     <li>Válida que el nombre de la tarea sea único dentro del contexto del usuario</li>
     *     <li>Mapea el DTO {@link TaskCreateDto} a la entidad {@link Task}</li>
     *     <li>Asocia la tarea al usuario autenticado</li>
     *     <li>Persiste la entidad en la base de datos</li>
     *     <li>Convierte la entidad almacenada a {@link TaskResponseDto}</li>
     * </ul>
     * <p>
     * <strong>Nota:</strong> Si ya existe una tarea con el mismo nombre para este usuario,
     * se lanza una {@link DuplicateResourceException}.
     *
     * @param user    usuario autenticado propietario de la nueva tarea
     * @param taskDto DTO que contiene los datos necesarios para crear la tarea
     * @return {@link TaskResponseDto} que representa la tarea creada
     * @throws DuplicateResourceException si una tarea con el mismo nombre
     *         ya existe para el usuario
     */
    @Override
    @Transactional
    public TaskResponseDto createNewTask(User user, TaskCreateDto taskDto) {

        if (repository.existsByNameIgnoreCaseAndUser(taskDto.getName(), user)) {
            throw new DuplicateResourceException("Ese nombre ya existe");
        }

        Task task = mapper.toEntity(taskDto);
        task.setUser(user);

        Task saved = repository.save(task);
        return mapper.toResponseDto(saved);
    }

    /**
     * Actualiza una tarea existente del usuario autenticado.
     * <p>
     * Realiza las siguientes validaciones y operaciones:
     * <ul>
     *     <li>Válida que el nuevo nombre (si se proporciona) sea único dentro del contexto del usuario</li>
     *     <li>Verifica que la tarea existe y pertenece al usuario autenticado</li>
     *     <li>Aplica los cambios del DTO {@link TaskUpdateDto} a la entidad {@link Task}</li>
     *     <li>Persiste los cambios en la base de datos</li>
     *     <li>Convierte la entidad actualizada a {@link TaskResponseDto}</li>
     * </ul>
     * <p>
     * <strong>Nota:</strong> La validación de unicidad del nombre excluye el ID de la tarea
     * actual para permitir que la tarea mantenga su nombre.
     *
     * @param user    usuario autenticado propietario de la tarea
     * @param id      identificador único de la tarea a actualizar
     * @param taskDto DTO que contiene los datos a actualizar
     * @return {@link TaskResponseDto} que representa la tarea actualizada
     * @throws EntityNotFoundException si la tarea no existe o no pertenece al usuario autenticado
     * @throws DuplicateResourceException si el nuevo nombre ya existe para otra tarea
     *         del usuario
     */
    @Override
    @Transactional
    public TaskResponseDto updateTask(User user, Long id, TaskUpdateDto taskDto) {

        if (taskDto.getName() != null &&
                repository.existsByNameIgnoreCaseAndUserAndIdNot(
                        taskDto.getName(), user, id
                )) {
            throw new DuplicateResourceException("Ese nombre ya existe");
        }

        Task task = repository.findTaskWithItemsByIdAndUser(user, id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada o no pertenece al usuario"));

        mapper.updateEntity(task, taskDto);

        return mapper.toResponseDto(task);
    }

    /**
     * Elimina una tarea del usuario autenticado.
     * <p>
     * Verifica que la tarea existe y pertenece al usuario autenticado antes de proceder
     * a su eliminación. La eliminación es en cascada, eliminando también todos los items
     * (subtareas) asociados a la tarea.
     * <p>
     * <strong>Advertencia:</strong> Esta operación es destructiva y definitiva.
     * Se recomienda que el usuario confirme su intención antes de ejecutarla.
     *
     * @param user usuario autenticado propietario de la tarea
     * @param id   identificador único de la tarea a eliminar
     * @throws EntityNotFoundException si la tarea no existe o no pertenece al usuario autenticado
     */
    @Override
    @Transactional
    public void deleteTask(User user, Long id) {
        Task task = repository.findTaskWithItemsByIdAndUser(user, id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada o no pertenece al usuario"));

        repository.delete(task);
    }
}
