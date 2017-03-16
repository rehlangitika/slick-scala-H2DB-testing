import edu.knoldus.connection.H2DBComponent
import edu.knoldus.repositories.{Dependent, DependentRepo, Employee}
import org.scalatest.AsyncFunSuite

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DependentSpec extends AsyncFunSuite with DependentRepo with H2DBComponent  {

  test("should insert a record into the database") {
    val res = insert(Dependent(2,2,"Manvi","wife",Some(24)))
    res.map { x => assert(x == 1) }
  }

  test("should delete a record from the database") {
    val res = delete(2)
    res.map{ x => assert(x==1)}
  }

  test("should update a record from the database") {
    val res = updateName(3,"Akash")
    res.map{x => assert(x == 1)}
  }

  test("should update a record in the table (Updation in a tuple)") {
    val res = updateTuple(3,("Kritika",3,"sister",Some(20)))
    res.map{ x => assert(x == 1)}
  }

  test("should update or insert a record into the table") {
    val res = upsert(Dependent(1,3,"Arjun","brother",Some(23)))
    res.map{ x => assert(x == 1)}
  }

  test("should generate the List of Dependents") {
    val list = getAll
    assert(Await.result(list,Duration.Inf) == List(Dependent(1,1,"Kamal","mother",Some(50)),
      Dependent(2,1,"Amit","brother",Some(20)), Dependent(3,2,"Aman","brother",Some(25)),
      Dependent(4,3,"Deepti","sister",Some(26))))
  }

  test("should return a list of employee and his dependent") {
    val list = getDependentWithEmployee
    assert(Await.result(list, Duration.Inf) == List((Employee(1,"Gitika",2.5),Dependent(1,1,"Kamal","mother",Some(50))),
      (Employee(1,"Gitika",2.5),Dependent(2,1,"Amit","brother",Some(20))), (Employee(2,"Raman",2.0),
        Dependent(3,2,"Aman","brother",Some(25))), (Employee(3,"Anuj",3.0),
        Dependent(4,3,"Deepti","sister",Some(26)))))
  }

  test("should return a list of dependent based on employee experience") {
    val list = getDependentBasedOnEmpExp(2.5)
    assert(Await.result(list, Duration.Inf) == List(("Gitika","Kamal"), ("Gitika","Amit")))
  }

  test("should insert a dependent using plain sql") {
    val res = insertDependent
    res.map { x => assert(x==1)}
  }

}
