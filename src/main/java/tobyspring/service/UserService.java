package tobyspring.service;

import tobyspring.dao.UserDao;
import tobyspring.domain.Level;
import tobyspring.domain.User;
import tobyspring.service.policy.UserLevelUpgradePolicy;

import java.util.List;

public class UserService {

    UserDao userDao;
    UserLevelUpgradePolicy userLevelUpgradePolicy;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void upgradeLevels(){
        List<User> users = userDao.getAll();
        users.stream().forEach(user -> {
            if(canUpgradeLevel(user)){
                upgradeLevel(user);
            }
        });
    }

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private boolean canUpgradeLevel(User user){
        return userLevelUpgradePolicy.canUpgradeLevel(user);
    }

    private void upgradeLevel(User user){
        userLevelUpgradePolicy.upgradeLevel(user);
        userDao.update(user);
    }


    public void add(User user) {
        if(user.getLevel() == null){
            user.setLevel(Level.BASIC);
        }

        userDao.add(user);
    }
}
