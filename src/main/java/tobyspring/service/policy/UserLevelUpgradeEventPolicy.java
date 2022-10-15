package tobyspring.service.policy;

import tobyspring.domain.User;

public class UserLevelUpgradeEventPolicy implements UserLevelUpgradePolicy{
    @Override
    public boolean canUpgradeLevel(User user) {
        return false;
    }

    @Override
    public void upgradeLevel(User user) {

    }
}
