package com.example.springBoot2.controllers;

import com.example.springBoot2.models.Movie;
import com.example.springBoot2.repositories.MovieRepository;
import org.springframework.web.bind.annotation.*;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository repo;

    public MovieController(MovieRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Movie> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Movie> one(@PathVariable Integer id) {
        return repo.findById(id);
    }

    //Add a movie by title, then auto-generate & save its description via Gemini.
    @PostMapping
    public Movie create(@RequestBody Movie m) {
        Movie saved = repo.save(m);

        Client client = Client.builder().apiKey(System.getenv("GOOGLE_API_KEY")).build();

        String prompt = String.format("Write a concise, engaging movie description for the film titled \"%s\".", saved.getTitle());

        GenerateContentResponse resp = client.models.generateContent("gemini-2.0-flash-001", prompt, null);

        saved.setDescription(resp.text());
        return repo.save(saved);
    }

    @PatchMapping("/{id}")
    public Movie setDescription(@PathVariable Integer id, @RequestBody Movie update) {
        return repo.findById(id).map(m -> {
            m.setDescription(update.getDescription());
            return repo.save(m);
        }).orElseThrow(() -> new RuntimeException("Movie not found"));
    }
}