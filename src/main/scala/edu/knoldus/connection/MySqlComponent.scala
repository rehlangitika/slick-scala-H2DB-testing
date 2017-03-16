package edu.knoldus.connection

import slick.jdbc.MySQLProfile

trait MySqlComponent extends DBComponent {

  val driver = MySQLProfile

  import driver.api._

  val db = Database.forConfig("mySqlDB")

}
