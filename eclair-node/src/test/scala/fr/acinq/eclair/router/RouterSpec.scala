package fr.acinq.eclair.router

import akka.actor.Status.Failure
import akka.testkit.TestProbe
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
  * Created by PM on 29/08/2016.
  */
@RunWith(classOf[JUnitRunner])
class RouterSpec extends BaseRouterSpec {

  test("route not found (unreachable target)") { case (router, watcher) =>
    val sender = TestProbe()
    // no route a->f
    sender.send(router, RouteRequest(a, f))
    val res = sender.expectMsgType[Failure]
    assert(res.cause.getMessage === "route not found")
  }

  test("route not found (non-existing source)") { case (router, watcher) =>
    val sender = TestProbe()
    // no route a->f
    sender.send(router, RouteRequest(randomPubkey, f))
    val res = sender.expectMsgType[Failure]
    assert(res.cause.getMessage === "graph must contain the source vertex")
  }

  test("route not found (non-existing target)") { case (router, watcher) =>
    val sender = TestProbe()
    // no route a->f
    sender.send(router, RouteRequest(a, randomPubkey))
    val res = sender.expectMsgType[Failure]
    assert(res.cause.getMessage === "graph must contain the sink vertex")
  }

  test("route found") { case (router, watcher) =>
    val sender = TestProbe()
    sender.send(router, RouteRequest(a, d))
    val res = sender.expectMsgType[RouteResponse]
    assert(res.hops.map(_.nodeId).toList === a.toBin :: b.toBin :: c.toBin :: Nil)
    assert(res.hops.last.nextNodeId === d.toBin)
  }

    /*val finalAmountMsat = 42000000L
    val currentBlockCount = 420000

    val node_a = Globals.Node.publicKey
    val node_b = PrivateKey(BinaryData("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"), compressed = true).publicKey
    val node_c = PrivateKey(BinaryData("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"), compressed = true).publicKey
    val node_d = PrivateKey(BinaryData("dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"), compressed = true).publicKey

    // no route b -> c
    router ! ChannelDiscovered(ChannelDesc("01", node_a, node_b))
    router ! ChannelDiscovered(ChannelDesc("02", node_c, node_d))

    val paymentFsm = system.actorOf(PaymentLifecycle.props(router, selector, 1440))

    val monitor = TestProbe()
    paymentFsm ! SubscribeTransitionCallBack(monitor.ref)
    val CurrentState(_, WAITING_FOR_REQUEST) = monitor.expectMsgClass(classOf[CurrentState[_]])

    val sender = TestProbe()
    sender.send(paymentFsm, CreatePayment(42000000, BinaryData("00112233445566778899aabbccddeeff"), node_c))
    val Transition(_, WAITING_FOR_REQUEST, WAITING_FOR_ROUTE) = monitor.expectMsgClass(classOf[Transition[_]])
    sender.expectMsgType[Status.Failure]*/


