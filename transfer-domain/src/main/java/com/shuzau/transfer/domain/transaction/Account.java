package com.shuzau.transfer.domain.transaction;

import com.shuzau.transfer.domain.core.Money;
import com.shuzau.transfer.domain.secondary.TransactionRepository;
import com.shuzau.transfer.domain.transfer.TransferId;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static com.shuzau.transfer.domain.core.Validators.multiValidator;
import static io.vavr.control.Validation.invalid;
import static io.vavr.control.Validation.valid;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public class Account {

    private final AccountId id;
    private final TransactionRepository transactionRepository;
    private Transaction latestTransaction;

    public static Builder from(@NonNull Transaction transaction) {
        return new FromTransactionBuilder(transaction);
    }

    public static Builder newAccount(@NonNull Money balance) {
        return new NewAccountBuilder(balance);
    }

    private Validation<Seq<String>, Money> validateTransaction(Money amount) {
        return validatePositiveAmount(amount).combine(validateCurrency(amount))
                                             .ap(Function2.constant(amount));
    }

    private Validation<String, Money> validateCurrency(Money amount) {
        return latestTransaction.getAmount().validateCurrency(amount);
    }


    public Validation<Seq<String>, Money> validateDeposit(@NonNull Money amount) {
        return validateTransaction(amount);
    }

    public Validation<Seq<String>, Money> validateWithdraw(@NonNull Money amount) {
        return validatePositiveAmount(amount).combine(validateCurrency(amount))
                                             .combine(validateSufficientFunds(amount))
                                             .ap(Function3.constant(amount));
    }

    public Validation<Seq<String>, Money> validateTransfer(@NonNull Money amount) {
        if (amount.isNegative()) {
            return validateWithdraw(amount.negate());
        } else {
            return validateDeposit(amount);
        }
    }

    public void withdraw(Money amount) {
        multiValidator().accept(validateWithdraw(amount));

        latestTransaction = transactionRepository.save(latestTransaction.nextTransaction(amount.negate())
                                                                        .withId(transactionRepository.nextTransactionId()));
    }

    public void deposit(Money amount) {
        multiValidator().accept(validateDeposit(amount));

        latestTransaction = transactionRepository.save(latestTransaction.nextTransaction(amount)
                                                                        .withId(transactionRepository.nextTransactionId()));
    }

    public void transfer(@NonNull TransferId transferId, Money amount) {
        multiValidator().accept(validateTransfer(amount));

        latestTransaction = transactionRepository.save(latestTransaction.nextTransferTransaction(transferId, amount)
                                                                        .withId(transactionRepository.nextTransactionId()));
    }

    public Money getBalance() {
        Transaction currentTransaction = latestTransaction;
        Money balance = currentTransaction.getAmount();
        while (!currentTransaction.isInitial()) {
            currentTransaction = currentTransaction.getPreviousTransaction();
            balance = balance.add(currentTransaction.getAmount());
        }
        return balance;
    }

    private Validation<String, Money> validatePositiveAmount(Money amount) {
        return amount.isNegative() ? invalid("Amount can't be negative: " + amount) : valid(amount);
    }

    private Validation<String, Money> validateSufficientFunds(Money amount) {
        Money diff = getBalance().subtract(amount);
        return diff.isNegative() ? invalid("Insufficient funds: " + diff) : valid(amount);
    }

    public interface Builder {

        Account withRepository(@NonNull TransactionRepository transactionRepository);
    }

    private static class FromTransactionBuilder implements Builder {

        private final Transaction latestTransaction;

        private FromTransactionBuilder(Transaction transaction) {
            latestTransaction = transaction;
        }

        @Override
        public Account withRepository(@NonNull TransactionRepository transactionRepository) {
            Account account = new Account(latestTransaction.getAccountId(), transactionRepository);
            account.latestTransaction = latestTransaction;
            return account;
        }
    }

    private static class NewAccountBuilder implements Builder {

        private final Money balance;

        private NewAccountBuilder(Money balance) {
            this.balance = balance;
        }

        @Override
        public Account withRepository(@NonNull TransactionRepository transactionRepository) {
            AccountId accountId = transactionRepository.newAccountId();
            Account account = new Account(accountId, transactionRepository);
            account.latestTransaction = transactionRepository.save(Transaction.createNewAccountTransaction(accountId, balance)
                                                                              .withId(transactionRepository.nextTransactionId()));
            return account;
        }
    }
}