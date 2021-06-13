Example of SQL StoredProcedure: 

```
[chandrashekhar@localhost Development]$ podman run -p 3306:3306 -d --name=mariadb -e MYSQL_ROOT_PASSWORD=mypassword mariadb/server
f63034a1f205d00882cba109f4a131bcc8d24858113a2b58dc3c746535339211
[chandrashekhar@localhost Development]$ podman ps -a
CONTAINER ID  IMAGE                            COMMAND  CREATED        STATUS            PORTS                   NAMES
f63034a1f205  docker.io/mariadb/server:latest  mysqld   5 seconds ago  Up 3 seconds ago  0.0.0.0:3306->3306/tcp  mariadb

[chandrashekhar@localhost Development]$ podman exec -it mariadb bash
root@f63034a1f205:/# mariadb
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 6
Server version: 10.5.10-MariaDB-1:10.5.10+maria~bionic mariadb.org binary distribution

Copyright (c) 2000, 2018, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

MariaDB [(none)]> create database testdb
    -> ;
Query OK, 1 row affected (0.000 sec)


MariaDB [(none)]> use testdb;
Database changed
MariaDB [testdb]> create table item(id INT NOT NULL AUTO_INCREMENT, title VARCHAR(50) NOT NULL, description VARCHAR(200), price INT NOT NULL, create_date DATE, PRIMARY KEY(id)); 
Query OK, 0 rows affected (0.197 sec)

MariaDB [testdb]> insert into item values(1, "pencilbox", "plastic one side box", 40, '2021-06-12');
Query OK, 1 row affected (0.126 sec)
MariaDB [testdb]> 
MariaDB [testdb]> insert into item values(2, "eraser", "eraset", 5, '2021-06-11');
Query OK, 1 row affected (0.126 sec)

MariaDB [testdb]> insert into item values(3, "sharpener", "sharpener", 6, '2021-06-11');
Query OK, 1 row affected (0.352 sec)

MariaDB [testdb]> insert into item values(4, "pencil", "pencil", 3, '2021-06-13');
Query OK, 1 row affected (0.010 sec)

MariaDB [testdb]> select * from item;
+----+-----------+----------------------+-------+-------------+
| id | title     | description          | price | create_date |
+----+-----------+----------------------+-------+-------------+
|  1 | pencilbox | plastic one side box |    40 | 2021-06-12  |
|  2 | eraser    | eraset               |     5 | 2021-06-11  |
|  3 | sharpener | sharpener            |     5 | 2021-06-11  |
|  4 | pencil    | pencil               |     3 | 2021-06-13  |
+----+-----------+----------------------+-------+-------------+
4 rows in set (0.000 sec)


MariaDB [testdb]> select * from item where price >= 5;
+----+-----------+----------------------+-------+-------------+
| id | title     | description          | price | create_date |
+----+-----------+----------------------+-------+-------------+
|  1 | pencilbox | plastic one side box |    40 | 2021-06-12  |
|  2 | eraser    | eraset               |     5 | 2021-06-11  |
|  3 | sharpener | sharpener            |     5 | 2021-06-11  |
+----+-----------+----------------------+-------+-------------+
3 rows in set (0.001 sec)

CREATE PROCEDURE testdb.GetItems(IN cost INT)
BEGIN
	SELECT * from item where price > cost;
END

MariaDB [testdb]> SHOW PROCEDURE STATUS WHERE Db = 'testdb';
+--------+----------+-----------+---------+---------------------+---------------------+---------------+---------+----------------------+----------------------+--------------------+
| Db     | Name     | Type      | Definer | Modified            | Created             | Security_type | Comment | character_set_client | collation_connection | Database Collation |
+--------+----------+-----------+---------+---------------------+---------------------+---------------+---------+----------------------+----------------------+--------------------+
| testdb | GetItems | PROCEDURE | root@%  | 2021-06-12 07:23:43 | 2021-06-12 07:23:43 | DEFINER       |         | utf8mb4              | utf8mb4_general_ci   | utf8mb4_general_ci |
+--------+----------+-----------+---------+---------------------+---------------------+---------------+---------+----------------------+----------------------+--------------------+
1 row in set (0.003 sec)

MariaDB [testdb]> call GetItems(4);
+----+-----------+----------------------+-------+-------------+
| id | title     | description          | price | create_date |
+----+-----------+----------------------+-------+-------------+
|  1 | pencilbox | plastic one side box |    40 | 2021-06-12  |
|  2 | eraser    | eraset               |     5 | 2021-06-11  |
|  3 | sharpener | sharpener            |     6 | 2021-06-11  |
+----+-----------+----------------------+-------+-------------+
3 rows in set (0.001 sec)

Query OK, 0 rows affected (0.001 sec)

MariaDB [testdb]> call GetItems(5);
+----+-----------+----------------------+-------+-------------+
| id | title     | description          | price | create_date |
+----+-----------+----------------------+-------+-------------+
|  1 | pencilbox | plastic one side box |    40 | 2021-06-12  |
|  3 | sharpener | sharpener            |     6 | 2021-06-11  |
+----+-----------+----------------------+-------+-------------+
2 rows in set (0.001 sec)

MariaDB [(none)]> select * from  information_schema.routines where SPECIFIC_NAME="GetItems";
+---------------+-----------------+----------------+--------------+--------------+-----------+--------------------------+------------------------+-------------------+---------------+--------------------+--------------------+----------------+----------------+--------------+---------------------------------------------------+---------------+-------------------+-----------------+------------------+-----------------+----------+---------------+---------------------+---------------------+--------------------------------------------------------------------------------------------------------+-----------------+---------+----------------------+----------------------+--------------------+
| SPECIFIC_NAME | ROUTINE_CATALOG | ROUTINE_SCHEMA | ROUTINE_NAME | ROUTINE_TYPE | DATA_TYPE | CHARACTER_MAXIMUM_LENGTH | CHARACTER_OCTET_LENGTH | NUMERIC_PRECISION | NUMERIC_SCALE | DATETIME_PRECISION | CHARACTER_SET_NAME | COLLATION_NAME | DTD_IDENTIFIER | ROUTINE_BODY | ROUTINE_DEFINITION                                | EXTERNAL_NAME | EXTERNAL_LANGUAGE | PARAMETER_STYLE | IS_DETERMINISTIC | SQL_DATA_ACCESS | SQL_PATH | SECURITY_TYPE | CREATED             | LAST_ALTERED        | SQL_MODE                                                                                               | ROUTINE_COMMENT | DEFINER | CHARACTER_SET_CLIENT | COLLATION_CONNECTION | DATABASE_COLLATION |
+---------------+-----------------+----------------+--------------+--------------+-----------+--------------------------+------------------------+-------------------+---------------+--------------------+--------------------+----------------+----------------+--------------+---------------------------------------------------+---------------+-------------------+-----------------+------------------+-----------------+----------+---------------+---------------------+---------------------+--------------------------------------------------------------------------------------------------------+-----------------+---------+----------------------+----------------------+--------------------+
| GetItems      | def             | testdb         | GetItems     | PROCEDURE    |           |                     NULL |                   NULL |              NULL |          NULL |               NULL | NULL               | NULL           | NULL           | SQL          | BEGIN
	SELECT * from item where price > cost;
END | NULL          | NULL              | SQL             | NO               | CONTAINS SQL    | NULL     | DEFINER       | 2021-06-12 07:23:43 | 2021-06-12 07:23:43 | IGNORE_SPACE,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION |                 | root@%  | utf8mb4              | utf8mb4_general_ci   | utf8mb4_general_ci |
+---------------+-----------------+----------------+--------------+--------------+-----------+--------------------------+------------------------+-------------------+---------------+--------------------+--------------------+----------------+----------------+--------------+---------------------------------------------------+---------------+-------------------+-----------------+------------------+-----------------+----------+---------------+---------------------+---------------------+--------------------------------------------------------------------------------------------------------+-----------------+---------+----------------------+----------------------+--------------------+
1 row in set (0.004 sec)

MariaDB [(none)]> 



karaf@root()> install -s mvn:org.apache.commons/commons-dbcp2/2.1.1

karaf@root()> install -s mvn:org.apache.commons/commons-pool2/2.6.2
karaf@root()> feature:install camel-sql
karaf@root()> install -s mvn:org.mariadb.jdbc/mariadb-java-client/2.7.3
karaf@root()> install -s mvn:com.mycompany/sql-storedprocedure/1.0

Logs:
2021-06-13 09:07:22,949 | INFO  | ead #1 - timer://foo | cbr-route                        | 64 - org.apache.camel.camel-core - 2.23.2.fuse-780036-redhat-00001 | headers: {breadcrumbId=ID-localhost-localdomain-1623554741540-0-36, CamelSqlStoredUpdateCount=0, firedTime=Sun Jun 13 09:07:22 IST 2021, num1=6} Body: {#result-set-1=[{id=1, title=pencilbox, description=plastic one side box, price=40, create_date=2021-06-12}], #update-count-1=0}
```
