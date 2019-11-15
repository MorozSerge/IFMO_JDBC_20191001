package com.efimchick.ifmo.web.jdbc.service;
import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.ArrayList;

public class ServiceFactory {
    private ResultSet getResultSet(String s) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        return connection.createStatement().executeQuery(s);
    }
    private List<Employee> Employees;
    private List<Employee> EmployeesForChain;
    private void fillManagers1lev() {
        for (int i = 0; i < Employees.size(); ++i) {
            if (Employees.get(i).getManager() != null) {
                for (int j = 0; j < Employees.size(); ++j) {
                    if (Employees.get(j).getId().equals(Employees.get(i).getManager().getId())) {
                        Employee current = Employees.get(i);
                        Employee currentNum2 = Employees.get(j);
                        Employees.set(i, new Employee(
                                current.getId(),
                                current.getFullName(),
                                current.getPosition(),
                                current.getHired(),
                                current.getSalary(),
                                new Employee(
                                        currentNum2.getId(),
                                        currentNum2.getFullName(),
                                        currentNum2.getPosition(),
                                        currentNum2.getHired(),
                                        currentNum2.getSalary(),
                                        null,
                                        currentNum2.getDepartment()
                                ),
                                current.getDepartment()
                        ));
                    }
                }
            }
        }
    }

    private void fillManagersInfLev() {
        for (int k = 0; k < 5; ++k)
            for (int i = 0; i < EmployeesForChain.size(); ++i) {
                if (EmployeesForChain.get(i).getManager() != null) {
                    for (int j = 0; j < EmployeesForChain.size(); ++j) {
                        if (EmployeesForChain.get(j).getId().equals(EmployeesForChain.get(i).getManager().getId())) {
                            Employee current = EmployeesForChain.get(i);
                            Employee currentNum2 = EmployeesForChain.get(j);
                            EmployeesForChain.set(i, new Employee(
                                    current.getId(),
                                    current.getFullName(),
                                    current.getPosition(),
                                    current.getHired(),
                                    current.getSalary(),
                                    currentNum2,
                                    current.getDepartment()
                            ));
                        }
                    }
                }
            }
    }
    {
        try {
            Employees = getEmployeesByMan();
            EmployeesForChain = getEmployeesByMan();
            fillManagers1lev();
            fillManagersInfLev();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Employee employeeRowMapper(ResultSet resultSet) {
        try {
            Department department = null;
            if (resultSet.getString("DEPARTMENT") != null)
                for (int i = 0; i < getDepartments().size(); i++) {
                    if (BigInteger.valueOf(Long.parseLong(resultSet.getString("DEPARTMENT"))).equals(getDepartments().get(i).getId())) {
                        department = getDepartments().get(i);
                    }
                }
            BigInteger manId = null;
            if (resultSet.getString("MANAGER") != null) {
                manId = BigInteger.valueOf(Long.parseLong(resultSet.getString("MANAGER")));
            }
            Employee manager = null;
            if (manId != null) {
                manager = new Employee(
                        manId,
                        null,
                        null,
                        null,
                        BigDecimal.ZERO,
                        null,
                        null
                );
            }
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
                    manager,
                    department
            );
        } catch (Exception e) {
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

    private List<Employee> getEmployeesByMan() throws SQLException {
        ResultSet resultSet = getResultSet("SELECT * FROM EMPLOYEE");
        List<Employee> employees = new LinkedList<>();
        while (resultSet.next()) {
            Employee employee = employeeRowMapper(resultSet);
            employees.add(employee);
        }
        return employees;
    }

    private List<Department> getDepartments() throws SQLException {
        ResultSet resultSet = getResultSet("SELECT * FROM DEPARTMENT");
        List<Department> departments = new LinkedList<>();
        while (resultSet.next()) {
            Department department = departmentRowMapper(resultSet);
            departments.add(department);
        }
        return departments;
    }

    private List<Employee> getEmployeePaging(List<Employee> list, Paging paging) {
        return list.subList(paging.itemPerPage * (paging.page - 1), Math.min(paging.itemPerPage * paging.page, list.size()));
    }

    public EmployeeService employeeService() {
        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                List<Employee> current = Employees;
                current.sort(Comparator.comparing(Employee::getHired));
                return getEmployeePaging(current, paging);
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                List<Employee> current = Employees;
                current.sort(Comparator.comparing(o -> o.getFullName().getLastName()));
                return getEmployeePaging(current, paging);
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                List<Employee> current = Employees;
                current.sort(Comparator.comparing(Employee::getSalary));
                return getEmployeePaging(current, paging);
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                List<Employee> current = Employees;
                current.sort((o1, o2) -> {
                    if (o1.getDepartment() != null && o2.getDepartment() != null) {
                        if (o1.getDepartment().getName().equals(o2.getDepartment().getName())) {
                            return o1.getFullName().getLastName().compareTo(o2.getFullName().getLastName());
                        } else {
                            return o1.getDepartment().getName().compareTo(o2.getDepartment().getName());
                        }
                    } else {
                        if (o1.getDepartment() == null) {
                            return -1;
                        }
                        if (o2.getDepartment() == null) {
                            return 1;
                        }
                        return 0;
                    }
                });
                return getEmployeePaging(current, paging);
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                List<Employee> current = Employees;
                current.sort(Comparator.comparing(Employee::getHired));
                return getEmployeesByDep(department, paging, current);
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                List<Employee> current = Employees;
                current.sort(Comparator.comparing(Employee::getSalary));
                return getEmployeesByDep(department, paging, current);
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                List<Employee> current = Employees;
                current.sort(Comparator.comparing(o -> o.getFullName().getLastName()));
                return getEmployeesByDep(department, paging, current);
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                List<Employee> current = Employees;
                current.sort(Comparator.comparing(o -> o.getFullName().getLastName()));
                return getEmployeesByMan(manager, paging, current);
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                List<Employee> current = Employees;
                current.sort(Comparator.comparing(Employee::getHired));
                return getEmployeesByMan(manager, paging, current);
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                List<Employee> current = Employees;
                current.sort(Comparator.comparing(Employee::getSalary));
                return getEmployeesByMan(manager, paging, current);
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                List<Employee> current = EmployeesForChain;
                for (Employee employ : current){
                    if (employ.getId().equals(employee.getId())){
                        return employ;
                    }
                }
                return null;
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                List<Employee> current = Employees;
                current.sort((o1, o2) -> o2.getSalary().compareTo(o1.getSalary()));
                List<Employee> qwe = new ArrayList<>();
                for (Employee employee : current) {
                    if (employee.getDepartment() != null && employee.getDepartment().getId().equals(department.getId())) {
                        qwe.add(employee);
                    }
                }
                return qwe.get(salaryRank - 1);
            }
        };
    }

    private List<Employee> getEmployeesByDep(Department department, Paging paging, List<Employee> current) {
        List<Employee> qwe = new ArrayList<>();
        for (Employee employee : current) {
            if (employee.getDepartment() != null && employee.getDepartment().getId().equals(department.getId())) {
                qwe.add(employee);
            }
        }
        return getEmployeePaging(qwe, paging);
    }

    private List<Employee> getEmployeesByMan(Employee manager, Paging paging, List<Employee> current) {
        List<Employee> qwe = new ArrayList<>();
        for (Employee employee : current) {
            if (employee.getManager() != null && employee.getManager().getId().equals(manager.getId())) {
                qwe.add(employee);
            }
        }
        return getEmployeePaging(qwe, paging);
    }

}

