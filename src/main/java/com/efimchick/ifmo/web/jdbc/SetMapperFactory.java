package com.efimchick.ifmo.web.jdbc;

import java.util.Set;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.HashSet;
import java.time.LocalDate;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        return resultSet -> {
            Set<Employee> Employees = new HashSet<>();
            try{
                while (resultSet.next()){
                    Employee employee = rowMap(resultSet);
                    Employees.add(employee);
                    while ((employee = employee.getManager()) != null)
                        Employees.add(employee);
                }
            }
            catch (SQLException e) {
                return null;
            }
            return Employees;
        };
    }

    private Employee rowMap(ResultSet resultSet) {
        try {
            Employee manager = null;
            int current = resultSet.getRow();
            if (resultSet.getObject("MANAGER") != null){
                String id = resultSet.getString("MANAGER");
                resultSet.absolute(0);
                while (resultSet.next() && !resultSet.getString("ID").equals(id));
                manager = rowMap(resultSet);
            }
            resultSet.absolute(current);
            return new Employee(new BigInteger(resultSet.getString("ID")),
                    new FullName(resultSet.getString("FIRSTNAME"), resultSet.getString("LASTNAME"), resultSet.getString("MIDDLENAME")),
                    Position.valueOf(resultSet.getString("POSITION")),
                    LocalDate.parse(resultSet.getString("HIREDATE")),
                    new BigDecimal(resultSet.getString("SALARY")), manager);
        } catch (SQLException e) {
            return null;
        }
    }


}
