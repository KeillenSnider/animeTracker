package com.keillen.ShelfLog.repositories;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.keillen.ShelfLog.entities.Status;

/**
 * Databse access for the Status rows.
 * Spring generates the implementation at startup
 * findByName is used by StatusSeeder to check for existing rows.
 */

//Checks if the specific status exists for each name and inserts the ones that don't
//2 Parameters are what it manages which is the status and the type of it's @ID which is Integer
public interface StatusRepository extends JpaRepository<Status, Integer> {
    //Optional means might be there might not
    //FindByName generates SQL to compare and see if it connects
    //<which table> Find = Select ByName = Where name ? and ? = String name
    Optional<Status> findByName(String name);
}