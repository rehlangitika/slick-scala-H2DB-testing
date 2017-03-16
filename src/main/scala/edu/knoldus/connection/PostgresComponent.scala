package edu.knoldus.connection

import slick.jdbc.PostgresProfile

trait PostgresComponent extends DBComponent {

  val driver = PostgresProfile

  import driver.api._

  val db = Database.forConfig("myPostgresDB")

}
