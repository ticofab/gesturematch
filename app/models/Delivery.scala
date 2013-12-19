package models

case class Delivery(id: String, payload: String, chunk: Option[Int], totalChunks: Option[Int])
