package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByIdIn(Long[] ids, PageRequest pageRequest);

    boolean existsById(Long id);
}
