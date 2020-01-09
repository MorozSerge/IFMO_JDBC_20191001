package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DaoFactory {
    private Statement statement() throws SQLException {
        return ConnectionSource.instance().createConnection().createStatement();
    }

    private Employee employeeRowMapper(ResultSet resultSet) {
        try {
            BigInteger managerId = resultSet.getString("MANAGER") == null ? BigInteger.ZERO : BigInteger.valueOf(Long.parseLong(resultSet.getString("MANAGER")));
            BigInteger departmentId = resultSet.getString("DEPARTMENT") == null ? BigInteger.ZERO : BigInteger.valueOf(Long.parseLong(resultSet.getString("DEPARTMENT")));
            return new Employee(
                    BigInteger.valueOf(resultSet.getInt("ID")),
                    new FullName(
                            resultSet.getString("FIRSTNAME"),
                            resultSet.getString("LASTNAME"),
                            resultSet.getString("MIDDLENAME")
                    ),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    BigDecimal.valueOf(resultSet.getInt("SALARY")),
                    managerId,
                    departmentId);
        } catch (SQLException e) {
            System.out.println("employeeRowMapper error");
            return null;
        }
    }

    private Department departmentRowMapper(ResultSet resultSet) {
        try {
            return new Department(
                    BigInteger.valueOf(resultSet.getInt("ID")),
                    resultSet.getString("NAME"),
                    resultSet.getString("LOCATION"));
        } catch(SQLException e) {
            System.out.println("departmentRowMapper error");
            return null;
        }
    }

    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                List<Employee> result = new ArrayList<>();
                try {
                    ResultSet resultSet = statement().executeQuery(
                            "SELECT * FROM employee WHERE department = " + department.getId()
                    );

                    while (resultSet.next() != false) {
                        result.add(employeeRowMapper(resultSet));
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            public List<Employee> getByManager(Employee manager) {
                List<Employee> result = new ArrayList<>();
                try{
                    ResultSet resultSet = statement().executeQuery(
                            "SELECT * FROM employee WHERE manager = " + manager.getId()
                    );
                    while (resultSet.next() != false) {
                        result.add(employeeRowMapper(resultSet));
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            public Optional<Employee> getById(BigInteger employeeId) {
                try {
                    ResultSet resultSet = statement().executeQuery(
                            "SELECT * FROM employee WHERE id = " + employeeId
                    );
                    if (resultSet.next() != false) {
                        return Optional.of(Objects.requireNonNull(employeeRowMapper(resultSet)));
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
                return Optional.empty();
            }

            @Override
            public List<Employee> getAll() {
                List<Employee> result = new ArrayList<>();
                try {
                    ResultSet resultSet = statement().executeQuery(
                            "SELECT * FROM employee"
                    );
                    while (resultSet.next() != false) {
                        result.add(employeeRowMapper(resultSet));
                    }
                    return result;
                } catch (SQLException e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public Employee save(Employee employee) {
                try {
                    statement().execute(
                            "INSERT INTO employee VALUES ('" +
                                    employee.getId()                       + "', '" +
                                    employee.getFullName().getFirstName()  + "', '" +
                                    employee.getFullName().getLastName()   + "', '" +
                                    employee.getFullName().getMiddleName() + "', '" +
                                    employee.getPosition()                 + "', '" +
                                    employee.getManagerId()                + "', '" +
                                    employee.getHired()                    + "', '" +
                                    employee.getSalary()                   + "', '" +
                                    employee.getDepartmentId()             + "')"
                    );
                } catch (SQLException e){
                    e.printStackTrace();
                }
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                try {
                    statement().execute(
                            "DELETE FROM employee WHERE ID = " + employee.getId()
                    );
                } catch (SQLException ignored){}
            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger departmentId) {
                try {
                    ResultSet resultSet = statement().executeQuery(
                            "SELECT * FROM department WHERE id = " + departmentId
                    );
                    if (resultSet.next() != false) {
                        Department department = departmentRowMapper(resultSet);
                        return Optional.of(Objects.requireNonNull(department));
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
                return Optional.empty();
            }

            @Override
            public List<Department> getAll() {
                List<Department> departments = new ArrayList<>();
                try {
                    ResultSet resultSet = statement().executeQuery(
                            "SELECT * FROM department"
                    );
                    while (resultSet.next() != false) {
                        Department department = departmentRowMapper(resultSet);
                        departments.add(department);
                    }
                    return departments;
                } catch (SQLException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Department save(Department department) {
                try {
                    if (getById(department.getId()).equals(Optional.empty())) {
                        statement().execute(
                                "INSERT INTO department VALUES ('" +
                                        department.getId()       + "', '" +
                                        department.getName()     + "', '" +
                                        department.getLocation() + "')"
                        );
                    } else {
                        statement().execute(
                                "UPDATE department SET " +
                                        "NAME = '"     + department.getName()     + "', " +
                                        "LOCATION = '" + department.getLocation() + "' " +
                                        "WHERE ID = '" + department.getId()       + "'"
                        );
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
                return department;
            }

            @Override
            public void delete(Department department) {
                try{
                    statement().execute(
                            "DELETE FROM department WHERE ID = " + department.getId()
                    );
                } catch (SQLException ignored){}
            }
        };
    }
}
