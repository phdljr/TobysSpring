package tobyspring.service.policy;

import tobyspring.dao.UserDao;
import tobyspring.domain.Level;
import tobyspring.domain.User;

import static tobyspring.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static tobyspring.service.UserService.MIN_RECCOMEND_FOR_GOLD;

public class UserLevelUpgradeNormalPolicy implements UserLevelUpgradePolicy{

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean canUpgradeLevel(User user){
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    public void upgradeLevel(User user){
        user.upgradeLevel();
        userDao.update(user);
    }
}
