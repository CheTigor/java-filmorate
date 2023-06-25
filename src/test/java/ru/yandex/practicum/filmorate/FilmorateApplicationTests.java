package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

	private final UserDao userDao;
	private final FSStatusDao fsStatusDao;
	private final GenreDao genreDao;
	private final MpaDao mpaDao;
	private final FilmDao filmDao;

	private final FilmService filmService;
	private final UserService userService;

	private Film spiderMan;
	private Film transformers;
	private Film newSpiderMan;

	private User kotBoris;
	private User dragonFly;
	private User newBoris;

	@BeforeEach
	void createFilms() {
		spiderMan = new Film("Человек-паук", "Описание Человека-паука",
				LocalDate.of(2023, 5, 31), 140, new Mpa(1, "G"));
		transformers = new Film("Трансформеры", "Описание Трансформеров",
				LocalDate.of(2023, 5, 27), 127,new Mpa(3, "PG-13"));
		newSpiderMan = new Film(1, "Новый Человек-паук", "Описание Нового Человека-паука",
				LocalDate.of(2023, 5, 31), 140, new Mpa(2, "PG"));
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
	@Sql(scripts = {"/schema.sql", "/data.sql"})
	void testDB() {
		//testCreate
		Film film = filmService.create(spiderMan);
		spiderMan.setId(1);
		assertEquals(spiderMan, film);

		Film film2 = filmService.create(transformers);
		transformers.setId(2);
		assertEquals(transformers, film2);

		//testGetAllFilms
		List<Film> films = filmService.getAll();
		assertEquals(List.of(spiderMan,transformers), films);

		//testGetById
		Film film3 = filmService.getFilmById(2);
		assertEquals(transformers, film3);

		//testUpdateFilm
		Film film4 = filmService.put(newSpiderMan);
		assertEquals(newSpiderMan, film4);

		//testCreateUser
		User user = userService.create(kotBoris);
		kotBoris.setId(1);
		assertEquals(kotBoris, user);
		User user1 = userService.create(dragonFly);
		dragonFly.setId(2);
		assertEquals(dragonFly, user1);

		//testGetAllUsers
		List<User> users = userService.getAll();
		assertEquals(List.of(kotBoris, dragonFly), users);

		//testGetUserById
		User user2 = userService.getUserById(2);
		assertEquals(dragonFly, user2);

		//testUpdateUser
		User user3 = userService.put(newBoris);
		assertEquals(newBoris, user3);

		//testAddFriend
		userService.addFriend(kotBoris.getId(),dragonFly.getId());
		List<Integer> friendsIds = userDao.getUserFriendsIds(kotBoris.getId());
		assertEquals(List.of(2), friendsIds);

		//testDeleteFriend
		userService.removeFriend(kotBoris.getId(),dragonFly.getId());
		friendsIds.clear();
		friendsIds = userDao.getUserFriendsIds(kotBoris.getId());
		assertEquals(List.of(), friendsIds);


		//testAddLike
		filmService.addLike(newSpiderMan.getId(),newBoris.getId());
		assertEquals(List.of(1), filmDao.getFilmLikesIds(1));

		//testDeleteLike
		filmService.deleteLike(newSpiderMan.getId(),newBoris.getId());
		assertEquals(List.of(), filmDao.getFilmLikesIds(1));

		//testGetPopularFilms
		filmService.addLike(newSpiderMan.getId(),newBoris.getId());
		filmService.addLike(transformers.getId(),newBoris.getId());
		filmService.addLike(transformers.getId(),dragonFly.getId());
		List<Film> popFilms = filmService.getPopularFilms(1);
		assertEquals(List.of(filmService.getFilmById(2)), popFilms);

		//testDeleteFilm
		filmService.deleteFilmById(2);
		assertEquals(List.of(filmService.getFilmById(1)), filmService.getAll());

		//testDeleteUser
		userService.deleteUserById(2);
		assertEquals(List.of(newBoris), userService.getAll());

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
	@Sql(scripts = {"/schema.sql", "/data.sql"})
	void testGenre() {
		//(1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');
		assertEquals(List.of(new Genre(1, "Комедия"), new Genre(2, "Драма"),
				new Genre(3, "Мультфильм"), new Genre(4, "Триллер"),
				new Genre(5, "Документальный"), new Genre(6, "Боевик")),
				genreDao.getAll());
		assertEquals(new Genre(5, "Документальный"), genreDao.getById(5));
	}

	@Test
	@Sql(scripts = {"/schema.sql", "/data.sql"})
	void testMpa() {
		//(1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');
		assertEquals(List.of(new Mpa(1, "G"), new Mpa(2, "PG"),
				new Mpa(3, "PG-13"), new Mpa(4, "R"), new Mpa(5, "NC-17")),
				mpaDao.getAll());
		assertEquals(new Mpa(5, "NC-17"), mpaDao.getById(5));
	}

	@Test
	@Sql(scripts = {"/schema.sql", "/data.sql"})
	void testFSStatus() {
		//(1, 'подтвержденный'), (2, 'неподтвержденный');
		assertEquals(List.of(new FsStatus(1, "подтвержденный"), new FsStatus(2, "неподтвержденный")),
				fsStatusDao.getAll());
		assertEquals(new FsStatus(2, "неподтвержденный"), fsStatusDao.getById(2));
	}
}
