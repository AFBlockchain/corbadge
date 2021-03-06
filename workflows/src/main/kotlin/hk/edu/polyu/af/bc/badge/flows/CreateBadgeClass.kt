package hk.edu.polyu.af.bc.badge.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.TransactionState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker

/**
 * Create a [BadgeClass].
 *
 * This flow is initiated by the issuer. Optionally, provide a list of observers, who will also see the state (refer to
 * the observers in [CreateEvolvableTokens]).
 *
 * @property name name of this type of badges
 * @property description a short description of achievement denoted by this type of badges
 * @property observers observer parties (optional)
 */
@StartableByRPC
@StartableByService
class CreateBadgeClass(
        private val name: String,
        private val description: String,
        private val observers: List<AbstractParty> = listOf()
): FlowLogic<SignedTransaction>() {
    /**
     * The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
     * checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call() function.
     */
    companion object {
        object GENERATING_TRANSACTION : ProgressTracker.Step("Generating transaction based on new BadgeClass.")

        object FINALISING_TRANSACTION : ProgressTracker.Step("Create evolvable token")


        fun tracker() = ProgressTracker(
            GENERATING_TRANSACTION,
            FINALISING_TRANSACTION
        )
    }

    override val progressTracker = tracker()

    @Suspendable
    override fun call(): SignedTransaction {
        //get the corda default notary
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        //step 1 create new transation and BadgeClass
        progressTracker.currentStep = GENERATING_TRANSACTION
        val newBadgeClass = BadgeClass(name, description, ourIdentity, UniqueIdentifier())

        val transactionState = TransactionState<BadgeClass>(newBadgeClass, notary = notary)


        //Step 2 create evolvable token
        progressTracker.currentStep = FINALISING_TRANSACTION
        return (subFlow(CreateEvolvableTokens(transactionState = transactionState, observers = observers as List<Party>)))

    }
}
