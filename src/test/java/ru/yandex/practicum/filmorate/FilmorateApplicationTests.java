package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.*;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

	private final UserDao userDao;
	private final FilmDao filmDao;
	private final FSStatusDao fsStatusDao;
	private final GenreDao genreDao;
	private final RatingDao ratingDao;

	private Film spiderMan;
	private Film transformers;
	private Film newSpiderMan;

	private User kotBoris;
	private User dragonFly;
	private User newBoris;

	@BeforeEach
	void createFilms() {
		spiderMan = new Film("Человек-паук", "Описание Человека-паука",
				LocalDate.of(2023, 5, 31), 140, new Rating(1, "G"));
		transformers = new Film("Трансформеры", "Описание Трансформеров",
				LocalDate.of(2023, 5, 27), 127,new Rating(3, "PG-13"));
		newSpiderMan = new Film(1, "Новый Человек-паук", "Описание Нового Человека-паука",
				LocalDate.of(2023, 5, 31), 140, new Rating(2, "PG"));
	}

	@BeforeEach
	void createUsers() {
		kotBoris = new User("Борис", "boris@email.com", "kotBoris",
				LocalDate.of(2000, 1, 1));
		dragonFly = new User("Андрей", "reddragon@yandex.ru", "dragonFly",
				LocalDate.of(1985, 5, 28));
		newBoris = new User(1,"Борис", "boris@email.com", "newBoris",
				LocalDate.of(2000, 1, 1));
	}

	@Test
	void testDB() {
		//testCreate
		Film film = filmDao.create(spiderMan);
		spiderMan.setId(1);
		assertEquals(spiderMan, film);

		Film film2 = filmDao.create(transformers);
		transformers.setId(2);
		assertEquals(transformers, film2);

		//testGetAllFilms
		List<Film> films = filmDao.getAll();
		assertEquals(List.of(spiderMan,transformers), films);

		//testGetById
		Film film3 = filmDao.getFilmById(2);
		assertEquals(transformers, film3);

		//testUpdateFilm
		Film film4 = filmDao.put(newSpiderMan);
		assertEquals(newSpiderMan, film4);

		//testCreateUser
		User user = userDao.create(kotBoris);
		kotBoris.setId(1);
		assertEquals(kotBoris, user);
		User user1 = userDao.create(dragonFly);
		dragonFly.setId(2);
		assertEquals(dragonFly, user1);

		//testGetAllUsers
		List<User> users = userDao.getAll();
		assertEquals(List.of(kotBoris, dragonFly), users);

		//testGetUserById
		User user2 = userDao.getUserById(2);
		assertEquals(dragonFly, user2);

		//testUpdateUser
		User user3 = userDao.put(newBoris);
		assertEquals(newBoris, user3);

		//testAddFriend
		userDao.addFriend(kotBoris.getId(),dragonFly.getId());
		List<Integer> friendsIds = userDao.getUserFriendsIds(kotBoris.getId());
		assertEquals(List.of(2), friendsIds);

		//testDeleteFriend
		userDao.removeFriend(kotBoris.getId(),dragonFly.getId());
		friendsIds.clear();
		friendsIds = userDao.getUserFriendsIds(kotBoris.getId());
		assertEquals(List.of(), friendsIds);


		//testAddLike
		filmDao.addLike(newSpiderMan.getId(),newBoris.getId());
		assertEquals(List.of(1), filmDao.getFilmLikesIds(1));

		//testDeleteLike
		filmDao.deleteLike(newSpiderMan.getId(),newBoris.getId());
		assertEquals(List.of(), filmDao.getFilmLikesIds(1));

		//testGetPopularFilms
		filmDao.addLike(newSpiderMan.getId(),newBoris.getId());
		filmDao.addLike(transformers.getId(),newBoris.getId());
		filmDao.addLike(transformers.getId(),dragonFly.getId());
		List<Film> popFilms = filmDao.getPopularFilms(1);
		assertEquals(List.of(filmDao.getFilmById(2)), popFilms);

		//testDeleteFilm
		filmDao.deleteFilmById(2);
		assertEquals(List.of(filmDao.getFilmById(1)), filmDao.getAll());

		//testDeleteUser
		userDao.deleteUserById(2);
		assertEquals(List.of(newBoris), userDao.getAll());

		//testAddGenre
		//GENRE: 1 -'Комедия', 2 - 'Драма', 3 - 'Мультфильм', 4 - 'Триллер', 5 - 'Документальный', 6 - 'Боевик';
		filmDao.addGenre(newSpiderMan.getId(),1);
		filmDao.addGenre(newSpiderMan.getId(),2);
		assertEquals(List.of(1,2), filmDao.getFilmGenreIds(newSpiderMan.getId()));

		//testDeleteGenre
		filmDao.removeGenre(newSpiderMan.getId(),1);
		assertEquals(List.of(2), filmDao.getFilmGenreIds(newSpiderMan.getId()));
	}

	@Test
	void testGenre() {
		//(1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');
		genreDao.deleteById(1);
		genreDao.deleteById(2);
		genreDao.deleteById(3);
		genreDao.deleteById(4);

		assertEquals(List.of(new Genre(5, "Документальный"), new Genre(6, "Боевик")),
				genreDao.getAll());
		assertEquals(new Genre(5, "Документальный"), genreDao.getById(5));
		assertEquals(new Genre(1, "Мультфильм"), genreDao.create(new Genre("Мультфильм")));
		assertEquals(new Genre(1, "Multik"), genreDao.put(new Genre(1, "Multik")));
	}

	@Test
	void testRating() {
		//(1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');
		ratingDao.deleteById(1);
		ratingDao.deleteById(2);
		ratingDao.deleteById(3);

		assertEquals(List.of(new Rating(4, "R"), new Rating(5, "NC-17")),
				ratingDao.getAll());
		assertEquals(new Rating(5, "NC-17"), ratingDao.getById(5));
		assertEquals(new Rating(1, "G"), ratingDao.create(new Rating("G")));
		assertEquals(new Rating(1, "PG"), ratingDao.put(new Rating(1, "PG")));
	}

	@Test
	void testFSStatus() {
		//(1, 'подтвержденный'), (2, 'неподтвержденный');
		fsStatusDao.deleteById(1);

		assertEquals(List.of(new FsStatus(2, "неподтвержденный")),
				fsStatusDao.getAll());
		assertEquals(new FsStatus(2, "неподтвержденный"), fsStatusDao.getById(2));
		assertEquals(new FsStatus(1, "подтвержденный"), fsStatusDao.create(new FsStatus("подтвержденный")));
		assertEquals(new FsStatus(1, "confirmed"), fsStatusDao.put(new FsStatus(1, "confirmed")));
	}
}
