package Group2.capstone_project.repository;

import Group2.capstone_project.domain.Client;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class MysqlClientRepository implements ClientRepository{

    private final JdbcTemplate jdbcTemplate;

    public MysqlClientRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    @Override
    public void save(Client client) {
        String sql = "INSERT INTO client(id,name,age,studentNumber,email,school, pwd) values(?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,client.getId(),client.getName(),client.getAge(),client.getStudentNumber(),
                client.getEmail(),client.getSchool(),client.getPwd());
    }

    @Override
    public Optional<Client> findId(String name, String studentNumber, String age) {
        String[] object = {name,studentNumber,age};
        String sql = "SELECT * FROM client where name = ? and studentNumber = ? and age = ?";
        List<Client> result = jdbcTemplate.query(sql,clientRowMapper(),object);
        return result.stream().findAny();

    }

    @Override
    public Optional<Client> findPwd(String name, String id, String studentNumber) {
        String[] object = {name,id,studentNumber};
        String sql = "SELECT * FROM client where name = ? and id = ? and studentNumber = ?";
        List<Client> result = jdbcTemplate.query(sql,clientRowMapper(),object);
        return result.stream().findAny();
    }
    @Override
    public List<Client> findAll() {
        String sql = "SELECT * FROM client";
        return jdbcTemplate.query(sql,clientRowMapper());
    }


    @Override
    public Optional<Client> findById(String id) {
        String sql = "SELECT *FROM client WHERE id =?";
        List<Client> result = jdbcTemplate.query(sql, clientRowMapper(), id);
        return result.stream().findAny();
    }

    private RowMapper<Client> clientRowMapper(){
        return (rs, rowNum) -> {

            Client client = new Client();
            client.setId(rs.getString("id"));
            client.setName(rs.getString("name"));
            client.setStudentNumber(rs.getString("studentNumber"));
            client.setAge(rs.getString("age"));
            client.setPwd(rs.getString("pwd"));



            return client;
        };
    }
    @Override
    public Optional<Client> login (Client client){
        String sql = "SELECT * FROM client where id = ?";
        List<Client> result = jdbcTemplate.query(sql,clientRowMapper(),client.getId());
        return result.stream().findAny();
    }

    @Override
    public void updateInfo(Client client) {
        String sql = "UPDATE client SET name= ? , studentNumber =? , age=? WHERE id =?  ";
        String[] object = {client.getName(),client.getStudentNumber(),client.getAge(),client.getId()};
        jdbcTemplate.update(sql,client.getName(),client.getStudentNumber(),client.getAge(),client.getId());
    }
}
