package tobyspring.service.policy;

import tobyspring.domain.User;

public interface UserService {
    void add(User user);
    void upgradeLevels();
}
