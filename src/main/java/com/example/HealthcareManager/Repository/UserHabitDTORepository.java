package com.example.HealthcareManager.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.HealthcareManager.DTO.UserHabitDTO;
import com.example.HealthcareManager.Model.Habit;

@Repository
public interface UserHabitDTORepository extends JpaRepository<Habit, Integer> {

    @Query("SELECT new com.example.HealthcareManager.DTO.UserHabitDTO(u.id, u.weight, u.height, u.gender, u.dateOfBirth, h.alcohol, h.cigarette, h.areca) "
            + "FROM User u JOIN Habit h ON u.id = h.user.id WHERE u.id = :userId")
    UserHabitDTO findUserHabitDTObyUserId(@Param("userId") String userId);

}
