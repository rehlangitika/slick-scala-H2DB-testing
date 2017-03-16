CREATE TABLE experienced_employee(id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                                  name VARCHAR(50) NOT NULL,
                                  experience DOUBLE NOT NULL);

CREATE TABLE project(projid INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                     name VARCHAR(50) NOT NULL,
                     empId INT NOT NULL REFERENCES experienced_employee(id),
                     teamsize INT NOT NULL,
                     teamlead VARCHAR(20) NOT NULL);

CREATE TABLE dependent(dependentid INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
                       empid INT NOT NULL REFERENCES experienced_employee(id),
                       name VARCHAR(50) NOT NULL,
                       relation VARCHAR(40) NOT NULL,
                       age INT);