package tobyspring.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import tobyspring.dao.UserDao;
import tobyspring.domain.Level;
import tobyspring.domain.User;
import tobyspring.service.policy.UserLevelUpgradePolicy;
import tobyspring.service.policy.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserLevelUpgradePolicy userLevelUpgradePolicy;
    private MailSender mailSender;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void upgradeLevels(){
        List<User> users = userDao.getAll();
        users.forEach(user -> {
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

    protected void upgradeLevel(User user){
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEMail(user);
    }

    private void sendUpgradeEMail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이 " + user.getLevel().name()+"으로 업그레이드됐습니다.");

        mailSender.send(mailMessage);
    }

    public void add(User user) {
        if(user.getLevel() == null){
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
