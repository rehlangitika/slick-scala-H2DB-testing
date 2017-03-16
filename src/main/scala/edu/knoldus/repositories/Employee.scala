package edu.knoldus.repositories

import edu.knoldus.connection.{DBComponent, H2DBComponent}

import scala.concurrent.Future


case class Employee(id: Int, name: String, experience: Double)

trait EmployeeTable {

  this: DBComponent =>

  import driver.api._

  private[repositories] class EmployeeTable(tag: Tag) extends Table[Employee](tag, "experienced_employee") {

    val id = column[Int]("id", O.PrimaryKey)
    val name = column[String]("name")
    val experience = column[Double]("experience")

    def * = (id, name, experience) <> (Employee.tupled, Employee.unapply)
  }

  protected val employeeTableQuery = TableQuery[EmployeeTable]

  protected def employeeTableAutoInc = employeeTableQuery returning employeeTableQuery.map(_.id)

}

trait EmployeeRepo extends EmployeeTable {

  this: DBComponent =>

  import driver.api._

  /*
  * creating schema for the given table
  * */

  def create(): Future[Unit] = db.run(employeeTableQuery.schema.create)

  /*
  * inserting a record
  * */

  def insert(emp: Employee): Future[Int] = db.run {
    employeeTableQuery += emp
  }

  /*
  * deleting a record
  * */

  def delete(experience: Double): Future[Int] = {
    val query = employeeTableQuery.filter(e => e.experience === experience).delete
    db.run(query)
  }

  /*
  * Updation of the name field of the record for the given id
  * */

  def updateName(id: Int, name: String): Future[Int] = {
    val query = employeeTableQuery.filter(_.id === id).map(_.name).update(name)
    db.run(query)
  }

  /*
  * Insertion(if record is not present) or updation(if existing record)
  * */

  def upsert(employee: Employee): Future[Int] = {
    val query = employeeTableQuery.insertOrUpdate(employee)
    employeeTableQuery += employee
    db.run(query)
  }

  /*
  * Retrieving all the records in the Table
  * */

  def getAll: Future[List[Employee]] = db.run {
    employeeTableQuery.to[List].result
  }

  /*
  * Updation of tuple in the record with the given id
  * */

  def updateTuple(id: Int, values: (String, Double)): Future[Int] = {
    val query = employeeTableQuery.filter(_.id === id).map(e => (e.name, e.experience)).update(values)
    db.run(query)
  }

  def nextId(employee: Employee): Future[Int] = db.run {
    employeeTableAutoInc += employee
  }

  /*
  * Get Average employee experience
  * */

  def getAvgEmpExp: Future[Option[Double]] = db.run {
    val exps = employeeTableQuery.map(_.experience)
    exps.max.result
  }

  /*
  * inserting an employee using plain sql
  * */

  def insertEmployee: Future[Int] = db.run {
    sqlu"insert into experienced_employee values(6,'Nikita',3.5)"
  }

}

object EmployeeRepo extends EmployeeRepo with H2DBComponent

