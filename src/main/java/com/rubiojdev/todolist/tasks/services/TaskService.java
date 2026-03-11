package com.rubiojdev.todolist.tasks.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWithItemsResponseDto;
import com.rubiojdev.todolist.users.entities.User;

/**
 * Define las operaciones disponibles para la gestión de {@link com.rubiojdev.todolist.tasks.entities.Task}.
 *
 * <p>Esta interfaz forma parte de la capa de servicio y establece el contrato
 * que deben cumplir las implementaciones encargadas de manejar la lógica
 * de negocio relacionada con las tareas.</p>
 *
 * <p>Las operaciones definidas aquí permiten crear, consultar, actualizar
 * y eliminar tareas pertenecientes al usuario autenticado, además de
 * recuperar tareas con paginación y realizar búsquedas por nombre.</p>
 *
 * <p>Las implementaciones deben garantizar que todas las operaciones
 * se realicen únicamente sobre tareas pertenecientes al usuario
 * proporcionado.</p>
 *
 * @see com.rubiojdev.todolist.tasks.entities.Task
 */
public interface TaskService {

    /**
     * Obtiene todas las tareas pertenecientes al usuario autenticado
     * utilizando paginación.
     *
     * @param user usuario propietario de las tareas
     * @param page número de página (comenzando desde 0)
     * @param size cantidad de elementos por página
     * @return {@link PageResponse} que contiene la lista paginada de tareas
     */
    PageResponse<TaskResponseDto> getAllTasks(User user, int page, int size);

    /**
     * Obtiene una tarea específica junto con todos sus items (subtareas).
     *
     * @param user usuario propietario de la tarea
     * @param id identificador único de la tarea
     * @return {@link TaskWithItemsResponseDto} con la información completa de la tarea
     */
    TaskWithItemsResponseDto findTaskById(User user, Long id);

    /**
     * Busca tareas del usuario cuyo nombre contenga el texto especificado.
     * La búsqueda es case-insensitive.
     *
     * @param user usuario propietario de las tareas
     * @param name fragmento del nombre a buscar
     * @param page número de página (comenzando desde 0)
     * @param size cantidad de elementos por página
     * @return {@link PageResponse} con las tareas que coinciden con el criterio
     */
    PageResponse<TaskResponseDto> findAllTaskByName(User user, String name, int page, int size);

    /**
     * Crea una nueva tarea asociada al usuario autenticado.
     *
     * @param user usuario propietario de la nueva tarea
     * @param taskDto datos necesarios para la creación de la tarea
     * @return {@link TaskResponseDto} que representa la tarea creada
     */
    TaskResponseDto createNewTask(User user, TaskCreateDto taskDto);

    /**
     * Actualiza una tarea existente del usuario autenticado.
     *
     * @param user usuario propietario de la tarea
     * @param id identificador de la tarea a actualizar
     * @param taskDto datos a modificar en la tarea
     * @return {@link TaskResponseDto} con la información actualizada
     */
    TaskResponseDto updateTask(User user, Long id, TaskUpdateDto taskDto);

    /**
     * Elimina una tarea del usuario autenticado.
     *
     * @param user usuario propietario de la tarea
     * @param id identificador de la tarea a eliminar
     */
    void deleteTask(User user, Long id);
}
