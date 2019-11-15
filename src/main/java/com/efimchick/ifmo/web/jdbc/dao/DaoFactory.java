package com.efimchick.ifmo.web.jdbc.dao;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;
import com.efimchick.ifmo.web.jdbc.ConnectionSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class DaoFactory {
    private ResultSet getResultSet(String a) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        return connection.createStatement().executeQuery(a);
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

    private List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new LinkedList<>();
        ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE");
        while (resultSet.next()!= false) {
            Employee employee = employeeRowMapper(resultSet);
            employees.add(employee);
        }
        return employees;
    }

    private List<Employee> Employ;
    {
        try {
            Employ = getAllEmployees();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Department> getAllDepartments() throws SQLException {
        List<Department> departments = new LinkedList<>();
        ResultSet resultSet = getResultSet("SELECT * FROM DEPARTMENT");
        while (resultSet.next() != false) {
            Department department = departmentRowMapper(resultSet);
            departments.add(department);
        }
        return departments;
    }

    private List<Department> Depart;
    {
        try {
            Depart = getAllDepartments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                List<Employee> result = new ArrayList<>();
                try {
                    for (Employee employee : Employ) {
                        if (employee.getDepartmentId() != null && employee.getDepartmentId().equals(department.getId()))
                            result.add(employee);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            public List<Employee> getByManager(Employee manager) {
                List<Employee> result = new ArrayList<>();
                try {
                    for (Employee employee : Employ) {
                        if (employee.getManagerId() != null && employee.getManagerId().equals(manager.getId())) {
                            result.add(employee);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            public Optional<Employee> getById(BigInteger employeeId) {
                try {
                    for (Employee employee : Employ) {
                        if (employee.getId().equals(employeeId)) {
                            return Optional.of(employee);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return Optional.empty();
            }

            @Override
            public List<Employee> getAll() {
                return Employ;
            }


            @Override
            public Employee save(Employee employee) {
                try {
                    Employ.add(employee);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                Employ.remove(employee);
            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger departmentId) {
                try {
                    for (Department department : Depart) {
                        if (department.getId().equals(departmentId)) {
                            return Optional.of(department);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return Optional.empty();
            }

            @Override
            public List<Department> getAll() {
                return Depart;
            }

            @Override
            public Department save(Department department) {
                try {
                    for (int i = 0; i < Depart.size(); i++) {
                        if (Depart.get(i).getId().equals(department.getId())) {
                            Depart.remove(Depart.get(i));
                        }
                    }
                    Depart.add(department);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return department;
            }

            @Override
            public void delete(Department department) {
                Depart.remove(department);
            }
        };
    }

}
