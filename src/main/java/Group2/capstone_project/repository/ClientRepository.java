package Group2.capstone_project.repository;

import Group2.capstone_project.domain.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    void save(Client client);
    Optional<Client> findId(String name, String studentNumber, String age);
    Optional<Client> findPwd(String name, String id, String studentNumber);
    List<Client> findAll();
    Optional<Client> findById(String id);
    Optional<Client> login(Client client);

    void updateInfo(Client client);


}
