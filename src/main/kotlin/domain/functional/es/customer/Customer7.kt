package domain.functional.es.customer

import domain.shared.command.ChangeCustomerEmailAddress
import domain.shared.command.ConfirmCustomerEmailAddress
import domain.shared.command.RegisterCustomer
import domain.shared.event.CustomerEmailAddressChanged
import domain.shared.event.CustomerEmailAddressConfirmationFailed
import domain.shared.event.CustomerEmailAddressConfirmed
import domain.shared.event.CustomerRegistered
import domain.shared.event.CustomerRegistered.Companion.build
import domain.shared.event.Event

object Customer7 {

    fun register(command: RegisterCustomer): CustomerRegistered? {
        return build(command.customerID, command.emailAddress, command.confirmationHash, command.name)

    }

    fun confirmEmailAddress(current: CustomerState, command: ConfirmCustomerEmailAddress): List<Event> {

        if (current.confirmationHash != command.confirmationHash) {
            return listOf(CustomerEmailAddressConfirmationFailed.build(command.customerID))
        }
        if (current.isEmailAddressConfirmed) {
            return emptyList()
        }

        return listOf(CustomerEmailAddressConfirmed.build(command.customerID))
    }

    fun changeEmailAddress(current: CustomerState, command: ChangeCustomerEmailAddress): List<Event> {
        if (current.emailAddress == command.emailAddress) {
            return emptyList()
        }

        return listOf(
            CustomerEmailAddressChanged.build(
                command.customerID,
                command.emailAddress,
                command.confirmationHash
            )
        )

    }
}