  //TODO re-enable
  /*test("payment succeeded") {
    val router = system.actorOf(Props[Router])
    val selector = system.actorOf(Props[ChannelSelector])
    val channel00 = TestProbe()
    val channel01 = TestProbe()

    // network: aaaa -> bbbbbbb -> cccc
    val node_a = Globals.Node.publicKey
    val node_b = BinaryData("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
    val node_c = BinaryData("ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc")

    router ! ChannelDiscovered(ChannelDesc("01", node_a, node_b))
    router ! ChannelDiscovered(ChannelDesc("02", node_b, node_c))

    selector ! ChannelChangedState(channel00.ref, node_b, OPEN_WAIT_FOR_COMPLETE_OURFUNDING, NORMAL, DATA_NORMAL_2(0, Commitments(null, null, null, TheirCommit(0L, CommitmentSpec(Set(), 0L, 0L, 100000), null, null), null, null, 0L, null, null, null, null, null), null, null))
    selector ! ChannelChangedState(channel01.ref, node_b, OPEN_WAIT_FOR_COMPLETE_OURFUNDING, NORMAL, DATA_NORMAL(Commitments(null, null, null, TheirCommit(0L, CommitmentSpec(Set(), 0L, 0L, 100000000), null, null), null, null, 0L, null, null, null, null, null), null, null))

    val paymentFsm = system.actorOf(PaymentLifecycle.props(router, selector, 1440))

    val monitor = TestProbe()
    paymentFsm ! SubscribeTransitionCallBack(monitor.ref)
    val CurrentState(_, WAITING_FOR_REQUEST) = monitor.expectMsgClass(classOf[CurrentState[_]])

    val sender = TestProbe()
    val req = CreatePayment(42000000, BinaryData("00112233445566778899aabbccddeeff"), node_c)
    sender.send(paymentFsm, req)
    val Transition(_, WAITING_FOR_REQUEST, WAITING_FOR_ROUTE) = monitor.expectMsgClass(classOf[Transition[_]])
    val Transition(_, WAITING_FOR_ROUTE, WAITING_FOR_CHANNEL) = monitor.expectMsgClass(classOf[Transition[_]])
    val Transition(_, WAITING_FOR_CHANNEL, WAITING_FOR_PAYMENT_COMPLETE) = monitor.expectMsgClass(classOf[Transition[_]])
    channel01.expectMsgType[CMD_ADD_HTLC]
    sender.send(paymentFsm, PaymentSent(channel01.ref, req.h))
    sender.expectMsg("sent")

  }*/

  //TODO re-enable
  /*test("payment failed") {
    val router = system.actorOf(Props[Router])
    val selector = system.actorOf(Props[ChannelSelector])
    val channel00 = TestProbe()
    val channel01 = TestProbe()

    // network: aaaa -> bbbbbbb -> cccc
    val node_a = Globals.Node.publicKey
    val node_b = BinaryData("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
    val node_c = BinaryData("ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc")

    router ! ChannelDiscovered(ChannelDesc("01", node_a, node_b))
    router ! ChannelDiscovered(ChannelDesc("02", node_b, node_c))

    selector ! ChannelChangedState(channel00.ref, node_b, OPEN_WAIT_FOR_COMPLETE_OURFUNDING, NORMAL, DATA_NORMAL(Commitments(null, null, null, TheirCommit(0L, CommitmentSpec(Set(), 0L, 0L, 100000), null, null), null, null, 0L, null, null, null, null, null), null, null))
    selector ! ChannelChangedState(channel01.ref, node_b, OPEN_WAIT_FOR_COMPLETE_OURFUNDING, NORMAL, DATA_NORMAL(Commitments(null, null, null, TheirCommit(0L, CommitmentSpec(Set(), 0L, 0L, 100000000), null, null), null, null, 0L, null, null, null, null, null), null, null))

    val paymentFsm = system.actorOf(PaymentLifecycle.props(router, selector, 1440))

    val monitor = TestProbe()
    paymentFsm ! SubscribeTransitionCallBack(monitor.ref)
    val CurrentState(_, WAITING_FOR_REQUEST) = monitor.expectMsgClass(classOf[CurrentState[_]])

    val sender = TestProbe()
    val req = CreatePayment(42000000, BinaryData("00112233445566778899aabbccddeeff"), node_c)
    sender.send(paymentFsm, req)
    val Transition(_, WAITING_FOR_REQUEST, WAITING_FOR_ROUTE) = monitor.expectMsgClass(classOf[Transition[_]])
    val Transition(_, WAITING_FOR_ROUTE, WAITING_FOR_CHANNEL) = monitor.expectMsgClass(classOf[Transition[_]])
    val Transition(_, WAITING_FOR_CHANNEL, WAITING_FOR_PAYMENT_COMPLETE) = monitor.expectMsgClass(classOf[Transition[_]])
    channel01.expectMsgType[CMD_ADD_HTLC]
    sender.send(paymentFsm, PaymentFailed(channel01.ref, req.h, "some reason"))
    sender.expectMsgType[Status.Failure]
  }*/

}
