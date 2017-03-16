package edu.knoldus.repositories

import edu.knoldus.connection.{DBComponent, PostgresComponent}

import scala.concurrent.Future

case class Project(projId: Int, name: String, empId: Int, teamSize: Int, teamLead: String = "Vikas")

trait ProjectTable extends EmployeeTable {

  this: DBComponent =>

  import driver.api._

  private[repositories] class ProjectTable(tag: Tag) extends Table[Project](tag, "project") {

    val projId = column[Int]("projid", O.PrimaryKey)
    val name = column[String]("name")
    val empId = column[Int]("empId")
    val teamSize = column[Int]("teamsize")
    val teamLead = column[String]("teamlead")

    def employeeProjectFK = foreignKey("employee_project_fk", empId, employeeTableQuery)(_.id)

    def * = (projId, name, empId, teamSize, teamLead) <> (Project.tupled, Project.unapply)
  }

  protected val projectTableQuery = TableQuery[ProjectTable]

  protected def projectTableAutoInc: driver.ReturningInsertActionComposer[Project, Int] = projectTableQuery returning projectTableQuery.map(_.projId)

}

trait ProjectRepo extends ProjectTable {

  this: DBComponent =>

  import driver.api._

  /*
  * creating schema for the given table
  * */

  def create(): Future[Unit] = db.run(projectTableQuery.schema.create)

  /*
  * inserting a record
  * */

  def insert(proj: Project): Future[Int] = db.run {
    projectTableQuery += proj
  }

  /*
  * deleting a record
  * */

  def delete(id: Int): Future[Int] = {
    val query = projectTableQuery.filter(p => p.projId === id)
    val action = query.delete
    db.run(action)
  }

  /*
  * Updation of the name field of the record for the given id
  * */

  def updateName(id: Int, name: String): Future[Int] = {
    val query = projectTableQuery.filter(_.projId === id).map(_.name).update(name)
    db.run(query)
  }

  /*
  * Insertion(if record is not present) or updation(if existing record)
  * */

  def upsert(project: Project): Future[Int] = {
    val query = projectTableQuery.insertOrUpdate(project)
    projectTableQuery += project
    db.run(query)
  }

  /*
  * Retrieving all the records in the Table
  * */

  def getAll: Future[List[Project]] = db.run {
    projectTableQuery.to[List].result
  }

  /*
  * Updation of tuple in the record with the given id
  * */

  def updateTuple(id: Int, values: (String, Int, Int, String)): Future[Int] = {
    val query = projectTableQuery.filter(_.projId === id).map(p => (p.name, p.empId, p.teamSize, p.teamLead)).update(values)
    db.run(query)
  }

  /*
  * Retrieving the Project details along with Employee details
  * */

  def getProjectWithEmployee: Future[List[(Employee, Project)]] = db.run {
    (for {
      record <- projectTableQuery
      employee <- record.employeeProjectFK
    } yield (employee, record)).to[List].result
  }

  /*
  * Get Project names for the given Employee
  * */

  def getProjectBasedOnEmpName(name: String): Future[List[(String, String)]] = db.run {
    (for {
      (e, p) <- employeeTableQuery join projectTableQuery on (_.id === _.empId) if e.name === name
    } yield (e.name, p.name)).to[List].result
  }

  /*
  * Get average team size of the projects
  * */

  def getAvgTeamSize: Future[Option[Int]] = db.run {
    val projects = projectTableQuery.map(_.teamSize)
    projects.avg.result
  }

  /*
  * Insert project record using pain sql
  * */

  def insertProject: Future[Int] = db.run {
    sqlu"insert into project values(6,'EY',3,6,'Himanshu')"
  }

}

object ProjectRepo extends ProjectRepo with PostgresComponent


