package game;

import app.App;
import app.dao.UserDao;
import app.models.Profile;
import db.DBConnection;
import server.Server;
import app.models.Stats;
import app.models.User;
import java.util.Collection;
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

//        UserDao userDao = new UserDao();
//        userDao.save(user);
//
//        User user2 = new User("sxript", "Pa55w0rd");
//        userDao.save(user2);
//        Optional<User> optionalUser = userDao.get("username");
//        if (optionalUser.isPresent()) {
//            User getUser = optionalUser.get();
//            System.out.println(getUser);
//        }
//        System.out.println("\n GET ALL USERS");
//        Collection<User> users = userDao.getAll();
//        users.forEach(System.out::println);
//
//        System.out.println("\n DELETE USER");
//        userDao.delete(user);
//
//        System.out.println("\n GET ALL USERS");
//        users = userDao.getAll();
//        users.forEach(System.out::println);
//
//        System.out.println("\n UPDATE USER");
//        User userToUpdate = new User(user2.getName(), user2.getUsername(), user2.getPassword(), user2.getCoins(), user2.getStats(), user2.getProfile());
//        userToUpdate.setName("Mein name");
//        userToUpdate.setCoins(100000);
//        userToUpdate.getStats().setElo(1920);
//        userToUpdate.getProfile().setImage("url://ok");
//        userDao.update(user2, userToUpdate);
//
//        System.out.println("\n\n");
    }
}