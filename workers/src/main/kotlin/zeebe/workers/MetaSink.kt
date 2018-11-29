package zeebe.workers

import javax.inject.Singleton

@Singleton
class MetaSink {
  var paymentProcessed = 0
  var returnedPayment = 0
  var assembledOrders = 0
  var shippedOrders = 0
}
