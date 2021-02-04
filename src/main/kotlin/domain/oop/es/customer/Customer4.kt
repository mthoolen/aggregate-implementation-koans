package domain.oop.es.customer

import domain.shared.command.ChangeCustomerEmailAddress
import domain.shared.command.ConfirmCustomerEmailAddress
import domain.shared.command.RegisterCustomer
import domain.shared.event.CustomerEmailAddressChanged
import domain.shared.event.CustomerEmailAddressConfirmationFailed
import domain.shared.event.CustomerEmailAddressConfirmed
import domain.shared.event.CustomerRegistered
import domain.shared.event.Event
import domain.shared.value.EmailAddress
import domain.shared.value.Hash
import domain.shared.value.PersonName
import java.util.*

class Customer4 private constructor() {
    private var emailAddress: EmailAddress? = null
    private var confirmationHash: Hash? = null
    private var isEmailAddressConfirmed = false
    private var name: PersonName? = null
    private val recordedEvents: MutableList<Event>

    fun confirmEmailAddress(command: ConfirmCustomerEmailAddress) {
        when {
            confirmationHash != command.confirmationHash -> {
                return recordThat(CustomerEmailAddressConfirmationFailed.build(command.customerID))
            }
            isEmailAddressConfirmed -> {
                return
            }
            else -> recordThat(CustomerEmailAddressConfirmed.build(command.customerID))
        }
    }

    fun changeEmailAddress(command: ChangeCustomerEmailAddress) {
        if (emailAddress == command.emailAddress) {
            return
        }
        return recordThat(
            CustomerEmailAddressChanged.build(
                command.customerID,
                command.emailAddress,
                command.confirmationHash
            )
        )
    }

    fun getRecordedEvents(): List<Event> {
        return recordedEvents
    }

    private fun recordThat(event: Event) {
        recordedEvents.add(event)
    }

    fun apply(events: List<Event>) {
        for (event in events) {
            apply(event)
        }
    }

    fun apply(event: Event) {
        when (event) {
            is CustomerRegistered -> {
                emailAddress = event.emailAddress
                confirmationHash = event.confirmationHash
            }
            is CustomerEmailAddressConfirmed -> {
                isEmailAddressConfirmed = true
            }
            is CustomerEmailAddressChanged -> {
                emailAddress = event.emailAddress
                confirmationHash = event.confirmationHash
                isEmailAddressConfirmed = false
            }
        }
    }

    companion object {
        fun register(command: RegisterCustomer): Customer4 {
            return Customer4().apply {
                confirmationHash = command.confirmationHash
                emailAddress = command.emailAddress
                recordThat(
                    CustomerRegistered.build(
                        command.customerID,
                        command.emailAddress,
                        command.confirmationHash,
                        command.name
                    )
                )
            }
        }

        fun reconstitute(events: List<Event>): Customer4 {
            val customer = Customer4()
            customer.apply(events)
            return customer
        }
    }

    init {
        recordedEvents = ArrayList()
    }
}