package zeebe.workers.order

import javax.inject.Singleton

/**
 * Dumb statistics about orders
 */
@Singleton
class OrderMeta {
  var paymentProcessed = 0
  var returnedPayment = 0
  var assembledOrders = 0
  var shippedOrders = 0
  var latestPaymentProcessed = 0
}
