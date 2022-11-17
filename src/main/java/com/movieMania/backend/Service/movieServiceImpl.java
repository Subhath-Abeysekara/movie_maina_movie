package com.movieMania.backend.Service;

import com.movieMania.backend.Entity.movie;
import com.movieMania.backend.Entity.movieUpdateData;
import com.movieMania.backend.Entity.roles;
import com.movieMania.backend.Repository.rolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class movieServiceImpl implements movieService{

    @Autowired
    private com.movieMania.backend.Repository.movieRepository movieRepository;

    @Autowired
    private rolesRepository rolesRepository;


    RestTemplate restTemplate = new RestTemplate();


    private String dateInCount(){
        Date date = new Date();
        SimpleDateFormat sdt = new SimpleDateFormat("dd");
        SimpleDateFormat sdt2 = new SimpleDateFormat("MM");
        SimpleDateFormat sdt3 = new SimpleDateFormat("YYYY");
        //SimpleDateFormat sdt5 = new SimpleDateFormat("hh");

        String day= sdt.format(date);
        String month = sdt2.format(date);
        String year = sdt3.format(date);

        return dayCount(day,month,year).toString();

    }

    private Long dayCount(String day, String month, String year) {
        Long years = Long.parseLong(year);
        Long days = Long.parseLong(day);
        Long months = Long.parseLong(month);
        years-=2022;
        Long dayCount=years*365+months*30+days;
        return dayCount;
    }

    @Override
    public movie addMovie(movie movie) {

        movie.setAddDate(dateInCount());
        return movieRepository.save(movie);
    }

    @Override
    public movie[] getAllMovies() {
        movie[] movies = new movie[movieRepository.findAll().size()];
        movieRepository.findAll().toArray(movies);
        return movies;
    }

    @Override
    public List<movie> getByCategory(String category) {
        return movieRepository.findByCategory(category);
    }

    @Override
    public List<movie> getByName(String name) {
        List<movie> movies = movieRepository.findAll();
        List<movie> movies1 = new ArrayList<>();

        for (movie movie : movies){
            boolean logic = movie.getName().contains(name);
            if (logic){
                movies1.add(movie);
            }
        }
        return movies1;
    }

    @Override
    public String updateMovie(movieUpdateData movieUpdateData) {
        Optional<movie> movie1 = movieRepository.findById(movieUpdateData.getMovieId());
        if (movie1.isPresent()){
            movie movie2 = movie1.get();
            movie2.setAddDate(dateInCount());
            movie2.setCategory(movieUpdateData.getCategory());
            movie2.setImageUrl(movieUpdateData.getImageUrl());
            movie2.setLaunchingImageUrl(movieUpdateData.getLaunchingImageUrl());
            movie2.setName(movieUpdateData.getName());
            movie2.setStory(movieUpdateData.getStory());
            movie2.setTrailerLink(movieUpdateData.getTrailerLink());
            movie2.setPrice(movieUpdateData.getPrice());
            movieRepository.save(movie2);
            List<roles> characters = movieUpdateData.getRoles();
            List<Long> removableCharacters = movieUpdateData.getRemovableCharacters();
            for(roles character: characters){
                character.setMovie(movie1.get());
                rolesRepository.save(character);
            }
            for(Long id : removableCharacters){
                Optional<roles> character = rolesRepository.findById(id);
                if (character.isPresent()){
                    rolesRepository.deleteByRoleId(id);
                }
            }




            return "successfully updated";
        }
        return "error Id";
    }

    @Override
    public List<movie> getTopRated() {
        List<movie> movies = movieRepository.findAll();
        List<movie> moviesFiltered = new ArrayList<>();
        //List<Integer> requestAmounts = new ArrayList<>();

        int i=0;
        for (movie movie : movies){
            if (i<12){
                moviesFiltered.add(movie);
            }
            else {
                int j=0;
                for (com.movieMania.backend.Entity.movie movie1 : moviesFiltered){
                    if (movie1.getRate()<movie.getRate()){
                        moviesFiltered.get(j).equals(movie);
                        break;
                    }
                    j++;
                }
            }
            i++;
        }

        for (int j=0; j<moviesFiltered.size()-1; j++){
            if (moviesFiltered.get(j).getRate()>moviesFiltered.get(j+1).getRate()){
                movie newMovie = moviesFiltered.get(j+1);
                moviesFiltered.get(j+1).equals(moviesFiltered.get(j));
                moviesFiltered.get(j).equals(newMovie);

                for(int k=j; k>0; k--){
                    if (moviesFiltered.get(k).getRate()<moviesFiltered.get(k+1).getRate()){
                        movie newMovie2 = moviesFiltered.get(k);
                        moviesFiltered.get(k).equals(moviesFiltered.get(k+1));
                        moviesFiltered.get(k+1).equals(newMovie2);
                    }
                }
            }
        }

        return moviesFiltered;
    }

    @Override
    public List<String> getCategories() {

        List<String> categories = new ArrayList<>();
        List<movie> movies = movieRepository.findAll();
        categories.add(movies.get(0).getCategory());
        for (movie movie : movies){
            String category = movie.getCategory();
            boolean sameCat = false;
            for (String string : categories){
                if (string.equalsIgnoreCase(category)) {
                    sameCat = true;
                    break;
                }
            }
            if (!sameCat){
                categories.add(category);
            }
        }

        return categories;
    }

    @Override
    public String deleteMovie(Long id) {
        Optional<movie> movie = movieRepository.findById(id);
        if (movie.isPresent()){
            movieRepository.deleteById(id);
            return "successfully deleted";
        }
        return "error Id";
    }

    @Override
    public movie getById(Long id) {
        Optional<movie> movie =movieRepository.findById(id);
        return movie.orElse(null);
    }

    @Override
    public List<movie> getMoviesByIds(List<Long> ids) {
        List<movie> movies = new ArrayList<>();
        for (Long id : ids){
            Optional<movie> movie =movieRepository.findById(id);
            if(movie.isPresent()){
             boolean logic = false;
                for(com.movieMania.backend.Entity.movie movie1 : movies){
                    if(movie1.getMovieId().equals(movie.get().getMovieId())){
                        logic=true;
                        break;
                    }
                }
                if (!logic){
                    movies.add(movie.get());
                }
            }
           
        }
        return movies;
    }

    @Override
    public List<movie> getMoviesWithoutIds(List<Long> ids) {
        List<movie> movies = movieRepository.findAll();
        List<movie> movies1 = new ArrayList<>();

        for (movie movie : movies){
            boolean logicSelect = false;
            for (Long id : ids){
               if (movie.getMovieId().equals(id)){
                   logicSelect=true;
                   break;
               }
            }
            if (!logicSelect){
                movies1.add(movie);
            }
        }

        return movies1;
    }

    @Override
    public Long getLatestMovieId() {
        List<movie> movies = movieRepository.findAll();
        Long latest = 0L;
        for(movie movie : movies){
            if (movie.getMovieId()>latest){
                latest=movie.getMovieId();
            }
        }
        return latest+1;
    }

    @Override
    public boolean checkMovieAvailability(Long id) {
        Optional<movie> movie = movieRepository.findById(id);
        return movie.isPresent();
    }
}
