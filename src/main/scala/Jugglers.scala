import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }
import scala.concurrent.duration._
import scala.util.Random
import akka.actor.ActorLogging
import akka.event.LoggingReceive
import akka.event.Logging
import akka.actor.PoisonPill

// messages . . .
case class Partners(partners:Seq[ActorRef])
case object Ball
case object Count
case class Thrown(n:Int)

class Juggler extends Actor with ActorLogging {
  val random = new Random
  
  var partners:Seq[ActorRef] = null
  def randomPartner = partners(random.nextInt(partners.size))

  var throwCounter = 0
  
  def receive = waiting
  
  def waiting:Receive = LoggingReceive {
    case Partners(partners) => {
      this.partners = partners
      context.become(juggling)
    }
  }
  
  def juggling:Receive = LoggingReceive {
    case Ball =>
      throwCounter += 1
      randomPartner ! Ball
      
    case Count => 
      sender ! Thrown(throwCounter)
      throwCounter = 0
  }
}



object Jugglers extends App {
  
  def injectBalls(jugglers: Seq[ActorRef], number: Int):Unit = {
    (1 to number) foreach { i => Unit
      jugglers(0).tell(Ball, ActorRef.noSender)
    }
  }

  
  def reportCounts(jugglers: Seq[ActorRef]):Unit = {
    for (juggler <- jugglers) {
      inbox.send(juggler, Count)
    }

    val counts = for {i <- 1 to jugglers.size
	    val m = inbox.receive(100.millis)
	    val n = m match {
	      case Thrown(n) => n
	    }
    } yield n
    
    val sum = counts.sum
    println(s"thrown: $sum")
  }

  
  
  // Create the 'jugglers' actor system
  val system = ActorSystem("jugglers")

  val processors = Runtime.getRuntime().availableProcessors()
  
  // Create the 'Juggler' actors
  val jugglers = for (i <- 1 to processors*2) yield system.actorOf(Props[Juggler], s"juggler-$i") 
  
  // Create an "actor-in-a-box"
  val inbox = Inbox.create(system)

  // Tell the jugglers who their partners are
  jugglers foreach {juggler => juggler ! Partners(jugglers)}

  val iterations = 20
  val ballCount = jugglers.size
  println(s"Running with ${jugglers.size} jugglers thru ${iterations} iterations, injecting ${ballCount} balls each round.")
  
  // start the juggling
  injectBalls(jugglers, ballCount)
 
  // let hotspot do its work
  for (i <- 1 to iterations) {
    Thread.sleep(1000)
    reportCounts(jugglers)
    injectBalls(jugglers, ballCount)
  }
  
  jugglers foreach {juggler => juggler ! PoisonPill}
  system.shutdown
}
