package hk.edu.polyu.af.bc.badge.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.lib.tokens.workflows.flows.rpc.CreateEvolvableTokens
import com.r3.corda.lib.tokens.workflows.flows.rpc.UpdateEvolvableToken
import hk.edu.polyu.af.bc.badge.states.BadgeClass
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StartableByService
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction

/**
 * Update a [BadgeClass].
 *
 * This flow is used to update an existed BadgeClass
 *
 * @property badgeClassRef the reference of an existed BadgeClass
 * @property name name of this type of badges
 * @property description a short description of achievement denoted by this type of badges
 * @property image the byte code of image
 * @property observers observer parties (optional)
 */

@StartableByRPC
@StartableByService
class UpdateBadgeClass(
    private val badgeClassRef: StateAndRef<BadgeClass>,
    private val name: String,
    private val description: String,
    private val image: ByteArray,
    private val observers: List<AbstractParty> = listOf()
): FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val badgeClass = badgeClassRef.state.data
        val updateBadgeClass = BadgeClass(name,description,image,ourIdentity,badgeClass.linearId)
        return subFlow(UpdateEvolvableToken(badgeClassRef,updateBadgeClass, observers as List<Party>))
    }
}