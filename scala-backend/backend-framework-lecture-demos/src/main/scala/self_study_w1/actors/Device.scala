package self_study_w1.actors

import akka.actor.{ Actor, ActorLogging, Props }

object Device {
  def props(groupId: String, deviceId: String): Props = Props(new Device(groupId, deviceId))

  final case class ReadTemperature(requestId: Long)
  final case class RespondTemperature(requestId: Long, value: Option[Double])
}

class Device(groupId: String, deviceId: String) extends Actor with ActorLogging {
  import Device._

  var lastTemperatureReading: Option[Double] = None

  override def preStart(): Unit = log.info("Device actor {}-{} started", groupId, deviceId)
  override def postStop(): Unit = log.info("Device actor {}-{} stopped", groupId, deviceId)

  override def receive: Receive = {
    case ReadTemperature(id) =>
      sender() ! RespondTemperature(id, lastTemperatureReading)
  }

}