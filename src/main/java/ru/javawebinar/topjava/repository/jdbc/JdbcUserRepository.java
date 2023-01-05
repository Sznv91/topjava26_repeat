package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;
    private final SimpleJdbcInsert insertRole;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.insertRole = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_roles");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());

            List<Role> roleList = new ArrayList<>(user.getRoles());
            for (Role role : roleList) {
                Map<String, Object> param = new HashMap<>();
                param.put("user_id", user.getId());
                param.put("role", role);
                insertRole.execute(param);
            }
        } else {
            int result = 0;
            int[] userUpdateBatchResult = jdbcTemplate.batchUpdate(
                    "UPDATE users SET name=?, email=?, password=?, registered=?, enabled=?, calories_per_day=? WHERE id=?",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setString(1, user.getName());
                            ps.setString(2, user.getEmail());
                            ps.setString(3, user.getPassword());
                            ps.setTimestamp(4, new Timestamp(user.getRegistered().getTime()));
                            ps.setBoolean(5, user.isEnabled());
                            ps.setInt(6, user.getCaloriesPerDay());
                            ps.setInt(7, user.getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return 1;
                        }
                    });
            int[] roleUpdateBatchResult = jdbcTemplate.batchUpdate("UPDATE user_roles SET role=? WHERE user_id=?",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            List<Role> roleList = new ArrayList<>(user.getRoles());
                            ps.setString(1, roleList.get(i).toString());
                            ps.setInt(2, user.getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return user.getRoles().size();
                        }
                    });

            for (int i : userUpdateBatchResult) {
                result += i;
            }
            for (int i : roleUpdateBatchResult) {
                result += i;
            }
            if (result == 0) {
                return null;
            } else return user;
        }
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        List<RoleToRowMapper.RoleTo> roleTos = jdbcTemplate.query("SELECT * FROM user_roles WHERE user_id=?", new RoleToRowMapper(), id);
        return DataAccessUtils.singleResult(mergeRole(users, roleTos));
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        if (!users.isEmpty()) {
            List<RoleToRowMapper.RoleTo> roleTos = jdbcTemplate.query("SELECT * FROM user_roles WHERE user_id=?", new RoleToRowMapper(), users.get(0).getId());
            return DataAccessUtils.singleResult(mergeRole(users, roleTos));
        }
        return null;
    }

    @Override
    public List<User> getAll() {

        List<User> userList = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        List<RoleToRowMapper.RoleTo> roleToList = jdbcTemplate.query("SELECT * FROM user_roles", new RoleToRowMapper());
        return mergeRole(userList, roleToList);
    }

    private List<User> mergeRole(List<User> userList, List<RoleToRowMapper.RoleTo> roleToList) {
        Map<Integer, List<Role>> roleMap = new HashMap<>();
        for (RoleToRowMapper.RoleTo roleTo : roleToList) {
            if (roleTo.role != null) {
                roleMap.merge(roleTo.getUserId(), Collections.singletonList(roleTo.getRole()), (oldVal, newVal) -> {
                    List<Role> role = new ArrayList<>();
                    role.addAll(oldVal);
                    role.addAll(newVal);
                    return role;
                });
            }
        }
        userList.forEach(user -> user.setRoles(roleMap.getOrDefault(user.getId(), new ArrayList<>())));
        return new ArrayList<>(userList);
    }

    private class RoleToRowMapper implements RowMapper<RoleToRowMapper.RoleTo> {
        class RoleTo {
            int userId;
            Role role;

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public Role getRole() {
                return role;
            }

            public void setRole(Role role) {
                this.role = role;
            }
        }

        @Override
        public RoleTo mapRow(ResultSet rs, int rowNum) throws SQLException {
            RoleTo roleTo = new RoleTo();
            int userId = rs.getInt("user_id");
            roleTo.setUserId(userId);
            String role = rs.getString("role");
            if (role != null) {
                roleTo.setRole(Role.valueOf(role));
            }
            return roleTo;
        }
    }

}
