public interface UserRepository<T>{
    User findByEmailAndPassword(String login, String password);
    User findById(long userId);
}
