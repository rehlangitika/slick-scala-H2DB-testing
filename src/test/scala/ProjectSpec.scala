import edu.knoldus.connection.H2DBComponent
import edu.knoldus.repositories.{Employee, Project, ProjectRepo}
import org.scalatest.AsyncFunSuite

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ProjectSpec extends AsyncFunSuite with ProjectRepo with H2DBComponent {

  test("should insert a record into the table") {
    val res = insert(Project(5, "Royal Carribean", 2, 4, "Ayush"))
    res.map { x => assert(x == 1) }
  }

  test("should delete a record from the table") {
    val res = delete(3)
    res.map { x => assert(x == 1) }
  }

  test("should update a record in the table") {
    val res = updateName(2, "Bank Of America")
    res.map { x => assert(x == 1) }
  }

  test("should update a record's tuple in the table") {
    val res = updateTuple(4, ("Logam", 1, 5, "Prashant"))
    res.map { x => assert(x == 1) }
  }

  test("should update(if already present) or insert(if not present) a record into the table") {
    val res = upsert(Project(6, "Huaweii", 2, 4, "Ayush"))
    res.map { x => assert(x == 1) }
  }

  test("should return project with the employee record") {
    val list = getProjectWithEmployee
    assert(Await.result(list, Duration.Inf) == List((Employee(1, "Gitika", 2.5), Project(1, "Carbon Data", 1, 6, "Bhavya")),
      (Employee(2, "Raman", 2.0), Project(2, "Free Bird", 2, 6, "Prashant")), (Employee(3, "Anuj", 3.0),
        Project(3, "3TL", 3, 8, "Siddharth")), (Employee(1, "Gitika", 2.5),
        Project(4, "Carbon Data", 1, 6, "Bhavya"))))
  }

  test("should return project name along with the employee") {
    val list = getProjectBasedOnEmpName("Gitika")
    assert(Await.result(list, Duration.Inf) == List(("Gitika", "Carbon Data"), ("Gitika", "Carbon Data")))
  }

  test("should return an average team size") {
    val size = getAvgTeamSize
    size.map { x => assert(x == Some(6)) }
  }

  test("should insert a record into the table using plain sql") {
    val res = insertProject
    res.map { x => assert(x == 1) }
  }

}
