package game;

import app.App;
import app.dao.UserDao;
import db.DBConnection;
import game.*;
import server.Server;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.out.println("HEY");
        MonsterCard goblin = new MonsterCard("Goblin", 4, Element.NORMAL);
        MonsterCard dragon = new MonsterCard("Dragon", 10, Element.FIRE);
        MonsterCard wizard = new MonsterCard("Wizard", 7, Element.FIRE);
        MonsterCard ork = new MonsterCard("Ork", 6, Element.NORMAL);
        MonsterCard knight = new MonsterCard("Knight", 5, Element.NORMAL);
        MonsterCard kraken = new MonsterCard("Kraken", 9, Element.WATER);
        SpellCard waterSpell = new SpellCard("WATER TORNADO", 8, Element.WATER);
        SpellCard fireSpell = new SpellCard("FIRE DANCE", 6, Element.FIRE);

        DBConnection db = DBConnection.getInstance();
        db.getConnection();

        App app = new App();
        Thread service = new Thread(new Server(app, 7777));
        service.start();

        User user = new User("REAL NAME", "username", "secret", 90, new Stats(), new Profile("my bio", "link.to.image"));

        UserDao userDao = new UserDao();
//        userDao.save(user);
//        userDao.save(new User("sxript", "Pa55w0rd"));
        Optional<User> optionalUser = userDao.get("username");
        if (optionalUser.isPresent()) {
            User getUser = optionalUser.get();
            System.out.println(getUser);
        }
        System.out.println("\n GET ALL USERS");
        Collection<User> users = userDao.getAll();
        users.forEach(System.out::println);
    }
}