package com.rubiojdev.todolist.taskitems.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.users.entities.User;

import java.util.List;

/**
 * Contrato del servicio encargado de gestionar las operaciones relacionadas
 * con los items o subtareas ({@code TaskItem}) dentro del sistema.
 *
 * <p>Define las operaciones disponibles para crear, consultar, actualizar
 * y eliminar subtareas asociadas a una tarea del usuario autenticado.</p>
 *
 * <p>Las implementaciones de este servicio deben garantizar que todas las
 * operaciones se realicen únicamente sobre recursos pertenecientes al
 * usuario autenticado.</p>
 *
 * <p>Este servicio forma parte de la capa de negocio dentro de una
 * arquitectura en capas y es utilizado por los controladores para
 * interactuar con la lógica de gestión de subtareas.</p>
 */
public interface TaskItemService {

    /**
     * Obtiene los items (subtareas) asociados a una tarea específica
     * perteneciente al usuario autenticado.
     *
     * @param user usuario autenticado
     * @param taskId identificador de la tarea
     * @param page número de página (0-indexed)
     * @param size cantidad de elementos por página
     * @return página de subtareas
     */
    PageResponse<TaskItemResponseDto> getItemsByTask(User user, Long taskId, int page, int size);

    /**
     * Crea un nuevo item (subtarea) asociado a una tarea del usuario.
     *
     * @param user usuario autenticado
     * @param taskId identificador de la tarea
     * @param taskItemCreateDto datos necesarios para crear la subtarea
     * @return subtarea creada
     */
    TaskItemResponseDto createNewTaskItem(User user, Long taskId,
                                                  TaskItemCreateDto taskItemCreateDto);

    /**
     * Actualiza un item (subtarea) existente perteneciente a una tarea del usuario.
     *
     * @param user usuario autenticado
     * @param taskId identificador de la tarea
     * @param id identificador del item
     * @param taskItemUpdateDto datos a actualizar
     * @return subtarea actualizada
     */
    TaskItemResponseDto updateTaskItem(User user, Long taskId, Long id,
                                       TaskItemUpdateDto taskItemUpdateDto);

    /**
     * Elimina un item (subtarea) asociado a una tarea del usuario.
     *
     * @param user usuario autenticado
     * @param taskId identificador de la tarea
     * @param id identificador del item
     */
    void deleteTaskItem(User user, Long taskId, Long id);
}
