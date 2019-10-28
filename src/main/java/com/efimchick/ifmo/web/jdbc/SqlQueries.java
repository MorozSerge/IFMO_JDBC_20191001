package com.efimchick.ifmo.web.jdbc;

/**
 * Implement sql queries like described
 */
public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    String select01 = "SELECT * FROM employee order by LASTNAME";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    String select02 = "SELECT * FROM employee WHERE length(LASTNAME)<6 order by LASTNAME";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    String select03 = "SELECT * FROM employee WHERE salary >= 2000 AND salary <= 3000 ";

    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    String select04 = "SELECT * FROM employee WHERE salary <=2000 OR salary >= 3000";

    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    String select05 = "SELECT EMPLOYEE.*, DEPARTMENT.name FROM EMPLOYEE, DEPARTMENT WHERE EMPLOYEE.department = DEPARTMENT.id";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select06 = "SELECT EMPLOYEE.*, DEPARTMENT.name AS \"depname\" FROM EMPLOYEE LEFT JOIN DEPARTMENT on EMPLOYEE.department = DEPARTMENT.id";

    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB
    String select07 = "SELECT sum(EMPLOYEE.salary) AS \"total\" FROM EMPLOYEE ";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB
    String select08 = "SELECT DEPARTMENT.name AS \"depname\", COUNT(EMPLOYEE.department) AS \"staff_size\" FROM EMPLOYEE, DEPARTMENT WHERE EMPLOYEE.department = DEPARTMENT.id GROUP by DEPARTMENT.name";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB
    String select09 = "SELECT DEPARTMENT.name AS \"depname\", SUM(EMPLOYEE.salary) AS \"total\", AVG(EMPLOYEE.salary) AS \"average\" FROM EMPLOYEE, DEPARTMENT WHERE EMPLOYEE.department = DEPARTMENT.id GROUP by DEPARTMENT.name";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB
    String select10 = "SELECT e.lastname AS \"employee\", m.lastname AS \"manager\" FROM EMPLOYEE e LEFT JOIN EMPLOYEE m on e.manager = m.id";


}
