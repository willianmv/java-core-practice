package com.example.management.infrastructure.config;

import com.example.management.core.gateway.BoardGateway;
import com.example.management.core.gateway.ColumnGateway;
import com.example.management.core.gateway.TaskGateway;
import com.example.management.core.usecase.board.*;
import com.example.management.core.usecase.task.*;
import com.example.management.infrastructure.gateway.BoardRepositoryGateway;
import com.example.management.infrastructure.gateway.ColumnRepositoryGateway;
import com.example.management.infrastructure.gateway.TaskRepositoryGateway;
import com.example.management.infrastructure.persistence.db.JdbcBoardRepository;
import com.example.management.infrastructure.persistence.db.JdbcColumnRepository;
import com.example.management.infrastructure.persistence.db.JdbcTaskRepository;
import com.example.management.infrastructure.persistence.file.InFileBoardRepository;
import com.example.management.infrastructure.persistence.file.InFileColumnRepository;
import com.example.management.infrastructure.persistence.file.InFileTaskRepository;
import com.example.management.infrastructure.persistence.memory.InMemoryBoardRepository;
import com.example.management.infrastructure.persistence.memory.InMemoryColumnRepository;
import com.example.management.infrastructure.persistence.memory.InMemoryTaskRepository;

import java.util.HashMap;
import java.util.Map;

public class AppContext {

    private final Map<Class<?>, Object> container = new HashMap<>();
    private static AppContext instance;

    private AppContext(){

        //Repositories - In Memory
        InMemoryBoardRepository inMemoryBoardRepository = new InMemoryBoardRepository();
        register(InMemoryBoardRepository.class, inMemoryBoardRepository);

        InMemoryColumnRepository inMemoryColumnRepository = new InMemoryColumnRepository();
        register(InMemoryColumnRepository.class, inMemoryColumnRepository);

        InMemoryTaskRepository inMemoryTaskRepository = new InMemoryTaskRepository();
        register(InMemoryTaskRepository.class, inMemoryTaskRepository);

        //Repositories - In File
        InFileBoardRepository inFileBoardRepository = new InFileBoardRepository();
        register(InFileBoardRepository.class, inFileBoardRepository);

        InFileColumnRepository inFileColumnRepository = new InFileColumnRepository(inFileBoardRepository);
        register(InFileColumnRepository.class, inFileColumnRepository);

        InFileTaskRepository inFileTaskRepository = new InFileTaskRepository(inFileColumnRepository);
        register(InFileTaskRepository.class, inFileTaskRepository);

        //Repositories - In Relation Data Base
        JdbcBoardRepository jdbcBoardRepository = new JdbcBoardRepository();
        register(JdbcBoardRepository.class, jdbcBoardRepository);

        JdbcColumnRepository jdbcColumnRepository = new JdbcColumnRepository();
        register(JdbcColumnRepository.class, jdbcColumnRepository);

        JdbcTaskRepository jdbcTaskRepository = new JdbcTaskRepository();
        register(JdbcTaskRepository.class, jdbcTaskRepository);

        //Listeners - On cascade delete simulation - List insert order important !!!
        inFileBoardRepository.addListener(inFileTaskRepository);
        inFileBoardRepository.addListener(inFileColumnRepository);

        //Gateways
        BoardGateway boardGateway = new BoardRepositoryGateway(jdbcBoardRepository);
        register(BoardGateway.class, boardGateway);

        ColumnGateway columnGateway = new ColumnRepositoryGateway(jdbcColumnRepository);
        register(ColumnGateway.class, columnGateway);

        TaskGateway taskGateway = new TaskRepositoryGateway(jdbcTaskRepository);
        register(TaskGateway.class, taskGateway);

        //Use Case - Board
        CompleteBoardUseCase completeBoardUseCase = new CompleteBoardUseCaseImpl(boardGateway, columnGateway, taskGateway);
        register(CompleteBoardUseCase.class, completeBoardUseCase);

        CreateBoardUseCase createBoardUseCase = new CreateBoardUseCaseImpl(boardGateway, columnGateway);
        register(CreateBoardUseCase.class, createBoardUseCase);

        UpdateBoardUseCase updateBoardUseCase = new UpdateBoardUseCaseImpl(boardGateway);
        register(UpdateBoardUseCase.class, updateBoardUseCase);

        DeleteBoardUseCase deleteBoardUseCase = new DeleteBoardUseCaseImpl(boardGateway);
        register(DeleteBoardUseCase.class, deleteBoardUseCase);

        //Use Case - Task
        CreateTaskUseCase createTaskUseCase = new CreateTaskUseCaseImpl(taskGateway, columnGateway);
        register(CreateTaskUseCase.class, createTaskUseCase);

        UpdateTaskUseCase updateTaskUseCase = new UpdateTaskUseCaseImpl(taskGateway);
        register(UpdateTaskUseCase.class, updateTaskUseCase);

        DeleteTaskUseCase deleteTaskUseCase = new DeleteTaskUseCaseImpl(taskGateway);
        register(DeleteTaskUseCase.class, deleteTaskUseCase);

        BlockTaskUseCase blockTaskUseCase = new BlockTaskUseCaseImpl(taskGateway);
        register(BlockTaskUseCase.class, blockTaskUseCase);

        UnblockTaskUseCase unblockTaskUseCase = new UnblockTaskUseCaseImpl(taskGateway);
        register(UnblockTaskUseCase.class, unblockTaskUseCase);

        MoveTaskUseCase moveTaskUseCase = new MoveTaskUseCaseImpl(taskGateway, columnGateway);
        register(MoveTaskUseCase.class, moveTaskUseCase);

    }

    public <T> void register(Class<T> clazz, T instance){
        container.put(clazz, instance);
    }

    public <T> T get(Class<T> clazz){
        Object obj = container.get(clazz);
        if(obj == null){
            throw new RuntimeException("No instance for: "+clazz.getSimpleName());
        }
        return clazz.cast(obj);
    }

    public static AppContext getInstance(){
        if(instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

}
