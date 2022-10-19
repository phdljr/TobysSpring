package tobyspring.service;

import tobyspring.domain.User;

public interface UserService {
    void add(User user);
    void upgradeLevels();
}
