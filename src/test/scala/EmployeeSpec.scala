import edu.knoldus.connection.H2DBComponent
import edu.knoldus.repositories.{Employee, EmployeeRepo}
import org.scalatest.AsyncFunSuite

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class EmployeeSpec extends AsyncFunSuite with EmployeeRepo with H2DBComponent {

  test("1:should insert a record into the table") {
    val res = insert(Employee(6, "Aman", 2.5))
    res.map { x => assert(x == 1) }
  }


  test("should delete a record from the database") {
    val res = delete(4)
    res.map { x => assert(x==1) }
  }

  test("should update a record in the table (Updation in a single field") {
    val res = updateName(3,"Divya")
    res.map {x => assert(x == 1)}
  }

  test("should update a record in the table (Updation in a tuple)") {
    val res = EmployeeRepo.updateTuple(2,("Ishant",3.5))
    res.map{ x => assert(x == 1)}
  }

  test("should update or insert a record into the table") {
    val res = upsert(Employee(5,"Ajay",4.0))
    res.map{ x => assert(x == 1)}
  }

  test("should generate the list of employees") {
    val list = getAll
    assert(Await.result(list, Duration.Inf) ==  List(Employee(1,"Gitika",2.5), Employee(2,"Raman",2.0), Employee(3,"Anuj",3.0),
      Employee(4,"Jatin",3.5), Employee(5,"Anmol",4.0)))
  }

}
