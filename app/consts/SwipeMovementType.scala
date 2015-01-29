package consts

object SwipeMovementType extends Enumeration {
  type SwipeMovementType = Value
  val UNKNOWN = Value("unknown")
  val INCOMING = Value("incoming")
  val OUTGOING = Value("outgoing")
  val TRANSITION = Value("transition")
}
