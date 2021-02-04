package domain.functional.es.customer

import domain.shared.event.CustomerEmailAddressChanged
import domain.shared.event.CustomerEmailAddressConfirmed
import domain.shared.event.CustomerRegistered
import domain.shared.event.Event
import domain.shared.value.EmailAddress
import domain.shared.value.Hash
import domain.shared.value.PersonName

class CustomerState private constructor() {
    var emailAddress: EmailAddress? = null
    var confirmationHash: Hash? = null
    var name: PersonName? = null
    var isEmailAddressConfirmed = false

    fun apply(events: List<Event>) {
        for (event in events) {
            if (event is CustomerRegistered) {
                emailAddress = event.emailAddress
                confirmationHash = event.confirmationHash
                continue
            }
            if (event is CustomerEmailAddressConfirmed) {
                isEmailAddressConfirmed = true
                continue
            }
            if (event is CustomerEmailAddressChanged) {
                emailAddress = event.emailAddress
                confirmationHash = event.confirmationHash
                isEmailAddressConfirmed = false
            }
        }
    }

    companion object {
        fun reconstitute(events: List<Event>): CustomerState {
            val customer = CustomerState()
            customer.apply(events)
            return customer
        }
    }
}