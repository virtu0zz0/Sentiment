package core

import akka.actor.{Props, ActorSystem}
import scala.annotation.tailrec
import spray.can.Http

object Main extends App {
  import Commands._

  val system = ActorSystem()
  val sentiment = system.actorOf(Props(new SentimentAnalysisActor with CSVLoadedSentimentSets with AnsiConsoleSentimentOutput))
  val stream = system.actorOf(Props(new TweetStreamerActor(TweetStreamerActor.twitterUri, sentiment) with OAuthTwitterAuthorization))

  @tailrec
  private def commandLoop(): Unit = {
    Console.readLine() match {
      case QuitCommand         => return
      case TrackCommand(query) => stream ! query
      case StopCommand => stream ! Http.Close
      case _                   => println("WTF??!!")
    }
    commandLoop()
  }

  // start processing the commands
  commandLoop()

}

object Commands {

  val QuitCommand   = "quit"
  val StopCommand   = "stop"
  val TrackCommand = "track (.*)".r

}
