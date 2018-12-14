package zeebe.workers.card

import javax.inject.Singleton

@Singleton
class CardMeta {
  var kycDone = 0
  var scoreDone = 0
  var createContractDone = 0
  var msgDone = 0
  var scoreRateDone = 0
  var issueCardDone = 0
  var contractConfirmDone = 0

}
