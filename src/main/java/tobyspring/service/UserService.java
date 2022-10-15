package tobyspring.service;

import tobyspring.dao.UserDao;
import tobyspring.domain.Level;
import tobyspring.domain.User;

import java.util.List;

public class UserService {
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels(){
        List<User> users = userDao.getAll();
        users.stream().forEach(user -> {
            if(canUpgradeLevel(user)){
                upgradeLevel(user);
            }
        });
    }

    private boolean canUpgradeLevel(User user){
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC: return (user.getLogin() >= 50);
            case SILVER: return (user.getRecommend() >= 30);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    private void upgradeLevel(User user){
        user.upgradeLevel();
        userDao.update(user);
    }


    public void add(User user) {
        if(user.getLevel() == null){
            user.setLevel(Level.BASIC);
        }

        userDao.add(user);
    }
}
