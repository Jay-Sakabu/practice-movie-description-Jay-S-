package com.example.springBoot2.repositories;

import com.example.springBoot2.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
